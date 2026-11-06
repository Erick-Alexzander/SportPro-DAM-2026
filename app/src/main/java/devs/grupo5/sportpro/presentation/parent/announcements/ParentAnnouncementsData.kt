package devs.grupo5.sportpro.presentation.parent.announcements

import devs.grupo5.sportpro.data.model.relativeLabel
import java.time.LocalDateTime

/**
 * Comunicado publicado por la academia para las familias.
 * Se guarda la fecha real de publicación y la etiqueta visible se calcula al mostrarla.
 */
data class ParentAnnouncement(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val author: String = "Academia SportPro",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val isImportant: Boolean = false
) {
    fun relativeTimeLabel(now: LocalDateTime = LocalDateTime.now()): String =
        createdAt.relativeLabel(now)
}

/**
 * Comunicados de ejemplo relativos al momento actual. Se reemplazarán por la colección
 * `announcements` de Firestore cuando se conecte el módulo.
 */
internal fun parentAnnouncements(now: LocalDateTime = LocalDateTime.now()): List<ParentAnnouncement> =
    listOf(
        ParentAnnouncement(
            id = "a1",
            title = "Inicio de la segunda quincena",
            body = "Los horarios de entrenamiento se mantienen hasta fin de mes. " +
                "Este sábado no hay sesión por mantenimiento de la cancha.",
            author = "Academia SportPro",
            createdAt = now.minusHours(2),
            isImportant = true
        ),
        ParentAnnouncement(
            id = "a2",
            title = "Recordatorio de mensualidad",
            body = "La mensualidad del período actual se regulariza antes del día 10. " +
                "Recuerda registrar el comprobante en tu panel de apoderado.",
            author = "Administración SportPro",
            createdAt = now.minusHours(6)
        ),
        ParentAnnouncement(
            id = "a3",
            title = "Encuentro con las familias",
            body = "El club invita a las familias a conocer al equipo Sub-15 este sábado " +
                "en la cancha de la academia.",
            author = "Coach Ramírez",
            createdAt = now.minusDays(1)
        )
    ).sortedByDescending { it.createdAt }
