package devs.grupo5.sportpro.data.model

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
    val awayScore: Int? = null
)
