package devs.grupo5.sportpro.presentation.parent.announcements

/**
 * Comunicado publicado por la academia para las familias.
 */
data class ParentAnnouncement(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val author: String = "Academia SportPro",
    val time: String = "",
    val isImportant: Boolean = false
)

internal val parentAnnouncements = listOf(
    ParentAnnouncement(
        id = "a1",
        title = "Inicio de la segunda quincena",
        body = "Los horarios de entrenamiento se mantienen hasta el 30 de noviembre. " +
            "El sábado 09 no hay sesión por mantenimiento de la cancha.",
        author = "Academia SportPro",
        time = "Hace 2h",
        isImportant = true
    ),
    ParentAnnouncement(
        id = "a2",
        title = "Recordatorio de mensualidad",
        body = "La mensualidad de noviembre se regulariza antes del 10. Recuerda registrar " +
            "el comprobante en tu panel de apoderado.",
        author = "Administración SportPro",
        time = "Hace 6h"
    ),
    ParentAnnouncement(
        id = "a3",
        title = "Encuentro con las familias",
        body = "El club invita a las familias a conocer al equipo Sub-15 este sábado en la cancha de la academia.",
        author = "Coach Ramírez",
        time = "Ayer"
    )
)
