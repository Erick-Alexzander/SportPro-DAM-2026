package devs.grupo5.sportpro.data.repository

import android.content.Context
import devs.grupo5.sportpro.data.model.AnnouncementAudience
import devs.grupo5.sportpro.data.model.ChildStats
import devs.grupo5.sportpro.data.model.GuardianProfile
import devs.grupo5.sportpro.data.model.GuardianTitle
import devs.grupo5.sportpro.data.model.LinkedChild
import devs.grupo5.sportpro.data.model.ParentAnnouncement
import devs.grupo5.sportpro.data.model.ParentNotification
import devs.grupo5.sportpro.data.model.ParentPayment
import devs.grupo5.sportpro.data.model.UserProfile
import devs.grupo5.sportpro.data.model.currentPaymentPeriod
import devs.grupo5.sportpro.presentation.parent.calendar.ParentEvent
import java.time.LocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface ParentRepository {
    val guardian: StateFlow<GuardianProfile>

    /** Menores vinculados a la cuenta responsable. */
    val linkedChildren: StateFlow<List<LinkedChild>>

    /** Identificador del menor que se está visualizando en el módulo. */
    val selectedChildId: StateFlow<String?>

    /** Mensualidades de todos los menores y períodos conocidos. */
    val payments: StateFlow<List<ParentPayment>>

    /** Comunicados visibles para el apoderado. */
    val announcements: StateFlow<List<ParentAnnouncement>>

    /** Eventos de calendario (entrenamientos/partidos) de los equipos vinculados. */
    val calendarEvents: StateFlow<List<ParentEvent>>

    val notifications: StateFlow<List<ParentNotification>>

    val unreadNotifications: Int
        get() = notifications.value.count { !it.isRead }

    /** Carga los datos del apoderado autenticado como perfil editable del módulo. */
    fun initFrom(user: UserProfile)

    fun updateGuardian(profile: GuardianProfile)

    /** Vincula un menor a la cuenta responsable. Si ya existe, lo actualiza. */
    fun linkChild(child: LinkedChild)

    /** Actualiza los datos del menor ya vinculado (físicos, contacto, ficha). */
    fun updateChild(child: LinkedChild)

    /** Elimina un menor concreto de la cuenta responsable. */
    fun unlinkChild(childId: String)

    /** Cambia el menor visible en el módulo. */
    fun selectChild(childId: String)

    /**
     * Registra el comprobante de pago del período actual. El estado PENDIENTE/PAGADO lo
     * define la academia, por eso no existe una acción para cambiarlo desde el apoderado.
     */
    fun registerReceipt(playerId: String)

    fun markAnnouncementAsRead(announcementId: String)

    fun markNotificationsAsRead()
}

class MockParentRepository private constructor(
    private val context: Context? = null
) : ParentRepository {

    private var storage: ParentStorage? = null
    private var parentUid: String = ""

    private val _guardian = MutableStateFlow(
        GuardianProfile(
            firstName = "María",
            lastName = "González",
            email = "maria.gonzalez@sportpro.com",
            title = GuardianTitle.APODERADO
        )
    )
    override val guardian: StateFlow<GuardianProfile> = _guardian.asStateFlow()

    private val _linkedChildren = MutableStateFlow(
        listOf(
            LinkedChild(
                id = "lc_1",
                firstName = "Felipe",
                lastName = "Muñoz",
                category = "Sub-15",
                age = 15,
                jerseyNumber = 11,
                position = "DEL",
                birthDate = "2009-06-22",
                heightCm = 162,
                weightKg = 55,
                phone = "+51 943 210 987",
                emergencyPhone = "+51 955 550 187",
                status = "Activo",
                stats = ChildStats(
                    goals = 8,
                    assists = 3,
                    matchesPlayed = 14,
                    attendancePercent = 90
                )
            )
        )
    )
    override val linkedChildren: StateFlow<List<LinkedChild>> = _linkedChildren.asStateFlow()

    private val _selectedChildId = MutableStateFlow<String?>("lc_1")
    override val selectedChildId: StateFlow<String?> = _selectedChildId.asStateFlow()

    private val _payments = MutableStateFlow<List<ParentPayment>>(emptyList())
    override val payments: StateFlow<List<ParentPayment>> = _payments.asStateFlow()

    private val _announcements = MutableStateFlow<List<ParentAnnouncement>>(emptyList())
    override val announcements: StateFlow<List<ParentAnnouncement>> = _announcements.asStateFlow()

    private val _calendarEvents = MutableStateFlow<List<ParentEvent>>(emptyList())
    override val calendarEvents: StateFlow<List<ParentEvent>> = _calendarEvents.asStateFlow()

    private val _notifications = MutableStateFlow(defaultNotifications())
    override val notifications: StateFlow<List<ParentNotification>> = _notifications.asStateFlow()

    init {
        // Initial setup handled dynamically in initFrom
    }

    override fun initFrom(user: UserProfile) {
        val uid = user.uid.ifBlank { "anonymous" }
        parentUid = uid
        context?.let { ctx ->
            storage = ParentStorage(ctx, uid)
        }
        val store = storage

        // Reinicia todo el estado para no mezclar datos entre cuentas de apoderados.
        _guardian.value = store?.loadGuardian() ?: GuardianProfile()
        val storedChildren = store?.loadChildren().orEmpty()
        _linkedChildren.value = storedChildren
        _selectedChildId.value = if (storedChildren.isEmpty()) {
            null
        } else {
            store?.loadSelectedChildId()
                ?.takeIf { id -> storedChildren.any { it.id == id } }
                ?: storedChildren.first().id
        }

        val storedPayments = store?.loadPayments(storedChildren.firstOrNull()?.id).orEmpty()
        _payments.value = ensureCurrentPayments(storedChildren, storedPayments)
        store?.savePayments(_payments.value)

        val storedAnnouncements = store?.loadAnnouncements()
        _announcements.value = storedAnnouncements ?: defaultAnnouncements().also {
            store?.saveAnnouncements(it)
        }

        val storedNotifications = store?.loadNotifications().orEmpty()
        _notifications.value = if (storedNotifications.isEmpty()) {
            defaultNotifications().also { store?.saveNotifications(it) }
        } else {
            storedNotifications
        }

        _guardian.value = _guardian.value.copy(
            firstName = user.firstName.ifBlank { _guardian.value.firstName },
            lastName = user.lastName.ifBlank { _guardian.value.lastName },
            email = user.email.ifBlank { _guardian.value.email }
        )
        store?.saveGuardian(_guardian.value)
    }

    /** Garantiza una mensualidad del período actual por cada menor vinculado. */
    private fun ensureCurrentPayments(
        children: List<LinkedChild>,
        existing: List<ParentPayment>
    ): List<ParentPayment> {
        val period = currentPaymentPeriod()
        val known = existing.filter { payment -> children.any { it.id == payment.playerId } }
        val missing = children
            .filter { child -> known.none { it.playerId == child.id && it.period == period } }
            .map { newPayment(it.id) }
        return (known + missing)
            .distinctBy { "${it.playerId}_${it.period}" }
            .sortedWith(compareBy({ it.playerId }, { it.period }))
    }

    private fun newPayment(playerId: String): ParentPayment {
        val period = currentPaymentPeriod()
        return ParentPayment(
            id = "${parentUid}_${playerId}_$period",
            parentUid = parentUid,
            playerId = playerId,
            period = period,
            updatedAt = LocalDateTime.now(),
            updatedBy = parentUid
        )
    }

    private fun persistPayments() {
        storage?.savePayments(_payments.value)
    }

    private fun persistAnnouncements() {
        storage?.saveAnnouncements(_announcements.value)
    }

    private fun defaultNotifications(now: LocalDateTime = LocalDateTime.now()): List<ParentNotification> =
        listOf(
            ParentNotification(
                id = "n1",
                title = "Convocatoria confirmada",
                body = "Felipe fue convocado para el partido vs. Deportes Centro este sábado a las 10:00.",
                createdAt = now.minusHours(2)
            ),
            ParentNotification(
                id = "n2",
                title = "Mensualidad pendiente",
                body = "La mensualidad del período actual sigue pendiente de regularización.",
                createdAt = now.minusHours(6)
            ),
            ParentNotification(
                id = "n3",
                title = "Nuevo comunicado",
                body = "La academia publicó el calendario de días para la segunda quincena.",
                createdAt = now.minusDays(1)
            )
        ).sortedByDescending { it.createdAt }

    /** Comunicados de ejemplo con el esquema final; se reemplazarán por Firestore. */
    private fun defaultAnnouncements(now: LocalDateTime = LocalDateTime.now()): List<ParentAnnouncement> =
        listOf(
            ParentAnnouncement(
                id = "a1",
                title = "Inicio de la segunda quincena",
                body = "Los horarios de entrenamiento se mantienen hasta fin de mes. " +
                    "Este sábado no hay sesión por mantenimiento de la cancha.",
                author = "Academia SportPro",
                audience = AnnouncementAudience.TODOS,
                createdAt = now.minusHours(2),
                pinned = true
            ),
            ParentAnnouncement(
                id = "a2",
                title = "Recordatorio de mensualidad",
                body = "La mensualidad del período actual se regulariza antes del día 10. " +
                    "Recuerda registrar el comprobante en tu panel de apoderado.",
                author = "Administración SportPro",
                audience = AnnouncementAudience.APODERADOS,
                createdAt = now.minusHours(6)
            ),
            ParentAnnouncement(
                id = "a3",
                title = "Encuentro con las familias",
                body = "El club invita a las familias a conocer al equipo Sub-15 este sábado " +
                    "en la cancha de la academia.",
                author = "Coach Ramírez",
                audience = AnnouncementAudience.APODERADOS,
                createdAt = now.minusDays(1)
            )
        ).sortedByDescending { it.createdAt }

    override fun updateGuardian(profile: GuardianProfile) {
        _guardian.value = profile
        storage?.saveGuardian(profile)
    }

    override fun linkChild(child: LinkedChild) {
        val current = _linkedChildren.value
        val exists = current.any { it.id == child.id }
        _linkedChildren.value = if (exists) {
            current.map { if (it.id == child.id) child else it }
        } else {
            current + child
        }
        _selectedChildId.value = child.id

        if (_payments.value.none { it.playerId == child.id && it.period == currentPaymentPeriod() }) {
            _payments.value = _payments.value + newPayment(child.id)
        }
        persistChildren()
        persistPayments()
    }

    override fun updateChild(child: LinkedChild) {
        _linkedChildren.value = _linkedChildren.value.map {
            if (it.id == child.id) child else it
        }
        persistChildren()
    }

    override fun unlinkChild(childId: String) {
        val remaining = _linkedChildren.value.filterNot { it.id == childId }
        _linkedChildren.value = remaining
        if (_selectedChildId.value == childId) {
            _selectedChildId.value = remaining.firstOrNull()?.id
        }
        _payments.value = _payments.value.filterNot { it.playerId == childId }
        persistChildren()
        persistPayments()
    }

    override fun selectChild(childId: String) {
        if (_linkedChildren.value.any { it.id == childId }) {
            _selectedChildId.value = childId
            storage?.saveSelectedChildId(childId)
        }
    }

    private fun persistChildren() {
        storage?.saveChildren(_linkedChildren.value)
        storage?.saveSelectedChildId(_selectedChildId.value)
    }

    override fun registerReceipt(playerId: String) {
        val period = currentPaymentPeriod()
        val current = _payments.value
        val exists = current.any { it.playerId == playerId && it.period == period }

        _payments.value = if (exists) {
            current.map { payment ->
                if (payment.playerId == playerId && payment.period == period) {
                    payment.copy(
                        receiptRegistered = true,
                        updatedAt = LocalDateTime.now(),
                        updatedBy = parentUid
                    )
                } else {
                    payment
                }
            }
        } else {
            current + newPayment(playerId).copy(
                receiptRegistered = true,
                updatedAt = LocalDateTime.now(),
                updatedBy = parentUid
            )
        }
        persistPayments()
    }

    override fun markAnnouncementAsRead(announcementId: String) {
        _announcements.value = _announcements.value.map { announcement ->
            if (announcement.id == announcementId) announcement.copy(isRead = true) else announcement
        }
        persistAnnouncements()
    }

    override fun markNotificationsAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
        storage?.saveNotifications(_notifications.value)
    }

    companion object {
        @Volatile
        private var instance: MockParentRepository? = null

        /**
         * Instancia única del repositorio. Si se entrega un contexto, los datos se conservan
         * localmente entre sesiones (equivalente al `localStorage` del prototipo web).
         */
        fun getInstance(context: Context? = null): MockParentRepository {
            return instance ?: synchronized(this) {
                instance ?: MockParentRepository(context).also { instance = it }
            }
        }
    }
}
