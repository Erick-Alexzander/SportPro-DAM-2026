package devs.grupo5.sportpro.data.model

import java.time.LocalDate
import java.time.LocalDateTime

private val MONTH_NAMES = listOf(
    "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
    "Julio", "Agosto", "Setiembre", "Octubre", "Noviembre", "Diciembre"
)

/** Período actual con el formato usado en la mensualidad: "Noviembre 2026". */
fun currentPaymentPeriod(today: LocalDate = LocalDate.now()): String =
    "${MONTH_NAMES[today.monthValue - 1]} ${today.year}"

/**
 * Estado de la mensualidad. SportPro no procesa pagos: solo registra el estado reportado.
 * El estado lo define la academia/entrenador; el apoderado únicamente registra el comprobante.
 */
enum class PaymentStatus(val label: String) {
    PENDIENTE("Pendiente"),
    PAGADO("Pagado")
}

/**
 * Mensualidad de un menor vinculado. Refleja el esquema final de la colección `payments`
 * de Firestore para que, al conectar el backend, solo cambie la fuente de datos.
 *
 * Identificador recomendado: `"{parentUid}_{playerId}_{period}"`.
 */
data class ParentPayment(
    val id: String = "",
    val ownerUid: String = "",
    val parentUid: String = "",
    val playerId: String = "",
    val period: String = currentPaymentPeriod(),
    val status: PaymentStatus = PaymentStatus.PENDIENTE,
    val receiptRegistered: Boolean = false,
    val receiptUrl: String? = null,
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val updatedBy: String = ""
)
