package devs.grupo5.sportpro.data.repository

import devs.grupo5.sportpro.data.model.AnnouncementAudience
import devs.grupo5.sportpro.data.model.ParentAnnouncement
import kotlinx.coroutines.flow.StateFlow

interface AnnouncementRepository {
    val announcements: StateFlow<List<ParentAnnouncement>>
    val syncError: StateFlow<String?>

    fun startListening()
    fun stopListening()

    suspend fun createAnnouncement(
        title: String,
        body: String,
        audience: AnnouncementAudience,
        authorName: String,
        teamId: String?,
        pinned: Boolean
    ): Result<Unit>

    suspend fun updateAnnouncement(
        announcementId: String,
        title: String,
        body: String,
        audience: AnnouncementAudience,
        authorName: String,
        teamId: String?,
        pinned: Boolean
    ): Result<Unit>

    suspend fun deleteAnnouncement(announcementId: String): Result<Unit>
}
