package devs.grupo5.sportpro.data.model

/**
 * Representa una solicitud (reclamo) creada por un Jugador (PLAYER)
 * para consumir una invitación y vincularse a una ficha deportiva.
 */
data class LinkClaim(
    val userUid: String = "",
    val inviteId: String = "",
    val createdAt: Long = 0L,
    val status: String = "PENDING" // Valores posibles: PENDING, APPROVED, REJECTED
)
