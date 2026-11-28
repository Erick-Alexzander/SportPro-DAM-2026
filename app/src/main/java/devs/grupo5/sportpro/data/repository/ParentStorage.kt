package devs.grupo5.sportpro.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import devs.grupo5.sportpro.data.model.AnnouncementAudience
import devs.grupo5.sportpro.data.model.ChildStats
import devs.grupo5.sportpro.data.model.GuardianProfile
import devs.grupo5.sportpro.data.model.GuardianTitle
import devs.grupo5.sportpro.data.model.LinkedChild
import devs.grupo5.sportpro.data.model.ParentAnnouncement
import devs.grupo5.sportpro.data.model.ParentNotification
import devs.grupo5.sportpro.data.model.ParentPayment
import devs.grupo5.sportpro.data.model.PaymentStatus
import devs.grupo5.sportpro.data.model.currentPaymentPeriod
import java.time.LocalDateTime
import org.json.JSONArray
import org.json.JSONObject

/**
 * Persistencia local del módulo del apoderado. Equivale al `localStorage` del prototipo web:
 * conserva los datos de los menores vinculados (incluidos los datos físicos y de contacto), los del
 * apoderado responsable y el estado de la mensualidad entre sesiones de la aplicación.
 */
class ParentStorage(context: Context, val uid: String) {

    private val prefs: SharedPreferences = context.applicationContext
        .getSharedPreferences("${PREFS_NAME}_$uid", Context.MODE_PRIVATE)

    fun loadGuardian(): GuardianProfile? {
        val raw = prefs.getString(KEY_GUARDIAN, null) ?: return null
        return runCatching {
            val json = JSONObject(raw)
            GuardianProfile(
                firstName = json.optString("firstName"),
                lastName = json.optString("lastName"),
                email = json.optString("email"),
                title = GuardianTitle.entries.firstOrNull { it.name == json.optString("title") }
                    ?: GuardianTitle.APODERADO
            )
        }.getOrNull()
    }

    fun saveGuardian(profile: GuardianProfile) {
        val json = JSONObject().apply {
            put("firstName", profile.firstName)
            put("lastName", profile.lastName)
            put("email", profile.email)
            put("title", profile.title.name)
        }
        prefs.edit { putString(KEY_GUARDIAN, json.toString()) }
    }

    /**
     * Menores vinculados. Migra el registro único de versiones anteriores si aún existe.
     */
    fun loadChildren(): List<LinkedChild> {
        val raw = prefs.getString(KEY_CHILDREN, null)
        if (raw != null) {
            return runCatching {
                val array = JSONArray(raw)
                (0 until array.length()).mapNotNull { index ->
                    array.optJSONObject(index)?.let { childFromJson(it) }
                }
            }.getOrDefault(emptyList())
        }

        val legacy = prefs.getString(KEY_CHILD, null) ?: return emptyList()
        return runCatching {
            val child = childFromJson(JSONObject(legacy))
            if (child != null) listOf(child) else emptyList()
        }.getOrDefault(emptyList())
    }

    fun saveChildren(children: List<LinkedChild>) {
        val array = JSONArray()
        children.forEach { array.put(childToJson(it)) }
        prefs.edit {
            putString(KEY_CHILDREN, array.toString())
            remove(KEY_CHILD)
        }
    }

    fun loadSelectedChildId(): String? {
        val id = prefs.getString(KEY_SELECTED_CHILD, null) ?: return null
        return id.ifBlank { null }
    }

    fun saveSelectedChildId(id: String?) {
        prefs.edit {
            if (id.isNullOrBlank()) remove(KEY_SELECTED_CHILD) else putString(KEY_SELECTED_CHILD, id)
        }
    }

    /**
     * Mensualidades guardadas. Si no existe el formato nuevo, migra el registro único de
     * versiones anteriores asignándolo al primer menor vinculado.
     */
    fun loadPayments(fallbackPlayerId: String? = null): List<ParentPayment> {
        val raw = prefs.getString(KEY_PAYMENTS, null)
        if (raw != null) {
            return runCatching {
                val array = JSONArray(raw)
                (0 until array.length()).mapNotNull { index ->
                    array.optJSONObject(index)?.let { paymentFromJson(it) }
                }
            }.getOrDefault(emptyList())
        }

        val legacy = prefs.getString(KEY_PAYMENT, null) ?: return emptyList()
        return runCatching {
            val payment = paymentFromJson(JSONObject(legacy))
            listOf(payment.copy(playerId = fallbackPlayerId.orEmpty()))
        }.getOrDefault(emptyList())
    }

    fun savePayments(payments: List<ParentPayment>) {
        val array = JSONArray()
        payments.forEach { payment -> array.put(paymentToJson(payment)) }
        prefs.edit {
            putString(KEY_PAYMENTS, array.toString())
            remove(KEY_PAYMENT)
        }
    }

    private fun paymentFromJson(json: JSONObject): ParentPayment {
        val updatedAt = runCatching {
            LocalDateTime.parse(json.optString("updatedAt"))
        }.getOrNull() ?: LocalDateTime.now()
        val receiptUrl = json.optString("receiptUrl").takeIf { it.isNotBlank() }
        return ParentPayment(
            id = json.optString("id"),
            ownerUid = json.optString("ownerUid"),
            parentUid = json.optString("parentUid"),
            playerId = json.optString("playerId"),
            period = json.optString("period").ifBlank { currentPaymentPeriod() },
            status = PaymentStatus.entries.firstOrNull { it.name == json.optString("status") }
                ?: PaymentStatus.PENDIENTE,
            receiptRegistered = json.optBoolean("receiptRegistered"),
            receiptUrl = receiptUrl,
            updatedAt = updatedAt,
            updatedBy = json.optString("updatedBy")
        )
    }

    private fun paymentToJson(payment: ParentPayment): JSONObject = JSONObject().apply {
        put("id", payment.id)
        put("ownerUid", payment.ownerUid)
        put("parentUid", payment.parentUid)
        put("playerId", payment.playerId)
        put("period", payment.period)
        put("status", payment.status.name)
        put("receiptRegistered", payment.receiptRegistered)
        put("receiptUrl", payment.receiptUrl.orEmpty())
        put("updatedAt", payment.updatedAt.toString())
        put("updatedBy", payment.updatedBy)
    }

    /** Comunicados guardados. Devuelve `null` la primera vez para que el repositorio siembre ejemplos. */
    fun loadAnnouncements(): List<ParentAnnouncement>? {
        val raw = prefs.getString(KEY_ANNOUNCEMENTS, null) ?: return null
        return runCatching {
            val array = JSONArray(raw)
            (0 until array.length()).mapNotNull { index ->
                array.optJSONObject(index)?.let { announcementFromJson(it) }
            }
        }.getOrDefault(emptyList())
    }

    fun saveAnnouncements(announcements: List<ParentAnnouncement>) {
        val array = JSONArray()
        announcements.forEach { announcement ->
            array.put(
                JSONObject().apply {
                    put("id", announcement.id)
                    put("authorUid", announcement.authorUid)
                    put("author", announcement.author)
                    put("teamId", announcement.teamId.orEmpty())
                    put("audience", announcement.audience.value)
                    put("title", announcement.title)
                    put("body", announcement.body)
                    put("createdAt", announcement.createdAt.toString())
                    put("pinned", announcement.pinned)
                    put("isRead", announcement.isRead)
                }
            )
        }
        prefs.edit { putString(KEY_ANNOUNCEMENTS, array.toString()) }
    }

    private fun announcementFromJson(json: JSONObject): ParentAnnouncement? {
        val createdAt = runCatching {
            LocalDateTime.parse(json.optString("createdAt"))
        }.getOrNull() ?: LocalDateTime.now()
        return ParentAnnouncement(
            id = json.optString("id"),
            authorUid = json.optString("authorUid"),
            author = json.optString("author").ifBlank { "Academia SportPro" },
            teamId = json.optString("teamId").takeIf { it.isNotBlank() },
            audience = AnnouncementAudience.entries.firstOrNull {
                it.value == json.optString("audience")
            } ?: AnnouncementAudience.TODOS,
            title = json.optString("title"),
            body = json.optString("body"),
            createdAt = createdAt,
            pinned = json.optBoolean("pinned"),
            isRead = json.optBoolean("isRead")
        )
    }

    /** Notificaciones guardadas con su fecha real y estado de lectura. */
    fun loadNotifications(): List<ParentNotification> {
        val raw = prefs.getString(KEY_NOTIFICATIONS, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            (0 until array.length()).mapNotNull { index ->
                array.optJSONObject(index)?.let { notificationFromJson(it) }
            }
        }.getOrDefault(emptyList())
    }

    fun saveNotifications(notifications: List<ParentNotification>) {
        val array = JSONArray()
        notifications.forEach { notification ->
            array.put(
                JSONObject().apply {
                    put("id", notification.id)
                    put("title", notification.title)
                    put("body", notification.body)
                    put("createdAt", notification.createdAt.toString())
                    put("isRead", notification.isRead)
                }
            )
        }
        prefs.edit { putString(KEY_NOTIFICATIONS, array.toString()) }
    }

    private fun notificationFromJson(json: JSONObject): ParentNotification? {
        val createdAt = runCatching {
            LocalDateTime.parse(json.optString("createdAt"))
        }.getOrNull() ?: LocalDateTime.now()
        return ParentNotification(
            id = json.optString("id"),
            title = json.optString("title"),
            body = json.optString("body"),
            createdAt = createdAt,
            isRead = json.optBoolean("isRead")
        )
    }

    private fun childFromJson(json: JSONObject): LinkedChild? {
        val stats = json.optJSONObject("stats")
        val privacyJson = json.optJSONObject("privacySettings")
        val privacySettings = if (privacyJson != null) {
            devs.grupo5.sportpro.data.model.PrivacySettings(
                profilePhoto = privacyJson.optString("profilePhoto", "Público"),
                fullNameVisibility = privacyJson.optString("fullNameVisibility", "Público"),
                statsVisibility = privacyJson.optString("statsVisibility", "Restringido")
            )
        } else devs.grupo5.sportpro.data.model.PrivacySettings()
        
        return LinkedChild(
            id = json.optString("id"),
            firstName = json.optString("firstName"),
            lastName = json.optString("lastName"),
            category = json.optString("category"),
            age = json.optInt("age"),
            jerseyNumber = json.optInt("jerseyNumber"),
            position = json.optString("position"),
            birthDate = json.optString("birthDate"),
            heightCm = json.optInt("heightCm"),
            weightKg = json.optInt("weightKg"),
            phone = json.optString("phone"),
            emergencyPhone = json.optString("emergencyPhone"),
            parentRelation = json.optString("parentRelation"),
            status = json.optString("status").ifBlank { "Activo" },
            stats = ChildStats(
                goals = stats?.optInt("goals") ?: 0,
                assists = stats?.optInt("assists") ?: 0,
                matchesPlayed = stats?.optInt("matchesPlayed") ?: 0,
                attendancePercent = stats?.optInt("attendancePercent") ?: 0
            ),
            privacySettings = privacySettings
        )
    }

    private fun childToJson(child: LinkedChild): JSONObject = JSONObject().apply {
        put("id", child.id)
        put("firstName", child.firstName)
        put("lastName", child.lastName)
        put("category", child.category)
        put("age", child.age)
        put("jerseyNumber", child.jerseyNumber)
        put("position", child.position)
        put("birthDate", child.birthDate)
        put("heightCm", child.heightCm)
        put("weightKg", child.weightKg)
        put("phone", child.phone)
        put("emergencyPhone", child.emergencyPhone)
        put("parentRelation", child.parentRelation)
        put("status", child.status)
        put(
            "stats",
            JSONObject().apply {
                put("goals", child.stats.goals)
                put("assists", child.stats.assists)
                put("matchesPlayed", child.stats.matchesPlayed)
                put("attendancePercent", child.stats.attendancePercent)
            }
        )
        put(
            "privacySettings",
            JSONObject().apply {
                put("profilePhoto", child.privacySettings.profilePhoto)
                put("fullNameVisibility", child.privacySettings.fullNameVisibility)
                put("statsVisibility", child.privacySettings.statsVisibility)
            }
        )
    }

    // --- Estado local del repositorio Firebase ---

    /**
     * Menores creados o editados localmente por el apoderado. Nunca se escriben en Firestore:
     * la vinculación real la gestiona la academia mediante la Cloud Function del punto 4.
     */
    fun loadLocalChildren(): List<LinkedChild> = loadChildrenFrom(KEY_LOCAL_CHILDREN)

    fun saveLocalChildren(children: List<LinkedChild>) = saveChildrenTo(KEY_LOCAL_CHILDREN, children)

    /** Menores ocultados localmente por el apoderado (no afectan a Firestore). */
    fun loadHiddenChildIds(): Set<String> {
        val raw = prefs.getString(KEY_HIDDEN_CHILDREN, null) ?: return emptySet()
        return runCatching {
            val array = JSONArray(raw)
            (0 until array.length()).mapNotNull { array.optString(it).takeIf { id -> id.isNotBlank() } }.toSet()
        }.getOrDefault(emptySet())
    }

    fun saveHiddenChildIds(ids: Set<String>) {
        val array = JSONArray()
        ids.forEach { array.put(it) }
        prefs.edit { putString(KEY_HIDDEN_CHILDREN, array.toString()) }
    }

    fun loadReadAnnouncementIds(): Set<String> {
        val raw = prefs.getString(KEY_READ_ANNOUNCEMENTS, null) ?: return emptySet()
        return runCatching {
            val array = JSONArray(raw)
            (0 until array.length()).mapNotNull { array.optString(it).takeIf { id -> id.isNotBlank() } }.toSet()
        }.getOrDefault(emptySet())
    }

    fun saveReadAnnouncementIds(ids: Set<String>) {
        val array = JSONArray()
        ids.forEach { array.put(it) }
        prefs.edit { putString(KEY_READ_ANNOUNCEMENTS, array.toString()) }
    }

    fun loadReadNotificationIds(): Set<String> {
        val raw = prefs.getString(KEY_READ_NOTIFICATIONS, null) ?: return emptySet()
        return runCatching {
            val array = JSONArray(raw)
            (0 until array.length()).mapNotNull { array.optString(it).takeIf { id -> id.isNotBlank() } }.toSet()
        }.getOrDefault(emptySet())
    }

    fun saveReadNotificationIds(ids: Set<String>) {
        val array = JSONArray()
        ids.forEach { array.put(it) }
        prefs.edit { putString(KEY_READ_NOTIFICATIONS, array.toString()) }
    }

    private fun loadChildrenFrom(key: String): List<LinkedChild> {
        val raw = prefs.getString(key, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            (0 until array.length()).mapNotNull { index ->
                array.optJSONObject(index)?.let { childFromJson(it) }
            }
        }.getOrDefault(emptyList())
    }

    private fun saveChildrenTo(key: String, children: List<LinkedChild>) {
        val array = JSONArray()
        children.forEach { array.put(childToJson(it)) }
        prefs.edit { putString(key, array.toString()) }
    }

    private companion object {
        const val PREFS_NAME = "sportpro_parent_module"
        const val KEY_GUARDIAN = "guardian"
        const val KEY_CHILD = "child"
        const val KEY_CHILDREN = "children"
        const val KEY_LOCAL_CHILDREN = "localChildren"
        const val KEY_HIDDEN_CHILDREN = "hiddenChildIds"
        const val KEY_READ_ANNOUNCEMENTS = "readAnnouncementIds"
        const val KEY_SELECTED_CHILD = "selectedChildId"
        const val KEY_PAYMENT = "payment"
        const val KEY_PAYMENTS = "payments"
        const val KEY_ANNOUNCEMENTS = "announcements"
        const val KEY_NOTIFICATIONS = "notifications"
        const val KEY_READ_NOTIFICATIONS = "readNotificationIds"
    }
}
