package devs.grupo5.sportpro.data.model

/**
 * Forma en que el apoderado es tratado dentro de la aplicación. Define el saludo y la etiqueta
 * que se muestran en el encabezado del módulo.
 */
enum class GuardianTitle(val label: String, val greeting: String) {
    APODERADO("Apoderado", "Bienvenido,"),
    APODERADA("Apoderada", "Bienvenida,")
}

/**
 * Datos editables del apoderado responsable.
 */
data class GuardianProfile(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val title: GuardianTitle = GuardianTitle.APODERADO
) {
    val fullName: String
        get() = listOf(firstName, lastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")

    val initials: String
        get() = listOf(firstName, lastName)
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
            .ifBlank { "?" }

    val roleLabel: String
        get() = "${title.label} · SportPro"
}

/**
 * Estado de la mensualidad. SportPro no procesa pagos: solo registra el estado reportado.
 */
enum class PaymentStatus(val label: String) {
    PENDIENTE("Pendiente"),
    PAGADO("Pagado")
}

/**
 * Registro de la mensualidad del periodo actual.
 */
data class MonthlyPayment(
    val period: String = "Nov 2024",
    val status: PaymentStatus = PaymentStatus.PENDIENTE,
    val receiptRegistered: Boolean = false
)

/**
 * Aviso recibido por el apoderado desde la academia.
 */
data class ParentNotification(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val time: String = "",
    val isRead: Boolean = false
)
