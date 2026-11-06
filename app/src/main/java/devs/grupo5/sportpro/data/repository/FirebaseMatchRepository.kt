package devs.grupo5.sportpro.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import devs.grupo5.sportpro.data.model.AiSummary
import devs.grupo5.sportpro.data.model.Match
import devs.grupo5.sportpro.data.model.MatchEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class FirebaseMatchRepository private constructor() : MatchRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    override val matches: StateFlow<List<Match>> = _matches.asStateFlow()

    private val _syncError = MutableStateFlow<String?>(null)
    override val syncError: StateFlow<String?> = _syncError.asStateFlow()

    private var matchesListener: ListenerRegistration? = null

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                startListening(user.uid)
            } else {
                stopListening()
                _matches.value = emptyList()
            }
        }
    }

    private fun startListening(uid: String) {
        matchesListener?.remove()
        matchesListener = firestore.collection("matches")
            .whereEqualTo("ownerUid", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _syncError.value = error.message
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    _syncError.value = null
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Match::class.java)?.copy(id = doc.id)
                    }
                    _matches.value = list
                }
            }
    }

    private fun stopListening() {
        matchesListener?.remove()
        matchesListener = null
        _syncError.value = null
    }

    override fun getMatchById(id: String): Match? {
        return _matches.value.find { it.id == id }
    }

    override suspend fun addMatch(match: Match): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))

        return try {
            val docRef = firestore.collection("matches").document()

            // Validación de pre-requisitos de integridad
            val teamSnapshot = firestore.collection("teams").document(match.teamId).get().await()
            if (!teamSnapshot.exists()) {
                throw Exception("El equipo no existe")
            }
            if (teamSnapshot.getString("ownerUid") != uid) {
                throw Exception("No tienes permisos para usar este equipo")
            }

            val finalMatch = match.copy(
                id = docRef.id,
                ownerUid = uid
            )

            Log.d("MatchRepository", "Intentando guardar partido. ID=${finalMatch.id}, ownerUid=${finalMatch.ownerUid}, teamId=${finalMatch.teamId}, isHome=${finalMatch.isHome}, status=${finalMatch.status}")

            docRef.set(finalMatch).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMatch(match: Match): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))

        return try {
            val docRef = firestore.collection("matches").document(match.id)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                if (!snapshot.exists()) {
                    throw Exception("El partido no existe")
                }
                if (snapshot.getString("ownerUid") != uid) {
                    throw Exception("No tienes permisos para modificar este partido")
                }

                transaction.set(docRef, match)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addMatchEvent(matchId: String, event: MatchEvent): Result<Match?> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))

        return try {
            var updatedMatch: Match? = null
            val docRef = firestore.collection("matches").document(matchId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                if (!snapshot.exists()) {
                    throw Exception("El partido no existe")
                }
                if (snapshot.getString("ownerUid") != uid) {
                    throw Exception("No tienes permisos para modificar este partido")
                }

                val currentMatch = snapshot.toObject(Match::class.java) ?: throw Exception("Datos inválidos")
                if (currentMatch.status == "Finalizado") {
                    throw Exception("El partido ya está finalizado. No admite más eventos.")
                }

                // Evitamos procesar eventos duplicados mediante un ID estable
                val newEvent = if (event.id.isEmpty()) event.copy(id = "e_${System.currentTimeMillis()}") else event
                if (currentMatch.events.any { it.id == newEvent.id }) {
                    updatedMatch = currentMatch
                    return@runTransaction
                }

                val updatedEvents = currentMatch.events + newEvent

                // Evaluar cambios en marcador de forma atómica
                var newHomeScore = currentMatch.homeScore ?: 0
                var newAwayScore = currentMatch.awayScore ?: 0

                if (newEvent.eventType.equals("Gol", ignoreCase = true)) {
                    if (newEvent.team == "Local" || (currentMatch.isHome && newEvent.team == "SportPro (Local)")) {
                        newHomeScore += 1
                    } else {
                        newAwayScore += 1
                    }
                }

                // Generamos la versión final atómica
                val nextMatchState = currentMatch.copy(
                    events = updatedEvents,
                    homeScore = newHomeScore,
                    awayScore = newAwayScore
                )

                transaction.update(docRef, mapOf(
                    "events" to nextMatchState.events,
                    "homeScore" to nextMatchState.homeScore,
                    "awayScore" to nextMatchState.awayScore
                ))

                updatedMatch = nextMatchState
            }.await()
            Result.success(updatedMatch)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMatchScore(matchId: String, homeScore: Int, awayScore: Int): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            val docRef = firestore.collection("matches").document(matchId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                if (snapshot.exists() && snapshot.getString("ownerUid") == uid) {
                    transaction.update(docRef, mapOf(
                        "homeScore" to homeScore,
                        "awayScore" to awayScore
                    ))
                } else {
                    throw Exception("No autorizado")
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun startMatch(matchId: String): Result<Match?> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            var updated: Match? = null
            val docRef = firestore.collection("matches").document(matchId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                if (snapshot.exists() && snapshot.getString("ownerUid") == uid) {
                    val status = snapshot.getString("status")
                    if (status != "Programado") {
                        throw Exception("El partido no está programado.")
                    }
                    transaction.update(docRef, "status", "En vivo")
                    val currentMatch = snapshot.toObject(Match::class.java)
                    updated = currentMatch?.copy(status = "En vivo")
                } else {
                    throw Exception("No autorizado")
                }
            }.await()
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleMatchPause(matchId: String): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            val docRef = firestore.collection("matches").document(matchId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                if (snapshot.exists() && snapshot.getString("ownerUid") == uid) {
                    val currentPause = snapshot.getBoolean("isPaused") ?: false
                    transaction.update(docRef, "isPaused", !currentPause)
                } else {
                    throw Exception("No autorizado")
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMatchMinute(matchId: String, minute: Int): Result<Unit> {
        // PRECAUCIÓN DE SEGURIDAD: Llamadas masivas cada segundo/minuto pueden consumir la cuota
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            val docRef = firestore.collection("matches").document(matchId)
            docRef.update("currentMinute", minute).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun finishMatch(matchId: String): Result<Match?> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            var updated: Match? = null
            val docRef = firestore.collection("matches").document(matchId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                if (snapshot.exists() && snapshot.getString("ownerUid") == uid) {
                    transaction.update(docRef, "status", "Finalizado")
                    val currentMatch = snapshot.toObject(Match::class.java)
                    updated = currentMatch?.copy(status = "Finalizado")
                } else {
                    throw Exception("No autorizado")
                }
            }.await()
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun generateAiSummary(matchId: String): Result<AiSummary> {
        // En esta etapa mantenemos la logica Mock ficticia pero adaptada a suspensión
        // para no romper la compatibilidad actual de generación de reporte
        val match = getMatchById(matchId)
        val homeTeam = if (match?.isHome == true) "SportPro" else match?.rival ?: "Local"
        val awayTeam = if (match?.isHome == true) match.rival else "SportPro"
        val scoreText = "${match?.homeScore ?: 0} - ${match?.awayScore ?: 0}"

        if (match == null || match.events.isEmpty()) {
            val emptySummary = AiSummary(
                introduction = "Encuentro entre $homeTeam y $awayTeam (Categoría ${match?.category ?: "Primera"}). Marcador final: $scoreText.",
                development = "No hay eventos registrados en este partido. No es posible generar un resumen narrativo.",
                highlights = "• Sin incidencias registradas en la planilla.",
                analysisNote = "Este resumen fue generado exclusivamente con los eventos registrados durante el partido.",
                isApproved = false
            )
            updateAiSummary(matchId, emptySummary)
            return Result.success(emptySummary)
        }

        val validEvents = match.events.filter { !it.status.equals("Anulado", ignoreCase = true) }
        val goals = validEvents.filter { it.eventType.equals("Gol", ignoreCase = true) }
        val cards = validEvents.filter { it.eventType.contains("T.", ignoreCase = true) || it.eventType.contains("Tarjeta", ignoreCase = true) }
        val subs = validEvents.filter { it.eventType.equals("Cambio", ignoreCase = true) }
        val correctedEvents = match.events.filter { it.status.equals("Corregido", ignoreCase = true) }
        val annulledEvents = match.events.filter { it.status.equals("Anulado", ignoreCase = true) }

        val eventsChrono = match.events.joinToString("; ") { ev ->
            val playerPart = if (ev.player.isNotBlank()) " de ${ev.player}" else ""
            val detailPart = if (ev.detail.isNotBlank()) " (${ev.detail})" else ""
            val statusPart = if (ev.status == "Corregido" || ev.status == "Anulado") " [${ev.status}]" else ""
            "${ev.minute}' ${ev.eventType}$playerPart - ${ev.team}$detailPart$statusPart"
        }

        val goalsSummary = if (goals.isNotEmpty()) {
            "Goles anotados: " + goals.joinToString(", ") { "${it.minute}' ${it.player} (${it.team})" } + "."
        } else {
            "No se registraron goles en las incidencias de la planilla."
        }

        val cardsSummary = if (cards.isNotEmpty()) {
            " Amonestaciones/Sanciones: " + cards.joinToString(", ") { "${it.minute}' ${it.eventType} para ${it.player} (${it.team})" } + "."
        } else ""

        val subsSummary = if (subs.isNotEmpty()) {
            " Sustituciones realizadas: " + subs.joinToString(", ") { "${it.minute}' ${it.player} (${it.team})" } + "."
        } else ""

        val correctedText = if (correctedEvents.isNotEmpty()) {
            " Incidencias corregidas: " + correctedEvents.joinToString("; ") { "${it.minute}' ${it.eventType} de ${it.player}" } + "."
        } else ""

        val annulledText = if (annulledEvents.isNotEmpty()) {
            " Sanciones anuladas: " + annulledEvents.joinToString("; ") { "${it.minute}' ${it.eventType} a ${it.player}" } + "."
        } else ""

        val highlightsList = mutableListOf<String>()
        if (goals.isNotEmpty()) {
            goals.forEach { g ->
                highlightsList.add("• Gol de ${g.player} al minuto ${g.minute}' (${g.team}).")
            }
        }
        if (cards.isNotEmpty()) {
            cards.forEach { c ->
                highlightsList.add("• ${c.eventType} a ${c.player} al minuto ${c.minute}' (${c.team}).")
            }
        }
        if (annulledEvents.isNotEmpty()) {
            annulledEvents.forEach { a ->
                highlightsList.add("• Sanción anulada: ${a.eventType} a ${a.player} (${a.minute}').")
            }
        }
        if (highlightsList.isEmpty()) {
            highlightsList.add("• Se registraron ${match.events.size} eventos en la planilla oficial.")
        }

        val generated = AiSummary(
            introduction = "Resumen oficial del encuentro entre $homeTeam y $awayTeam (Categoría ${match.category}), finalizado con un marcador de $scoreText.",
            development = "Cronología de eventos registrados: $eventsChrono. $goalsSummary$cardsSummary$subsSummary$correctedText$annulledText",
            highlights = highlightsList.joinToString("\n"),
            analysisNote = "Este resumen fue generado exclusivamente con los eventos registrados durante el partido.",
            isApproved = false
        )

        updateAiSummary(matchId, generated)
        return Result.success(generated)
    }

    override suspend fun updateAiSummary(matchId: String, aiSummary: AiSummary): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            val docRef = firestore.collection("matches").document(matchId)
            docRef.update("aiSummary", aiSummary).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun approveAiSummary(matchId: String): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            val docRef = firestore.collection("matches").document(matchId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                if (snapshot.exists() && snapshot.getString("ownerUid") == uid) {
                    val currentMatch = snapshot.toObject(Match::class.java)
                    if (currentMatch?.aiSummary != null) {
                        transaction.update(docRef, "aiSummary.isApproved", true)
                    }
                } else {
                    throw Exception("No autorizado")
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        val instance: FirebaseMatchRepository by lazy { FirebaseMatchRepository() }
    }
}
