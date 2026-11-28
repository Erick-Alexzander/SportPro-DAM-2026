package devs.grupo5.sportpro.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import devs.grupo5.sportpro.data.model.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class FirebasePlayerRepository private constructor() : PlayerRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _players = MutableStateFlow<List<Player>>(emptyList())
    override val players: StateFlow<List<Player>> = _players.asStateFlow()

    private val _syncError = MutableStateFlow<String?>(null)
    override val syncError: StateFlow<String?> = _syncError.asStateFlow()

    private var playersListener: ListenerRegistration? = null

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                startListening(user.uid)
            } else {
                stopListening()
                _players.value = emptyList()
            }
        }
    }

    private fun startListening(uid: String) {
        playersListener?.remove()
        playersListener = firestore.collection("players")
            .whereEqualTo("ownerUid", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _syncError.value = error.message
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    _syncError.value = null
                    val playersList = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Player::class.java)?.copy(id = doc.id)
                    }
                    _players.value = playersList
                }
            }
    }

    private fun stopListening() {
        playersListener?.remove()
        playersListener = null
        _syncError.value = null
    }

    override fun getPlayerById(id: String): Player? {
        return _players.value.find { it.id == id }
    }

    override fun getPlayersByIds(ids: List<String>): List<Player> {
        return _players.value.filter { it.id in ids }
    }

    private fun playerCreateData(player: Player): HashMap<String, Any?> {
        val data = hashMapOf<String, Any?>(
            "id" to player.id,
            "ownerUid" to player.ownerUid,
            "fullName" to player.fullName,
            "category" to player.category,
            "position" to player.position,
            "jerseyNumber" to player.jerseyNumber,
            "age" to player.age,
            "status" to player.status,
            "dateOfBirth" to player.dateOfBirth,
            "contactEmail" to player.contactEmail,
            "dominantFoot" to player.dominantFoot,
            "size" to player.size,
            "weight" to player.weight,
            "parentName" to player.parentName,
            "parentRelation" to player.parentRelation,
            "parentPhone" to player.parentPhone,
            "emergencyPhone" to player.emergencyPhone,
            "privacySettings" to hashMapOf(
                "profilePhoto" to player.privacySettings.profilePhoto,
                "fullNameVisibility" to player.privacySettings.fullNameVisibility,
                "statsVisibility" to player.privacySettings.statsVisibility
            ),
            "guardianUids" to player.guardianUids
        )
        player.teamId?.let { data["teamId"] = it }
        player.linkedAuthId?.let { data["linkedAuthId"] = it }
        return data
    }

    override suspend fun addPlayer(player: Player): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))

        return try {
            val docRef = firestore.collection("players").document()
            val finalPlayer = player.copy(
                id = docRef.id,
                ownerUid = uid
            )
            docRef.set(playerCreateData(finalPlayer)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addPlayerWithTeam(player: Player, teamId: String): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))

        return try {
            val playerRef = firestore.collection("players").document()
            val teamRef = firestore.collection("teams").document(teamId)

            firestore.runTransaction { transaction ->
                // 1. Lectura del equipo
                val teamSnapshot = transaction.get(teamRef)
                if (!teamSnapshot.exists()) {
                    throw Exception("El equipo no existe")
                }

                val teamOwnerUid = teamSnapshot.getString("ownerUid")
                if (teamOwnerUid != uid) {
                    throw Exception("No tienes permisos para modificar este equipo")
                }

                // 2. Preparar el jugador
                val finalPlayer = player.copy(
                    id = playerRef.id,
                    ownerUid = uid,
                    teamId = teamId
                )

                // 3. Escritura del jugador
                transaction.set(playerRef, playerCreateData(finalPlayer))

                // 4. Actualización del equipo
                val currentPlayerIds = teamSnapshot.get("playerIds") as? List<*> ?: emptyList<Any>()
                if (!currentPlayerIds.contains(playerRef.id)) {
                    val updatedPlayerIds = currentPlayerIds.toMutableList()
                    updatedPlayerIds.add(playerRef.id)
                    
                    transaction.update(teamRef, "playerIds", updatedPlayerIds)
                    
                    // Solo incrementamos si realmente lo agregamos
                    val currentCount = teamSnapshot.getLong("playerCount") ?: currentPlayerIds.size.toLong()
                    transaction.update(teamRef, "playerCount", currentCount + 1)
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePlayer(player: Player): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))

        return try {
            val docRef = firestore.collection("players").document(player.id)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                if (!snapshot.exists()) {
                    throw Exception("El jugador no existe")
                }

                val ownerUid = snapshot.getString("ownerUid")
                if (ownerUid != uid) {
                    throw Exception("No tienes permisos para modificar este jugador")
                }

                // No reescribir guardianUids: solo la Cloud Function puede gestionar los vínculos.
                val editableFields = listOf(
                    "teamId", "fullName", "category", "position", "jerseyNumber",
                    "age", "status", "dateOfBirth", "contactEmail", "dominantFoot",
                    "size", "weight", "parentName", "parentRelation", "parentPhone",
                    "emergencyPhone", "privacySettings"
                )
                transaction.set(docRef, player, SetOptions.mergeFields(editableFields))
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        val instance: FirebasePlayerRepository by lazy { FirebasePlayerRepository() }
    }
}
