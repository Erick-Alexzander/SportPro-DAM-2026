package devs.grupo5.sportpro.data.repository

import android.content.Context
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import devs.grupo5.sportpro.data.model.AnnouncementAudience
import devs.grupo5.sportpro.data.model.ChildStats
import devs.grupo5.sportpro.data.model.GuardianProfile
import devs.grupo5.sportpro.data.model.LinkedChild
import devs.grupo5.sportpro.data.model.ParentAnnouncement
import devs.grupo5.sportpro.data.model.ParentNotification
import devs.grupo5.sportpro.data.model.ParentPayment
import devs.grupo5.sportpro.data.model.PaymentStatus
import devs.grupo5.sportpro.data.model.PrivacySettings
import devs.grupo5.sportpro.data.model.UserProfile
import devs.grupo5.sportpro.data.model.currentPaymentPeriod
import devs.grupo5.sportpro.presentation.parent.calendar.ParentEvent
import devs.grupo5.sportpro.presentation.parent.calendar.ParentEventType
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Repositorio del módulo apoderado con Firestore como fuente de datos.
 *
 * Datos leídos de Firestore:
 * - `users/{uid}` → perfil del apoderado.
 * - `players` donde `guardianUids` contiene el uid → menores vinculados por la academia.
 * - `payments` donde `parentUid` es el uid → mensualidades.
 * - `announcements` con audiencia TODOS/APODERADOS → comunicados.
 *
 * El apoderado NO puede vincularse a un jugador ni cambiar el estado de una mensualidad:
 * `guardianUids` y `status` solo los gestiona la academia (Cloud Function del punto 4).
 * Los cambios de menores y el estado de lectura se guardan solo como estado local de UI.
 */
class FirebaseParentRepository private constructor(private val appContext: Context?) : ParentRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var storage: ParentStorage? = null
    private var parentUid: String = ""

    // Espejo local de lo que llega de Firestore, para poder recombinarlo con los drafts locales.
    private var remoteChildren: List<LinkedChild> = emptyList()
    private var localChildren: List<LinkedChild> = emptyList()
    private var hiddenChildIds: Set<String> = emptySet()
    private var readAnnouncementIds: Set<String> = emptySet()
    private var readNotificationIds: Set<String> = emptySet()
    private var remoteAnnouncements: List<ParentAnnouncement> = emptyList()

    private val _guardian = MutableStateFlow(GuardianProfile())
    override val guardian: StateFlow<GuardianProfile> = _guardian.asStateFlow()

    private val _linkedChildren = MutableStateFlow<List<LinkedChild>>(emptyList())
    override val linkedChildren: StateFlow<List<LinkedChild>> = _linkedChildren.asStateFlow()

    private val _selectedChildId = MutableStateFlow<String?>(null)
    override val selectedChildId: StateFlow<String?> = _selectedChildId.asStateFlow()

    private val _payments = MutableStateFlow<List<ParentPayment>>(emptyList())
    override val payments: StateFlow<List<ParentPayment>> = _payments.asStateFlow()

    private val _announcements = MutableStateFlow<List<ParentAnnouncement>>(emptyList())
    override val announcements: StateFlow<List<ParentAnnouncement>> = _announcements.asStateFlow()

    private val _calendarEvents = MutableStateFlow<List<ParentEvent>>(emptyList())
    override val calendarEvents: StateFlow<List<ParentEvent>> = _calendarEvents.asStateFlow()

    private val _notifications = MutableStateFlow<List<ParentNotification>>(emptyList())
    override val notifications: StateFlow<List<ParentNotification>> = _notifications.asStateFlow()

    private val listeners = mutableListOf<ListenerRegistration>()
    private val calendarListeners = mutableListOf<ListenerRegistration>()
    private var linkedTeamIds: Set<String> = emptySet()
    private var remoteTrainings: List<ParentEvent> = emptyList()
    private var remoteMatches: List<ParentEvent> = emptyList()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser == null) stopListening()
        }
    }

    override fun initFrom(user: UserProfile) {
        if (parentUid == user.uid && listeners.isNotEmpty()) return

        stopListening()
        parentUid = user.uid
        storage = appContext?.let { ParentStorage(it, user.uid) }

        localChildren = storage?.loadLocalChildren().orEmpty()
        hiddenChildIds = storage?.loadHiddenChildIds().orEmpty()
        readAnnouncementIds = storage?.loadReadAnnouncementIds().orEmpty()
        readNotificationIds = storage?.loadReadNotificationIds().orEmpty()

        _guardian.value = GuardianProfile(
            firstName = user.firstName,
            lastName = user.lastName,
            email = user.email
        )
        storage?.saveGuardian(_guardian.value)

        val storedSelectedId = storage?.loadSelectedChildId()
        _selectedChildId.value = storedSelectedId?.takeIf { id -> _linkedChildren.value.any { it.id == id } }
        recomputeNotifications()

        startListening(user.uid)
    }

    private fun startListening(uid: String) {
        listeners += firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "No se pudo leer users/$uid", error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    _guardian.value = guardianFromDoc(snapshot)
                }
            }

        listeners += firestore.collection("players")
            .whereArrayContains("guardianUids", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "No se pudieron leer los menores vinculados", error)
                    return@addSnapshotListener
                }
                remoteChildren = snapshot?.documents?.mapNotNull { playerFromDoc(it) }.orEmpty()
                recomputeChildren()
            }

        listeners += firestore.collection("payments")
            .whereEqualTo("parentUid", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "No se pudieron leer las mensualidades", error)
                    return@addSnapshotListener
                }
                _payments.value = snapshot?.documents?.mapNotNull { paymentFromDoc(it) }
                    .orEmpty()
                    .sortedWith(compareBy({ it.playerId }, { it.period }))
                recomputeNotifications()
            }

        listeners += firestore.collection("announcements")
            .whereIn("audience", listOf("TODOS", "APODERADOS"))
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "No se pudieron leer los comunicados", error)
                    return@addSnapshotListener
                }
                remoteAnnouncements = snapshot?.documents?.mapNotNull { announcementFromDoc(it) }.orEmpty()
                applyAnnouncements()
            }

        listeners += firestore.collection("parentAccess").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "No se pudo leer parentAccess/$uid", error)
                    return@addSnapshotListener
                }
                val teamIds = snapshot?.get("teamIds") as? List<*> ?: emptyList<Any>()
                val newIds = teamIds.filterIsInstance<String>().filter { it.isNotBlank() }.toSet()
                if (newIds != linkedTeamIds) {
                    linkedTeamIds = newIds
                    refreshCalendarListeners()
                }
            }
    }

    private fun refreshCalendarListeners() {
        calendarListeners.forEach { it.remove() }
        calendarListeners.clear()
        remoteTrainings = emptyList()
        remoteMatches = emptyList()
        if (linkedTeamIds.isEmpty()) {
            recomputeCalendar()
            return
        }
        val ids = linkedTeamIds.toList().take(10)
        calendarListeners += firestore.collection("trainings")
            .whereIn("teamId", ids)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "No se pudieron leer entrenamientos", error)
                    return@addSnapshotListener
                }
                remoteTrainings = snapshot?.documents?.mapNotNull { trainingEventFromDoc(it) }.orEmpty()
                recomputeCalendar()
            }
        calendarListeners += firestore.collection("matches")
            .whereIn("teamId", ids)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "No se pudieron leer partidos", error)
                    return@addSnapshotListener
                }
                remoteMatches = snapshot?.documents?.mapNotNull { matchEventFromDoc(it) }.orEmpty()
                recomputeCalendar()
            }
    }

    private fun recomputeCalendar() {
        _calendarEvents.value = (remoteTrainings + remoteMatches)
            .sortedBy { it.startAt }
        recomputeNotifications()
    }

    private fun stopListening() {
        listeners.forEach { it.remove() }
        listeners.clear()
        calendarListeners.forEach { it.remove() }
        calendarListeners.clear()
        linkedTeamIds = emptySet()
        remoteTrainings = emptyList()
        remoteMatches = emptyList()
        remoteChildren = emptyList()
        remoteAnnouncements = emptyList()
        _linkedChildren.value = emptyList()
        _payments.value = emptyList()
        _announcements.value = emptyList()
        _calendarEvents.value = emptyList()
        _selectedChildId.value = null
        _guardian.value = GuardianProfile()
        _notifications.value = emptyList()
    }

    /** Combina los menores de Firestore con los cambios locales (que nunca se escriben en Firestore). */
    private fun recomputeChildren() {
        val remoteIds = remoteChildren.map { it.id }.toSet()

        // Si la academia volvió a vincular un menor, no debe quedar oculto localmente.
        if (hiddenChildIds.any { it in remoteIds }) {
            hiddenChildIds = hiddenChildIds - remoteIds
            storage?.saveHiddenChildIds(hiddenChildIds)
        }

        val combined = remoteChildren.map { remote ->
            localChildren.firstOrNull { it.id == remote.id } ?: remote
        }.toMutableList()

        combined += localChildren.filter { it.id !in remoteIds }

        val visible = combined.filterNot { it.id in hiddenChildIds }
            .distinctBy { it.id }
            .sortedBy { it.fullName }
        _linkedChildren.value = visible

        val currentSelected = _selectedChildId.value
        _selectedChildId.value = when {
            currentSelected != null && visible.any { it.id == currentSelected } -> currentSelected
            visible.isNotEmpty() -> visible.first().id
            else -> null
        }
        storage?.saveSelectedChildId(_selectedChildId.value)
        recomputeNotifications()
    }

    private fun applyAnnouncements() {
        _announcements.value = remoteAnnouncements.map { announcement ->
            announcement.copy(isRead = announcement.id in readAnnouncementIds)
        }.sortedByDescending { it.createdAt }
        recomputeNotifications()
    }

    override fun updateGuardian(profile: GuardianProfile) {
        _guardian.value = profile
        storage?.saveGuardian(profile)
        if (parentUid.isBlank()) return

        // Las reglas solo permiten firstName, lastName y birthDate en users/{uid}.
        firestore.collection("users").document(parentUid)
            .update(
                mapOf(
                    "firstName" to profile.firstName,
                    "lastName" to profile.lastName
                )
            )
            .addOnFailureListener { error -> Log.w(TAG, "No se pudo actualizar el perfil", error) }
    }

    override fun linkChild(child: LinkedChild): ChildLinkResult {
        // No se escribe en Firestore: la vinculaci?n la aprueba la academia.
        localChildren = localChildren.filterNot { it.id == child.id } + child
        hiddenChildIds = hiddenChildIds - child.id
        recomputeChildren()
        persistLocalChildren()
        return ChildLinkResult.Success
    }

    override fun updateChild(child: LinkedChild) {
        localChildren = localChildren.filterNot { it.id == child.id } + child
        recomputeChildren()
        persistLocalChildren()

        // Si el menor viene de Firestore (jugador real vinculado), persistir campos permitidos.
        if (parentUid.isNotBlank() && remoteChildren.any { it.id == child.id }) {
            val updates = hashMapOf<String, Any>(
                "privacySettings" to hashMapOf(
                    "profilePhoto" to child.privacySettings.profilePhoto,
                    "fullNameVisibility" to child.privacySettings.fullNameVisibility,
                    "statsVisibility" to child.privacySettings.statsVisibility
                ),
                "parentPhone" to child.phone,
                "emergencyPhone" to child.emergencyPhone,
                "dateOfBirth" to child.birthDate,
                "age" to child.currentAge,
                "weight" to child.weightKg.toDouble()
            )
            firestore.collection("players").document(child.id)
                .update(updates)
                .addOnFailureListener { error -> Log.w(TAG, "No se pudo actualizar el menor ${child.id}", error) }
        }
    }

    override fun unlinkChild(childId: String) {
        val isRemoteChild = remoteChildren.any { it.id == childId }
        hiddenChildIds = hiddenChildIds + childId
        localChildren = localChildren.filterNot { it.id == childId }
        recomputeChildren()
        persistLocalChildren()

        if (isRemoteChild && parentUid.isNotBlank()) {
            scope.launch {
                try {
                    firestore.collection("players").document(childId)
                        .update("guardianUids", FieldValue.arrayRemove(parentUid))
                        .await()

                    val parentAccessRef = firestore.collection("parentAccess").document(parentUid)
                    val accessSnap = parentAccessRef.get().await()
                    if (accessSnap.exists()) {
                        val currentPlayerIds = (accessSnap.get("playerIds") as? List<*>)
                            ?.filterIsInstance<String>().orEmpty()
                        val remainingPlayerIds = currentPlayerIds.filter { it != childId }
                        val remainingTeamIds = remoteChildren
                            .filter { it.id != childId && it.id in remainingPlayerIds }
                            .mapNotNull { child ->
                                // teamId no está en LinkedChild; se recalcula desde parentAccess previo menos el equipo del hijo removido si queda huérfano.
                                null
                            }
                        // Recalcular teamIds desde Firestore para precisión.
                        val teamIds = mutableSetOf<String>()
                        for (pid in remainingPlayerIds) {
                            val pSnap = firestore.collection("players").document(pid).get().await()
                            val tid = pSnap.getString("teamId")
                            if (!tid.isNullOrBlank()) teamIds.add(tid)
                        }
                        parentAccessRef.set(
                            mapOf(
                                "parentUid" to parentUid,
                                "playerIds" to remainingPlayerIds,
                                "teamIds" to teamIds.toList()
                            )
                        ).await()
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "No se pudo desvincular al menor $childId en Firestore", e)
                }
            }
        }
    }

    private fun persistLocalChildren() {
        storage?.saveLocalChildren(localChildren)
        storage?.saveHiddenChildIds(hiddenChildIds)
    }

    override fun selectChild(childId: String) {
        if (_linkedChildren.value.any { it.id == childId }) {
            _selectedChildId.value = childId
            storage?.saveSelectedChildId(childId)
        }
    }

    override fun registerReceipt(playerId: String) {
        val period = currentPaymentPeriod()
        val payment = _payments.value.firstOrNull {
            it.playerId == playerId && it.period == period
        } ?: return
        if (parentUid.isBlank()) return

        val now = LocalDateTime.now()
        val updated = payment.copy(
            receiptRegistered = true,
            updatedAt = now,
            updatedBy = parentUid
        )

        // status nunca se envía: la academia define PENDIENTE/PAGADO.
        firestore.collection("payments").document(payment.id)
            .update(
                mapOf(
                    "receiptRegistered" to true,
                    "receiptUrl" to (updated.receiptUrl.orEmpty()),
                    "updatedAt" to toFirestoreTimestamp(now),
                    "updatedBy" to parentUid
                )
            )
            .addOnSuccessListener {
                _payments.value = _payments.value.map { if (it.id == payment.id) updated else it }
            }
            .addOnFailureListener { error ->
                Log.w(TAG, "No se pudo registrar el comprobante de ${payment.id}", error)
            }
    }

    override fun markAnnouncementAsRead(announcementId: String) {
        readAnnouncementIds = readAnnouncementIds + announcementId
        storage?.saveReadAnnouncementIds(readAnnouncementIds)
        applyAnnouncements()
    }

    override fun markNotificationsAsRead() {
        val currentIds = _notifications.value.map { it.id }.toSet()
        readNotificationIds = readNotificationIds + currentIds
        storage?.saveReadNotificationIds(readNotificationIds)
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    private fun recomputeNotifications() {
        val now = LocalDateTime.now()
        val derived = mutableListOf<ParentNotification>()

        val period = currentPaymentPeriod()
        _payments.value
            .filter { it.period == period && it.status == PaymentStatus.PENDIENTE }
            .forEach { payment ->
                val childName = _linkedChildren.value.firstOrNull { it.id == payment.playerId }?.fullName
                    ?: "tu menor"
                derived += ParentNotification(
                    id = "pay_${payment.id}",
                    title = "Mensualidad pendiente",
                    body = "La mensualidad de $childName ($period) está pendiente.",
                    createdAt = payment.updatedAt ?: now
                )
            }

        _announcements.value
            .filter { !it.isRead }
            .forEach { announcement ->
                derived += ParentNotification(
                    id = "ann_${announcement.id}",
                    title = "Nuevo comunicado",
                    body = announcement.title.ifBlank { "La academia publicó un comunicado." },
                    createdAt = announcement.createdAt
                )
            }

        _calendarEvents.value
            .filter { !it.startAt.isBefore(now) && it.startAt.isBefore(now.plusHours(48)) }
            .forEach { event ->
                derived += ParentNotification(
                    id = "evt_${event.id}",
                    title = "Próximo evento",
                    body = "${event.title} · ${event.day}/${event.month} ${event.time}",
                    createdAt = now
                )
            }

        val unique = derived
            .distinctBy { it.id }
            .sortedByDescending { it.createdAt }
            .map { it.copy(isRead = it.id in readNotificationIds) }

        _notifications.value = unique
    }

    private fun guardianFromDoc(doc: DocumentSnapshot) = GuardianProfile(
        firstName = doc.getString("firstName").orEmpty(),
        lastName = doc.getString("lastName").orEmpty(),
        email = doc.getString("email").orEmpty()
    )

    private fun playerFromDoc(doc: DocumentSnapshot): LinkedChild? {
        val fullName = doc.getString("fullName").orEmpty()
        val (firstName, lastName) = splitName(fullName)
        val privacy = doc.get("privacySettings") as? Map<*, *>

        return LinkedChild(
            id = doc.id,
            firstName = firstName,
            lastName = lastName,
            category = doc.getString("category").orEmpty(),
            age = doc.getLong("age")?.toInt() ?: 0,
            jerseyNumber = doc.getLong("jerseyNumber")?.toInt() ?: 0,
            position = doc.getString("position").orEmpty(),
            birthDate = doc.getString("dateOfBirth").orEmpty(),
            weightKg = doc.getDouble("weight")?.toInt() ?: 0,
            phone = doc.getString("parentPhone").orEmpty(),
            emergencyPhone = doc.getString("emergencyPhone").orEmpty(),
            parentRelation = doc.getString("parentRelation").orEmpty(),
            status = doc.getString("status").takeIf { it.isNullOrBlank().not() } ?: "Activo",
            stats = ChildStats(),
            privacySettings = PrivacySettings(
                profilePhoto = privacy?.get("profilePhoto") as? String ?: "Público",
                fullNameVisibility = privacy?.get("fullNameVisibility") as? String ?: "Público",
                statsVisibility = privacy?.get("statsVisibility") as? String ?: "Restringido"
            )
        )
    }

    private fun paymentFromDoc(doc: DocumentSnapshot): ParentPayment? = ParentPayment(
        id = doc.id,
        ownerUid = doc.getString("ownerUid").orEmpty(),
        parentUid = doc.getString("parentUid").orEmpty(),
        playerId = doc.getString("playerId").orEmpty(),
        period = doc.getString("period").takeIf { it.isNullOrBlank().not() } ?: currentPaymentPeriod(),
        status = doc.getString("status")?.let { value ->
            runCatching { PaymentStatus.valueOf(value) }.getOrNull()
        } ?: PaymentStatus.PENDIENTE,
        receiptRegistered = doc.getBoolean("receiptRegistered") ?: false,
        receiptUrl = doc.getString("receiptUrl"),
        updatedAt = toLocalDateTime(doc.get("updatedAt")),
        updatedBy = doc.getString("updatedBy").orEmpty()
    )

    private fun announcementFromDoc(doc: DocumentSnapshot): ParentAnnouncement? {
        val audienceValue = doc.getString("audience").orEmpty()
        return ParentAnnouncement(
            id = doc.id,
            authorUid = doc.getString("authorUid").orEmpty(),
            author = doc.getString("author")?.takeIf { it.isNotBlank() } ?: "Academia SportPro",
            teamId = doc.getString("teamId"),
            audience = AnnouncementAudience.entries.firstOrNull {
                it.value == audienceValue || it.name == audienceValue
            } ?: AnnouncementAudience.TODOS,
            title = doc.getString("title").orEmpty(),
            body = doc.getString("body").orEmpty(),
            createdAt = toLocalDateTime(doc.get("createdAt")),
            pinned = doc.getBoolean("pinned") ?: false,
            isRead = doc.id in readAnnouncementIds
        )
    }

    private fun splitName(fullName: String): Pair<String, String> {
        if (fullName.isBlank()) return "" to ""
        val parts = fullName.trim().split(Regex("\\s+"), limit = 2)
        return parts.first() to parts.getOrNull(1).orEmpty()
    }

    private fun toLocalDateTime(value: Any?): LocalDateTime = when (value) {
        is Timestamp -> Instant.ofEpochSecond(value.seconds, value.nanoseconds.toLong())
            .atZone(ZoneId.systemDefault()).toLocalDateTime()
        is Date -> Instant.ofEpochMilli(value.time).atZone(ZoneId.systemDefault()).toLocalDateTime()
        is String -> runCatching { LocalDateTime.parse(value) }.getOrDefault(LocalDateTime.now())
        else -> LocalDateTime.now()
    }

    private fun toFirestoreTimestamp(value: LocalDateTime): Timestamp =
        Timestamp(Date.from(value.atZone(ZoneId.systemDefault()).toInstant()))

    private fun parseDateTime(date: String?, time: String?): LocalDateTime? {
        if (date.isNullOrBlank()) return null
        val t = time?.takeIf { it.isNotBlank() } ?: "00:00"
        val patterns = listOf("yyyy-MM-dd HH:mm", "dd/MM/yyyy HH:mm", "yyyy/MM/dd HH:mm", "dd-MM-yyyy HH:mm")
        for (p in patterns) {
            try {
                return LocalDateTime.parse("$date $t", java.time.format.DateTimeFormatter.ofPattern(p))
            } catch (_: Exception) { }
        }
        return try {
            LocalDateTime.parse("${date}T${t}")
        } catch (_: Exception) { null }
    }

    private fun trainingEventFromDoc(doc: DocumentSnapshot): ParentEvent? {
        val startAt = parseDateTime(doc.getString("date"), doc.getString("startTime")) ?: return null
        val title = doc.getString("title").orEmpty().ifBlank { "Entrenamiento" }
        val category = doc.getString("category").orEmpty()
        val duration = doc.getLong("durationMinutes")?.toInt() ?: 0
        val subtitle = listOf(category, if (duration > 0) "$duration min" else "").filter { it.isNotBlank() }.joinToString(" ? ")
        return ParentEvent(
            id = "tr_${doc.id}",
            type = ParentEventType.ENTRENAMIENTO,
            startAt = startAt,
            title = title,
            subtitle = subtitle,
            place = "Academia SportPro",
            category = category
        )
    }

    private fun matchEventFromDoc(doc: DocumentSnapshot): ParentEvent? {
        val startAt = parseDateTime(doc.getString("date"), doc.getString("time")) ?: return null
        val rival = doc.getString("rival").orEmpty().ifBlank { "Rival" }
        val category = doc.getString("category").orEmpty()
        val stadium = doc.getString("stadium").orEmpty().ifBlank { "Por definir" }
        val isHome = doc.getBoolean("isHome") ?: true
        val subtitle = listOf(category, if (isHome) "Local" else "Visitante", stadium).filter { it.isNotBlank() }.joinToString(" ? ")
        return ParentEvent(
            id = "mt_${doc.id}",
            type = ParentEventType.PARTIDO,
            startAt = startAt,
            title = "vs. $rival",
            subtitle = subtitle,
            place = stadium,
            category = category
        )
    }

    override fun clear() {
        listeners.forEach { it.remove() }
        listeners.clear()
        calendarListeners.forEach { it.remove() }
        calendarListeners.clear()
        parentUid = ""
        remoteChildren = emptyList()
        localChildren = emptyList()
        hiddenChildIds = emptySet()
        readAnnouncementIds = emptySet()
        readNotificationIds = emptySet()
        remoteAnnouncements = emptyList()
        remoteTrainings = emptyList()
        remoteMatches = emptyList()
        _guardian.value = GuardianProfile()
        _linkedChildren.value = emptyList()
        _selectedChildId.value = null
        _payments.value = emptyList()
        _announcements.value = emptyList()
        _calendarEvents.value = emptyList()
        _notifications.value = emptyList()
    }

    companion object {
        private const val TAG = "FirebaseParentRepository"

        @Volatile
        private var instance: FirebaseParentRepository? = null

        fun getInstance(context: Context? = null): FirebaseParentRepository =
            instance ?: synchronized(this) {
                instance ?: FirebaseParentRepository(context?.applicationContext).also { instance = it }
            }
    }
}
