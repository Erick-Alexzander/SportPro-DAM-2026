package devs.grupo5.sportpro.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import devs.grupo5.sportpro.data.model.AiSummary
import devs.grupo5.sportpro.data.model.Match
import devs.grupo5.sportpro.data.model.MatchEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirebaseMatchRepository private constructor() : MatchRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    override val matches: StateFlow<List<Match>> = _matches.asStateFlow()

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
                if (error != null || snapshot == null) return@addSnapshotListener
                _matches.value = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Match::class.java)?.copy(id = doc.id)
                }
            }
    }

    private fun stopListening() {
        matchesListener?.remove()
        matchesListener = null
    }

    override fun getMatchById(id: String): Match? = _matches.value.find { it.id == id }

    override fun addMatch(match: Match) {
        val uid = auth.currentUser?.uid ?: return
        val docRef = firestore.collection("matches").document()
        val newMatch = match.copy(
            id = docRef.id,
            ownerUid = uid,
            status = "Programado",
            homeScore = 0,
            awayScore = 0,
            currentMinute = 0,
            isPaused = false,
            events = emptyList(),
            aiSummary = null
        )
        _matches.value = _matches.value + newMatch
        scope.launch {
            try {
                docRef.set(newMatch).await()
            } catch (_: Exception) {
            }
        }
    }

    override fun updateMatch(match: Match) {
        val uid = auth.currentUser?.uid ?: return
        if (match.id.isBlank()) return
        val current = getMatchById(match.id)
        val updated = match.copy(ownerUid = uid)
        _matches.value = _matches.value.map { if (it.id == updated.id) updated else it }
        persistChangedFields(current, updated)
    }

    override fun addMatchEvent(matchId: String, event: MatchEvent): Match? {
        var updatedMatch: Match? = null
        var newEvent: MatchEvent? = null
        _matches.value = _matches.value.map { match ->
            if (match.id == matchId) {
                val ev = if (event.id.isEmpty()) event.copy(id = "e_${System.currentTimeMillis()}") else event
                newEvent = ev
                val updatedEvents = match.events + ev

                var newHomeScore = match.homeScore ?: 0
                var newAwayScore = match.awayScore ?: 0
                if (ev.eventType.equals("Gol", ignoreCase = true)) {
                    if (ev.team == "Local") newHomeScore += 1 else newAwayScore += 1
                }

                val m = match.copy(
                    events = updatedEvents,
                    homeScore = newHomeScore,
                    awayScore = newAwayScore
                )
                updatedMatch = m
                m
            } else match
        }
        val m = updatedMatch
        val ev = newEvent
        if (m != null && ev != null) {
            val data = hashMapOf<String, Any?>("events" to m.events)
            if (ev.eventType.equals("Gol", ignoreCase = true)) {
                if (ev.team == "Local") data["homeScore"] = m.homeScore else data["awayScore"] = m.awayScore
            }
            persistFields(matchId, data)
        }
        return updatedMatch
    }

    override fun updateMatchScore(matchId: String, homeScore: Int, awayScore: Int) {
        _matches.value = _matches.value.map { match ->
            if (match.id == matchId) match.copy(homeScore = homeScore, awayScore = awayScore) else match
        }
        persistFields(matchId, hashMapOf("homeScore" to homeScore, "awayScore" to awayScore))
    }

    override fun toggleMatchPause(matchId: String) {
        var newValue: Boolean? = null
        _matches.value = _matches.value.map { match ->
            if (match.id == matchId) {
                val paused = !match.isPaused
                newValue = paused
                match.copy(isPaused = paused)
            } else match
        }
        newValue?.let { persistFields(matchId, hashMapOf("isPaused" to it)) }
    }

    override fun updateMatchMinute(matchId: String, minute: Int) {
        _matches.value = _matches.value.map { match ->
            if (match.id == matchId) match.copy(currentMinute = minute) else match
        }
        persistFields(matchId, hashMapOf("currentMinute" to minute))
    }

    override fun finishMatch(matchId: String): Match? {
        var updated: Match? = null
        _matches.value = _matches.value.map { match ->
            if (match.id == matchId) {
                val m = match.copy(status = "Finalizado")
                updated = m
                m
            } else match
        }
        persistFields(matchId, hashMapOf("status" to "Finalizado"))
        return updated
    }

    override fun generateAiSummary(matchId: String): AiSummary {
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
            return emptySummary
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
        return generated
    }

    override fun updateAiSummary(matchId: String, aiSummary: AiSummary) {
        _matches.value = _matches.value.map { match ->
            if (match.id == matchId) match.copy(aiSummary = aiSummary) else match
        }
        persistFields(matchId, hashMapOf("aiSummary" to aiSummary))
    }

    override fun approveAiSummary(matchId: String) {
        var summary: AiSummary? = null
        _matches.value = _matches.value.map { match ->
            if (match.id == matchId) {
                val currentSummary = match.aiSummary ?: generateAiSummary(matchId)
                val approved = currentSummary.copy(isApproved = true)
                summary = approved
                match.copy(aiSummary = approved)
            } else match
        }
        summary?.let { persistFields(matchId, hashMapOf("aiSummary" to it)) }
    }

    private fun persistFields(matchId: String, data: HashMap<String, Any?>) {
        if (data.isEmpty()) return
        scope.launch {
            try {
                firestore.collection("matches").document(matchId)
                    .set(data, SetOptions.merge())
                    .await()
            } catch (_: Exception) {
            }
        }
    }

    private fun persistChangedFields(current: Match?, updated: Match) {
        val data = hashMapOf<String, Any?>()
        if (current == null) {
            data["rival"] = updated.rival
            data["date"] = updated.date
            data["time"] = updated.time
            data["stadium"] = updated.stadium
            data["category"] = updated.category
            data["status"] = updated.status
            data["isHome"] = updated.isHome
            data["homeScore"] = updated.homeScore
            data["awayScore"] = updated.awayScore
            data["currentMinute"] = updated.currentMinute
            data["isPaused"] = updated.isPaused
            data["events"] = updated.events
            data["aiSummary"] = updated.aiSummary
        } else {
            if (current.rival != updated.rival) data["rival"] = updated.rival
            if (current.date != updated.date) data["date"] = updated.date
            if (current.time != updated.time) data["time"] = updated.time
            if (current.stadium != updated.stadium) data["stadium"] = updated.stadium
            if (current.category != updated.category) data["category"] = updated.category
            if (current.status != updated.status) data["status"] = updated.status
            if (current.isHome != updated.isHome) data["isHome"] = updated.isHome
            if (current.homeScore != updated.homeScore) data["homeScore"] = updated.homeScore
            if (current.awayScore != updated.awayScore) data["awayScore"] = updated.awayScore
            if (current.currentMinute != updated.currentMinute) data["currentMinute"] = updated.currentMinute
            if (current.isPaused != updated.isPaused) data["isPaused"] = updated.isPaused
            if (current.events != updated.events) data["events"] = updated.events
            if (current.aiSummary != updated.aiSummary) data["aiSummary"] = updated.aiSummary
        }
        persistFields(updated.id, data)
    }

    companion object {
        val instance: FirebaseMatchRepository by lazy { FirebaseMatchRepository() }
    }
}
