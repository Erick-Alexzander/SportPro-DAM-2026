package devs.grupo5.sportpro.data.model

/**
 * Representa una invitación segura generada por un entrenador para vincular
 * una ficha deportiva (player) con la cuenta real de un usuario (user).
 */
data class LinkInvitation(
    val inviteId: String = "",
    val playerId: String = "",
    val trainerUid: String = "",
    val createdAt: Long = 0L,
    val expiresAt: Long = 0L,
    val status: String = "PENDING" // Valores posibles: PENDING, CONSUMED, REVOKED
)
