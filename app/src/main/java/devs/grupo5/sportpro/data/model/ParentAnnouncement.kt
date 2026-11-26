package devs.grupo5.sportpro.data.model

import java.time.LocalDateTime

/**
 * Audiencia de un comunicado. Se guarda como texto para que coincida con el valor
 * almacenado en la colección `announcements` de Firestore.
 */
enum class AnnouncementAudience(val value: String) {
    TODOS("TODOS"),
    APODERADOS("APODERADOS"),
    JUGADORES("JUGADORES")
}

/**
 * Comunicado publicado por la academia o el cuerpo técnico. Refleja el esquema final de la
 * colección `announcements` de Firestore; el estado de lectura es local por ahora.
 */
data class ParentAnnouncement(
    val id: String = "",
    val authorUid: String = "",
    val author: String = "Academia SportPro",
    val teamId: String? = null,
    val audience: AnnouncementAudience = AnnouncementAudience.TODOS,
    val title: String = "",
    val body: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val pinned: Boolean = false,
    val isRead: Boolean = false
) {
    /** La UI resalta los comunicados fijados como importantes. */
    val isImportant: Boolean
        get() = pinned

    fun relativeTimeLabel(now: LocalDateTime = LocalDateTime.now()): String =
        createdAt.relativeLabel(now)
}
