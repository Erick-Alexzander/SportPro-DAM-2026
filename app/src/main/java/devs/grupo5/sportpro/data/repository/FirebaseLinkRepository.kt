package devs.grupo5.sportpro.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import devs.grupo5.sportpro.data.model.LinkClaim
import devs.grupo5.sportpro.data.model.LinkInvitation
import devs.grupo5.sportpro.data.model.PlayerAccountLink
import kotlinx.coroutines.tasks.await
import java.security.SecureRandom
import java.util.Base64

class FirebaseLinkRepository private constructor() : LinkRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private fun generateSecureInviteCode(): String {
        // Genera 128-bits de entropía mediante un mecanismo criptográficamente fuerte.
        val randomBytes = ByteArray(16)
        SecureRandom().nextBytes(randomBytes)
        // Usa codificación Base64 URL-safe para que sea fácil transmitirlo sin corrupciones de string.
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes)
    }

    override suspend fun createInvitation(playerId: String): Result<LinkInvitation> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))

        return try {
            // Verificar primero si se tiene control de la ficha (Propiedad)
            val playerDoc = firestore.collection("players").document(playerId).get().await()
            if (!playerDoc.exists() || playerDoc.getString("ownerUid") != uid) {
                return Result.failure(Exception("No posees los derechos de administración sobre este jugador."))
            }

            val inviteId = generateSecureInviteCode()
            val now = System.currentTimeMillis()
            // Se utiliza 23 horas (en lugar de 24) para dar un margen de tolerancia (clock skew)
            // de hasta 1 hora en caso de que el reloj local del dispositivo esté adelantado
            // respecto al reloj del servidor de Firebase, evitando rechazo de reglas.
            val expiresAt = now + (23 * 60 * 60 * 1000L)

            val invitation = LinkInvitation(
                inviteId = inviteId,
                playerId = playerId,
                trainerUid = uid,
                createdAt = now,
                expiresAt = expiresAt,
                status = "PENDING"
            )

            // Emitir documento. (Cloud rules bloquea si expiresAt > request.time + 24H)
            firestore.collection("linkInvitations").document(inviteId).set(invitation).await()

            Result.success(invitation)
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Error al generar invitación segura."))
        }
    }

    override suspend fun submitClaim(inviteCode: String): Result<LinkClaim> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))

        return try {
            // Validaciones previas para entregar mensajes claros sin revelar data.
            // Para poder realizar este catch preventivo, recordamos que la Rule permite al auth 'get'.
            val inviteDoc = firestore.collection("linkInvitations").document(inviteCode).get().await()

            if (!inviteDoc.exists()) {
                return Result.failure(Exception("El código de invitación ingresado es inválido o no existe."))
            }
            if (inviteDoc.getString("status") != "PENDING") {
                return Result.failure(Exception("Esta invitación ya ha sido utilizada o fue revocada."))
            }

            val expiresAt = inviteDoc.getLong("expiresAt") ?: 0L
            if (System.currentTimeMillis() > expiresAt) {
                return Result.failure(Exception("El código de invitación ha expirado."))
            }

            // Document ID = UserUid (Por reglas de seguridad para subcollection)
            val claim = LinkClaim(
                userUid = uid,
                inviteId = inviteCode,
                createdAt = System.currentTimeMillis(),
                status = "PENDING"
            )

            firestore.collection("linkInvitations").document(inviteCode)
                .collection("claims").document(uid).set(claim).await()

            Result.success(claim)
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "No se pudo emitir la solicitud de enlace."))
        }
    }

    override suspend fun getInvitationClaims(inviteId: String): Result<List<LinkClaim>> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            // *NOTA DE LIMITACIÓN (Conforme a 4.1.2C - Reglas Restrictivas)*
            // La regla 'allow list' restringe explícitamente a trainerUid == request.auth.uid.
            // Esto implica que la vista puede intentar recolectar todas las solitudes.
            // Si Firestore la deniega es un PERMISSION_DENIED natural si el hacker no es dueño.

            val snapshot = firestore.collection("linkInvitations")
                .document(inviteId).collection("claims").get().await()

            val claims = snapshot.documents.mapNotNull { it.toObject(LinkClaim::class.java) }
            Result.success(claims)
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Error al consultar solicitudes."))
        }
    }

    override suspend fun approveClaim(inviteId: String, userUid: String): Result<Unit> {
        val trainerUid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))

        return try {
            // Extracción vital de la ficha
            val inviteDoc = firestore.collection("linkInvitations").document(inviteId).get().await()
            val playerId = inviteDoc.getString("playerId")
            val originalTrainer = inviteDoc.getString("trainerUid")

            if (playerId == null || originalTrainer != trainerUid) {
                return Result.failure(Exception("Operación denegada. Solo el creador de la invitación puede aprobarla."))
            }

            // Construir Batch (4 Operaciones atómicas requeridas por reglas `getAfter`)
            val batch = firestore.batch()

            val inviteRef = firestore.collection("linkInvitations").document(inviteId)
            val claimRef = inviteRef.collection("claims").document(userUid)
            val playerLinkRef = firestore.collection("playerAccountLinks").document(playerId)
            val userLinkRef = firestore.collection("userPlayerLinks").document(userUid)

            batch.update(inviteRef, "status", "CONSUMED")
            batch.update(claimRef, "status", "APPROVED")

            // ATENCIÓN: El esquema PlayerAccountLink espera un Timestamp. Usamos ServerTimestamp de Firebase.
            // Para la construcción del Batch no podemos mapear directamente a data classes Kotlin porque FieldValue no machea Long.
            // Es vital inyectar un map plano.

            val linkPayload = mapOf(
                "playerId" to playerId,
                "userUid" to userUid,
                "trainerUid" to trainerUid,
                "inviteId" to inviteId,
                "linkedAt" to FieldValue.serverTimestamp()
            )

            batch.set(playerLinkRef, linkPayload)
            batch.set(userLinkRef, linkPayload)

            // Ejecuta el commit masivo cruzado con los 4 `getAfter()` perimetrales
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Fallo en la aprobación atómica. Es probable que la cuenta ya posea un vínculo o la invitación caducó. (${e.message})"))
        }
    }

    override suspend fun getCurrentUserLink(): Result<PlayerAccountLink?> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            val doc = firestore.collection("userPlayerLinks").document(uid).get().await()
            if (doc.exists()) {
                val link = doc.toObject(PlayerAccountLink::class.java)
                Result.success(link)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(Exception("Incapaz de consultar la vinculación de cuenta: ${e.message}"))
        }
    }

    override suspend fun getPlayerAccountLink(playerId: String): Result<PlayerAccountLink?> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            val doc = firestore.collection("playerAccountLinks").document(playerId).get().await()
            if (doc.exists()) {
                val link = doc.toObject(PlayerAccountLink::class.java)
                Result.success(link)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(Exception("Incapaz de consultar el vínculo del jugador: ${e.message}"))
        }
    }

    override suspend fun getTrainerInvitations(): Result<List<LinkInvitation>> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            val snapshot = firestore.collection("linkInvitations")
                .whereEqualTo("trainerUid", uid)
                .get()
                .await()
            val invitations = snapshot.documents.mapNotNull { it.toObject(LinkInvitation::class.java) }
            Result.success(invitations)
        } catch (e: Exception) {
            Result.failure(Exception("Error al consultar invitaciones del entrenador: ${e.message}"))
        }
    }

    companion object {
        val instance: FirebaseLinkRepository by lazy { FirebaseLinkRepository() }
    }
}
