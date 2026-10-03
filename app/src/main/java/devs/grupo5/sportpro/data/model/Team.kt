package devs.grupo5.sportpro.data.model

data class Team(
    val id: String = "",
    val name: String = "",
    val category: String = "", // "Primera", "Sub-15", "Sub-10"
    val description: String = "",
    val coachName: String = "",
    val playerIds: List<String> = emptyList(),
    val playerCount: Int = playerIds.size
)
