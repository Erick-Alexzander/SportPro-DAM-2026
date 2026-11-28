package devs.grupo5.sportpro.data.model

import java.time.LocalDateTime

/**
 * Forma en que el apoderado es tratado dentro de la aplicación. Define el saludo y la etiqueta
 * que se muestran en el encabezado del módulo.
 */
enum class GuardianTitle(val label: String, val greeting: String) {
    APODERADO("Padre / Apoderado", "Bienvenido(a),")
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
 * Aviso recibido por el apoderado desde la academia.
 */
data class ParentNotification(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val isRead: Boolean = false
) {
    fun relativeTimeLabel(now: LocalDateTime = LocalDateTime.now()): String =
        createdAt.relativeLabel(now)
}
