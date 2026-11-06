
package devs.grupo5.sportpro.data.model

data class Exercise(
    val id: String = "",
    val name: String = "",
    val durationMinutes: Int = 15,
    val type: String = "Táctico"
)

// Estados disponibles para la asistencia
enum class AttendanceStatus {
    PRESENTE,
    AUSENTE,
    JUSTIFICADO
}

// Registro individual de asistencia
data class AttendanceRecord(
    val playerId: String = "",
    val status: AttendanceStatus = AttendanceStatus.AUSENTE
)

data class Training(
    val id: String = "",
    val teamId: String = "",
    val title: String = "",
    val date: String = "",
    val startTime: String = "",
    val durationMinutes: Int = 90,
    val category: String = "",
    val objective: String = "",
    val exercises: List<Exercise> = emptyList(),

    // Se conserva por compatibilidad con el código existente
    val attendees: List<String> = emptyList(),

    val status: String = "Programado",

    // Nuevos registros de asistencia
    val attendanceRecords: List<AttendanceRecord> = emptyList()
)
