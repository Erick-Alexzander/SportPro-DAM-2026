package devs.grupo5.sportpro.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

import devs.grupo5.sportpro.data.model.AnnouncementAudience
import devs.grupo5.sportpro.data.model.ParentAnnouncement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

class FirebaseAnnouncementRepository private constructor() : AnnouncementRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _announcements = MutableStateFlow<List<ParentAnnouncement>>(emptyList())
    override val announcements: StateFlow<List<ParentAnnouncement>> = _announcements.asStateFlow()

    private val _syncError = MutableStateFlow<String?>(null)
    override val syncError: StateFlow<String?> = _syncError.asStateFlow()

    private var listener: ListenerRegistration? = null

    override fun startListening() {
        val uid = auth.currentUser?.uid ?: return
        if (listener != null) return
        listener = firestore.collection("announcements")
            .whereEqualTo("authorUid", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _syncError.value = error.message
                    return@addSnapshotListener
                }
                _syncError.value = null
                _announcements.value = snapshot?.documents?.mapNotNull { doc ->
                    val audienceValue = doc.getString("audience").orEmpty()
                    ParentAnnouncement(
                        id = doc.id,
                        authorUid = doc.getString("authorUid").orEmpty(),
                        author = doc.getString("author").orEmpty(),
                        teamId = doc.getString("teamId"),
                        audience = AnnouncementAudience.entries.firstOrNull {
                            it.value == audienceValue || it.name == audienceValue
                        } ?: AnnouncementAudience.TODOS,
                        title = doc.getString("title").orEmpty(),
                        body = doc.getString("body").orEmpty(),
                        createdAt = toLocalDateTime(doc.get("createdAt")),
                        pinned = doc.getBoolean("pinned") ?: false
                    )
                }.orEmpty().sortedWith(compareByDescending<ParentAnnouncement> { it.pinned }.thenByDescending { it.createdAt })
            }
    }

    override fun stopListening() {
        listener?.remove()
        listener = null
        _announcements.value = emptyList()
    }

    override suspend fun createAnnouncement(
        title: String,
        body: String,
        audience: AnnouncementAudience,
        authorName: String,
        teamId: String?,
        pinned: Boolean
    ): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            val data = hashMapOf<String, Any?>(
                "authorUid" to uid,
                "author" to authorName.ifBlank { "Entrenador" },
                "audience" to audience.value,
                "title" to title.trim(),
                "body" to body.trim(),
                "createdAt" to FieldValue.serverTimestamp(),
                "pinned" to pinned
            )
            if (!teamId.isNullOrBlank()) data["teamId"] = teamId
            firestore.collection("announcements").add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAnnouncement(
        announcementId: String,
        title: String,
        body: String,
        audience: AnnouncementAudience,
        authorName: String,
        teamId: String?,
        pinned: Boolean
    ): Result<Unit> {
        return try {
            val data = hashMapOf<String, Any?>(
                "author" to authorName.ifBlank { "Entrenador" },
                "audience" to audience.value,
                "title" to title.trim(),
                "body" to body.trim(),
                "pinned" to pinned
            )
            if (teamId.isNullOrBlank()) {
                data["teamId"] = FieldValue.delete()
            } else {
                data["teamId"] = teamId
            }
            firestore.collection("announcements").document(announcementId).update(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAnnouncement(announcementId: String): Result<Unit> {
        return try {
            firestore.collection("announcements").document(announcementId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun toLocalDateTime(value: Any?): LocalDateTime = when (value) {
        is com.google.firebase.Timestamp -> Instant.ofEpochSecond(value.seconds, value.nanoseconds.toLong())
            .atZone(ZoneId.systemDefault()).toLocalDateTime()
        is Date -> Instant.ofEpochMilli(value.time).atZone(ZoneId.systemDefault()).toLocalDateTime()
        is String -> runCatching { LocalDateTime.parse(value) }.getOrDefault(LocalDateTime.now())
        else -> LocalDateTime.now()
    }

    companion object {
        val instance: FirebaseAnnouncementRepository by lazy { FirebaseAnnouncementRepository() }
    }
}
