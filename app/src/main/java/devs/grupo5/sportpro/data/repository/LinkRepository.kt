package devs.grupo5.sportpro.data.repository

import devs.grupo5.sportpro.data.model.LinkClaim
import devs.grupo5.sportpro.data.model.LinkInvitation
import devs.grupo5.sportpro.data.model.PlayerAccountLink

/**
 * Repositorio de Nube responsable del flujo atómico y encriptado de las vinculaciones
 * entre cuentas registradas (Jugador, Apoderado) y Fichas Oficiales (Players).
 */
interface LinkRepository {

    /**
     * Genera una nueva invitación segura de enlace.
     * @param playerId ID de la ficha oficial a enlazar.
     * @return El documento final de la invitación conteniendo el código de acceso.
     */
    suspend fun createInvitation(playerId: String): Result<LinkInvitation>

    /**
     * Realiza un reclamo/solicitud para vincular la cuenta en uso a un código dado.
     * @param inviteCode El código PENDING proveído por el Entrenador.
     */
    suspend fun submitClaim(inviteCode: String): Result<LinkClaim>

    /**
     * Consulta al servidor por los reclamos (claims) pendientes para una invitación generada
     * previamente por el entrenador autenticado.
     * @param inviteId ID del documento original de invitación.
     */
    suspend fun getInvitationClaims(inviteId: String): Result<List<LinkClaim>>

    /**
     * Ejecuta el empaquetado (batch transaccional) confirmando un enlace definitivo, validado atómicamente por Cloud Firestore.
     * @param inviteId ID del documento de invitación.
     * @param userUid UID de la cuenta oficial que se vinculará a la ficha.
     */
    suspend fun approveClaim(inviteId: String, userUid: String): Result<Unit>

    /**
     * Resuelve cuál es el Link oficial activo amarrado al currentUser.
     */
    suspend fun getCurrentUserLink(): Result<PlayerAccountLink?>

    /**
     * Consulta todas las invitaciones creadas por el entrenador autenticado.
     */
    suspend fun getTrainerInvitations(): Result<List<LinkInvitation>>

    /**
     * Consulta si un jugador específico ya posee una cuenta vinculada.
     */
    suspend fun getPlayerAccountLink(playerId: String): Result<PlayerAccountLink?>
}
