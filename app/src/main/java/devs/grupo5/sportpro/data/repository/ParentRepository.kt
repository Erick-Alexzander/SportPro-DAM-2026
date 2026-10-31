package devs.grupo5.sportpro.data.repository

import android.content.Context
import devs.grupo5.sportpro.data.model.ChildStats
import devs.grupo5.sportpro.data.model.GuardianProfile
import devs.grupo5.sportpro.data.model.GuardianTitle
import devs.grupo5.sportpro.data.model.LinkedChild
import devs.grupo5.sportpro.data.model.MonthlyPayment
import devs.grupo5.sportpro.data.model.ParentNotification
import devs.grupo5.sportpro.data.model.PaymentStatus
import devs.grupo5.sportpro.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface ParentRepository {
    val guardian: StateFlow<GuardianProfile>

    /** Menores vinculados a la cuenta responsable. */
    val linkedChildren: StateFlow<List<LinkedChild>>

    /** Identificador del menor que se está visualizando en el módulo. */
    val selectedChildId: StateFlow<String?>

    val payment: StateFlow<MonthlyPayment>
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

    fun updatePaymentStatus(status: PaymentStatus)

    /** Registra el comprobante de pago enviado por el apoderado. */
    fun registerReceipt()

    fun markNotificationsAsRead()
}

class MockParentRepository private constructor(
    private val context: Context? = null
) : ParentRepository {
    
    private var storage: ParentStorage? = null

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
                phone = "+56 9 4321 0987",
                emergencyPhone = "+56 9 5555 0187",
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

    private val _payment = MutableStateFlow(
        MonthlyPayment(
            period = "Nov 2024",
            status = PaymentStatus.PENDIENTE,
            receiptRegistered = false
        )
    )
    override val payment: StateFlow<MonthlyPayment> = _payment.asStateFlow()

    private val _notifications = MutableStateFlow(
        listOf(
            ParentNotification(
                id = "n1",
                title = "Convocatoria confirmada",
                body = "Felipe fue convocado para el partido vs. Deportes Centro este sábado a las 10:00.",
                time = "Hace 2h"
            ),
            ParentNotification(
                id = "n2",
                title = "Mensualidad pendiente",
                body = "La mensualidad de Nov 2024 sigue pendiente de regularización.",
                time = "Hace 6h"
            ),
            ParentNotification(
                id = "n3",
                title = "Nuevo comunicado",
                body = "La academia publicó el calendario de días para la segunda quincena.",
                time = "Ayer"
            )
        )
    )
    override val notifications: StateFlow<List<ParentNotification>> = _notifications.asStateFlow()

    init {
        // Initial setup handled dynamically in initFrom
    }

    override fun initFrom(user: UserProfile) {
        val uid = user.uid.ifBlank { "anonymous" }
        context?.let { ctx ->
            storage = ParentStorage(ctx, uid)
        }
        
        storage?.let { store ->
            store.loadGuardian()?.let { _guardian.value = it }
            val stored = store.loadChildren()
            if (stored.isNotEmpty()) {
                _linkedChildren.value = stored
                _selectedChildId.value = store.loadSelectedChildId()
                    ?.takeIf { id -> stored.any { it.id == id } }
                    ?: stored.first().id
            } else {
                _linkedChildren.value = emptyList() // Clear previous mock data from memory
                _selectedChildId.value = null
            }
            store.loadPayment()?.let { _payment.value = it }
        }
        
        _guardian.value = _guardian.value.copy(
            firstName = user.firstName.ifBlank { _guardian.value.firstName },
            lastName = user.lastName.ifBlank { _guardian.value.lastName },
            email = user.email.ifBlank { _guardian.value.email }
        )
        storage?.saveGuardian(_guardian.value)
    }

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
        persistChildren()
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
        persistChildren()
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

    override fun updatePaymentStatus(status: PaymentStatus) {
        _payment.value = _payment.value.copy(status = status)
        storage?.savePayment(_payment.value)
    }

    override fun registerReceipt() {
        _payment.value = _payment.value.copy(receiptRegistered = true)
        storage?.savePayment(_payment.value)
    }

    override fun markNotificationsAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
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
