package devs.grupo5.sportpro.data.model

import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val SHORT_DATE = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("es-PE"))

/**
 * Etiqueta relativa en español para fechas de anuncios y notificaciones.
 * Evita textos fijos como "Hace 2h" o "Ayer" que quedan obsoletos.
 */
fun LocalDateTime.relativeLabel(now: LocalDateTime = LocalDateTime.now()): String {
    val minutes = Duration.between(this, now).toMinutes()
    return when {
        minutes <= 0L -> "Ahora"
        minutes < 60L -> "Hace $minutes min"
        minutes < 60L * 24L -> "Hace ${minutes / 60L} h"
        minutes < 60L * 48L -> "Ayer"
        minutes < 60L * 24L * 7L -> "Hace ${minutes / (60L * 24L)} días"
        else -> SHORT_DATE.format(this)
    }
}
