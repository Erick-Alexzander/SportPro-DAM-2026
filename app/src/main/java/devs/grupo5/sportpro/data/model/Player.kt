package devs.grupo5.sportpro.data.model

data class Player(
    val id: String = "",
    val fullName: String = "",
    val category: String = "", // "Primera", "Sub-15", "Sub-10"
    val position: String = "", // "POR", "DEF", "MED", "DEL"
    val jerseyNumber: Int = 0,
    val age: Int = 0,
    val status: String = "Activo", // "Activo", "Lesionado", "Suspendido"
    val dateOfBirth: String = "",
    val contactEmail: String = "",
    val dominantFoot: String = "Derecho", // "Derecho", "Izquierdo", "Ambidiestro"
    val size: String = "M",
    val weight: Double = 0.0,
    val parentName: String = "",
    val parentRelation: String = "",
    val parentPhone: String = "",
    val emergencyPhone: String = "",
    val trainerId: String = "",
    val teamId: String = ""
)

