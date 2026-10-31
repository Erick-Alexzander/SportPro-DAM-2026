package devs.grupo5.sportpro.data.model

/**
 * Métricas deportivas mostradas para el menor vinculado a la cuenta del apoderado.
 */
data class ChildStats(
    val goals: Int = 0,
    val assists: Int = 0,
    val matchesPlayed: Int = 0,
    val attendancePercent: Int = 0
)

/**
 * Menor de edad vinculado a la cuenta responsable del apoderado.
 */
data class LinkedChild(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val category: String = "",
    val age: Int = 0,
    val jerseyNumber: Int = 0,
    val position: String = "",
    val birthDate: String = "",
    val heightCm: Int = 0,
    val weightKg: Int = 0,
    val phone: String = "",
    val emergencyPhone: String = "",
    val parentRelation: String = "",
    val status: String = "Activo",
    val stats: ChildStats = ChildStats(),
    val privacySettings: PrivacySettings = PrivacySettings()
) {
    val fullName: String
        get() = listOf(firstName, lastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")

    val initials: String
        get() = listOf(firstName, lastName)
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
            .ifBlank { "?" }

    val ageLabel: String
        get() = if (currentAge > 0) "$currentAge años" else "—"

    val currentAge: Int
        get() {
            if (birthDate.isBlank()) return age
            return try {
                val parts = birthDate.split("/", "-", ".").mapNotNull { it.trim().toIntOrNull() }
                if (parts.size == 3) {
                    val (day, month, year) = if (parts[0] > 1000) Triple(parts[2], parts[1], parts[0]) else Triple(parts[0], parts[1], parts[2])
                    val today = java.time.LocalDate.now()
                    val bDate = java.time.LocalDate.of(year, month, day)
                    var calculated = today.year - bDate.year
                    if (today.monthValue < bDate.monthValue || (today.monthValue == bDate.monthValue && today.dayOfMonth < bDate.dayOfMonth)) {
                        calculated--
                    }
                    calculated.coerceAtLeast(0)
                } else age
            } catch (_: Exception) {
                age
            }
        }

    val heightLabel: String
        get() = if (heightCm > 0) "$heightCm cm" else "—"

    val weightLabel: String
        get() = if (weightKg > 0) "$weightKg kg" else "—"

    val birthDateIso: String
        get() = birthDate.normalizeBirthDate()

    val birthDateLabel: String
        get() = birthDateIso.ifBlank { "—" }
}
