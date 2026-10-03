package devs.grupo5.sportpro.data.repository

import devs.grupo5.sportpro.data.model.AiSummary
import devs.grupo5.sportpro.data.model.Match
import devs.grupo5.sportpro.data.model.MatchEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface MatchRepository {
    val matches: StateFlow<List<Match>>
    fun getMatchById(id: String): Match?
    fun addMatch(match: Match)
    fun updateMatch(match: Match)
    fun addMatchEvent(matchId: String, event: MatchEvent): Match?
    fun updateMatchScore(matchId: String, homeScore: Int, awayScore: Int)
    fun toggleMatchPause(matchId: String)
    fun updateMatchMinute(matchId: String, minute: Int)
    fun finishMatch(matchId: String): Match?
    fun generateAiSummary(matchId: String): AiSummary
    fun updateAiSummary(matchId: String, aiSummary: AiSummary)
    fun approveAiSummary(matchId: String)
}

class MockMatchRepository private constructor() : MatchRepository {

    private val sampleEventsLive = listOf(
        MatchEvent(
            id = "e1",
            matchId = "m1",
            minute = 8,
            eventType = "Gol",
            icon = "⚽",
            player = "Rival #10",
            team = "Visitante",
            status = "Local",
            detail = "Gol de tiro libre"
        ),
        MatchEvent(
            id = "e2",
            matchId = "m1",
            minute = 22,
            eventType = "Gol",
            icon = "⚽",
            player = "Carlos Mendoza",
            team = "Local",
            status = "Corregido",
            detail = "Revisión VAR confirmada"
        ),
        MatchEvent(
            id = "e3",
            matchId = "m1",
            minute = 35,
            eventType = "T. Amarilla",
            icon = "🟨",
            player = "Sebastián Torres",
            team = "Local",
            status = "Local",
            detail = "Falta táctica"
        ),
        MatchEvent(
            id = "e4",
            matchId = "m1",
            minute = 55,
            eventType = "Gol",
            icon = "⚽",
            player = "Felipe Muñoz",
            team = "Local",
            status = "Local",
            detail = "Remate cruzado al ángulo"
        ),
        MatchEvent(
            id = "e5",
            matchId = "m1",
            minute = 62,
            eventType = "Cambio",
            icon = "🔄",
            player = "Diego Herrera",
            team = "Local",
            status = "Local",
            detail = "Sale: Diego Herrera / Entra: Gabriel Ruíz"
        )
    )

    private val sampleEventsFinished = listOf(
        MatchEvent(
            id = "e10",
            matchId = "m4",
            minute = 8,
            eventType = "Gol",
            icon = "⚽",
            player = "Rival #10",
            team = "Local",
            status = "Local",
            detail = "Aprovecha rebote en el área"
        ),
        MatchEvent(
            id = "e11",
            matchId = "m4",
            minute = 22,
            eventType = "Gol",
            icon = "⚽",
            player = "Carlos Mendoza",
            team = "Visitante",
            status = "Corregido",
            detail = "Anotación otorgada tras revisión"
        ),
        MatchEvent(
            id = "e12",
            matchId = "m4",
            minute = 55,
            eventType = "Gol",
            icon = "⚽",
            player = "Felipe Muñoz",
            team = "Visitante",
            status = "Visitante",
            detail = "Cabezazo tras córner"
        ),
        MatchEvent(
            id = "e13",
            matchId = "m4",
            minute = 78,
            eventType = "Gol",
            icon = "⚽",
            player = "Carlos Mendoza",
            team = "Visitante",
            status = "Visitante",
            detail = "Contraataque letal"
        ),
        MatchEvent(
            id = "e14",
            matchId = "m4",
            minute = 82,
            eventType = "T. Roja",
            icon = "🟥",
            player = "Rival #4",
            team = "Local",
            status = "Anulado",
            detail = "Tarjeta anulada por posición adelantada previa"
        )
    )

    private val initialSummaryFinished = AiSummary(
        introduction = "Encuentro oficial disputado en el Estadio Bicentenario por la categoría Primera entre Atlético Nacional y SportPro, finalizado con un marcador de 1 - 3.",
        development = "Anotaciones en planilla: 8' Gol del Rival #10 (Local); 22' Gol de Carlos Mendoza (Visitante - Corregido); 55' Gol de Felipe Muñoz (Visitante); 78' Gol de Carlos Mendoza (Visitante). A los 82', la tarjeta roja mostrada al Rival #4 fue anulada tras revisión.",
        highlights = "• Doblete anotado por Carlos Mendoza (22', 78').\n• Anotación de Felipe Muñoz al minuto 55'.\n• Incidencia corregida a los 22' y sanción anulada a los 82'.",
        analysisNote = "Este resumen fue generado exclusivamente con los eventos registrados durante el partido.",
        isApproved = false
    )

    private val initialMatches = listOf(
        Match(
            id = "m1",
            teamId = "t1",
            rival = "Club Deportivo Norte",
            date = "Hoy",
            time = "20:00",
            stadium = "Estadio Municipal",
            category = "Primera",
            status = "En vivo",
            isHome = true,
            homeScore = 2,
            awayScore = 1,
            currentMinute = 68,
            isPaused = false,
            events = sampleEventsLive
        ),
        Match(
            id = "m2",
            teamId = "t2",
            rival = "Academia Los Andes",
            date = "Sábado, 11:00",
            time = "11:00",
            stadium = "Cancha Central SportPro",
            category = "Sub-15",
            status = "Programado",
            isHome = true
        ),
        Match(
            id = "m3",
            teamId = "t3",
            rival = "Deportivo Juventud",
            date = "Domingo, 10:00",
            time = "10:00",
            stadium = "Complejo Deportivo Sur",
            category = "Sub-10",
            status = "Programado",
            isHome = false
        ),
        Match(
            id = "m4",
            teamId = "t1",
            rival = "Atlético Nacional",
            date = "24 Feb 2025",
            time = "19:00",
            stadium = "Estadio Bicentenario",
            category = "Primera",
            status = "Finalizado",
            isHome = false,
            homeScore = 1,
            awayScore = 3,
            currentMinute = 90,
            events = sampleEventsFinished,
            aiSummary = initialSummaryFinished
        )
    )

    private val _matches = MutableStateFlow(initialMatches)
    override val matches: StateFlow<List<Match>> = _matches.asStateFlow()

    override fun getMatchById(id: String): Match? {
        return _matches.value.find { it.id == id }
    }

    override fun addMatch(match: Match) {
        val newMatch = if (match.id.isEmpty()) {
            match.copy(id = "m_${System.currentTimeMillis()}")
        } else {
            match
        }
        _matches.value = _matches.value + newMatch
    }

    override fun updateMatch(match: Match) {
        _matches.value = _matches.value.map {
            if (it.id == match.id) match else it
        }
    }

    override fun addMatchEvent(matchId: String, event: MatchEvent): Match? {
        var updatedMatch: Match? = null
        _matches.value = _matches.value.map { match ->
            if (match.id == matchId) {
                val newEvent = if (event.id.isEmpty()) event.copy(id = "e_${System.currentTimeMillis()}") else event
                val updatedEvents = match.events + newEvent
                
                // Update score if event is Gol
                var newHomeScore = match.homeScore ?: 0
                var newAwayScore = match.awayScore ?: 0
                if (event.eventType.equals("Gol", ignoreCase = true)) {
                    if (event.team == "Local" || (match.isHome && event.team == "SportPro (Local)")) {
                        newHomeScore += 1
                    } else {
                        newAwayScore += 1
                    }
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
        return updatedMatch
    }

    override fun updateMatchScore(matchId: String, homeScore: Int, awayScore: Int) {
        _matches.value = _matches.value.map { match ->
            if (match.id == matchId) match.copy(homeScore = homeScore, awayScore = awayScore) else match
        }
    }

    override fun toggleMatchPause(matchId: String) {
        _matches.value = _matches.value.map { match ->
            if (match.id == matchId) match.copy(isPaused = !match.isPaused) else match
        }
    }

    override fun updateMatchMinute(matchId: String, minute: Int) {
        _matches.value = _matches.value.map { match ->
            if (match.id == matchId) match.copy(currentMinute = minute) else match
        }
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
    }

    override fun approveAiSummary(matchId: String) {
        _matches.value = _matches.value.map { match ->
            if (match.id == matchId) {
                val currentSummary = match.aiSummary ?: generateAiSummary(matchId)
                match.copy(aiSummary = currentSummary.copy(isApproved = true))
            } else match
        }
    }

    companion object {
        val instance: MockMatchRepository by lazy { MockMatchRepository() }
    }
}
