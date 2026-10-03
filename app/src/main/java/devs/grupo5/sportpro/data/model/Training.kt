package devs.grupo5.sportpro.data.model

data class Exercise(
    val id: String = "",
    val name: String = "",
    val durationMinutes: Int = 15,
    val type: String = "Táctico" // "Táctico", "Físico", "Técnico"
)

data class Training(
    val id: String = "",
    val teamId: String = "",
    val title: String = "",
    val date: String = "",
    val startTime: String = "",
    val durationMinutes: Int = 90,
    val category: String = "", // "Primera", "Sub-15", "Sub-10"
    val objective: String = "",
    val exercises: List<Exercise> = emptyList(),
    val attendees: List<String> = emptyList(), // IDs de jugadores presentes
    val status: String = "Programado" // "Programado", "En Curso", "Finalizado"
)
