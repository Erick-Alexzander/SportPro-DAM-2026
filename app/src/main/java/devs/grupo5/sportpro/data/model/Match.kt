package devs.grupo5.sportpro.data.model

data class MatchEvent(
    val id: String = "",
    val matchId: String = "",
    val minute: Int = 0,
    val eventType: String = "", // "Gol", "T. Amarilla", "T. Roja", "Cambio", "Córner", "Falta", "Penal", "Fuera de juego"
    val icon: String = "⚽",
    val player: String = "",
    val team: String = "Local", // "Local" or "Visitante"
    val status: String = "Local", // "Local", "Visitante", "Corregido", "Pendiente", "Anulado"
    val detail: String = "" // Información adicional (ej: jugador que entra/sale)
)

data class AiSummary(
    val introduction: String = "",
    val development: String = "",
    val highlights: String = "",
    val analysisNote: String = "Este resumen fue generado exclusivamente con los eventos registrados durante el partido.",
    val isApproved: Boolean = false
)

data class Match(
    val id: String = "",
    val teamId: String = "",
    val rival: String = "",
    val date: String = "",
    val time: String = "",
    val stadium: String = "",
    val category: String = "", // "Primera", "Sub-15", "Sub-10"
    val status: String = "Programado", // "Programado", "En vivo", "Finalizado"
    val isHome: Boolean = true, // true: Local, false: Visitante
    val homeScore: Int? = null,
    val awayScore: Int? = null,
    val currentMinute: Int = 68,
    val isPaused: Boolean = false,
    val events: List<MatchEvent> = emptyList(),
    val aiSummary: AiSummary? = null
)
