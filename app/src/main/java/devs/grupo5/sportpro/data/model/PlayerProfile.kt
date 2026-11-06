package devs.grupo5.sportpro.data.model

import com.google.firebase.firestore.Exclude

/**
 * Atributos técnicos y físicos del jugador. Los valores van de 0 a 100.
 */
data class PlayerAttributes(
    val speed: Int = 0,
    val shooting: Int = 0,
    val passing: Int = 0,
    val defense: Int = 0,
    val physical: Int = 0
) {
    @get:Exclude
    val hasData: Boolean
        get() = speed > 0 || shooting > 0 || passing > 0 || defense > 0 || physical > 0

    @get:Exclude
    val overall: Int
        get() {
            val values = listOf(speed, shooting, passing, defense, physical).filter { it > 0 }
            return if (values.isEmpty()) 0 else values.average().toInt()
        }
}

/**
 * Estadísticas acumuladas de la temporada actual.
 */
data class PlayerSeasonStats(
    val matchesPlayed: Int = 0,
    val goals: Int = 0,
    val assists: Int = 0,
    val yellowCards: Int = 0,
    val redCards: Int = 0
)

/**
 * Información deportiva que complementa el perfil de cuenta del jugador.
 * Se persiste dentro del documento users/{uid} en Firestore.
 */
data class PlayerProfile(
    val position: String = "",
    val jerseyNumber: Int = 0,
    val category: String = "",
    val dominantFoot: String = "",
    val status: String = "Activo",
    val teamName: String = "",
    val heightCm: Int = 0,
    val weightKg: Double = 0.0,
    val size: String = "",
    val stats: PlayerSeasonStats = PlayerSeasonStats(),
    val attributes: PlayerAttributes = PlayerAttributes(),
    val privacySettings: PrivacySettings = PrivacySettings()
) {
    @get:Exclude
    val hasSportData: Boolean
        get() = position.isNotBlank() ||
            jerseyNumber > 0 ||
            category.isNotBlank() ||
            heightCm > 0 ||
            weightKg > 0.0

    @get:Exclude
    val positionLabel: String
        get() = when (position.uppercase()) {
            "POR" -> "Portero"
            "DEF" -> "Defensa"
            "MED" -> "Mediocampista"
            "DEL" -> "Delantero"
            else -> position.ifBlank { "—" }
        }

    @get:Exclude
    val jerseyLabel: String
        get() = if (jerseyNumber > 0) "#$jerseyNumber" else "—"

    @get:Exclude
    val heightLabel: String
        get() = if (heightCm > 0) "$heightCm cm" else "—"

    @get:Exclude
    val weightLabel: String
        get() = if (weightKg > 0.0) "${weightKg.toInt()} kg" else "—"

    @get:Exclude
    val bmi: Double
        get() = if (heightCm > 0 && weightKg > 0.0) {
            val heightMeters = heightCm / 100.0
            weightKg / (heightMeters * heightMeters)
        } else {
            0.0
        }
}
