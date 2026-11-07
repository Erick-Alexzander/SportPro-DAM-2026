package devs.grupo5.sportpro.presentation.trainer.players.link

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import devs.grupo5.sportpro.data.model.LinkClaim
import devs.grupo5.sportpro.data.model.Player
import devs.grupo5.sportpro.data.repository.FirebaseLinkRepository
import devs.grupo5.sportpro.data.repository.LinkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PendingClaimItem(
    val inviteId: String,
    val playerId: String,
    val playerName: String,
    val claim: LinkClaim
)

class PendingClaimsViewModel(
    private val linkRepository: LinkRepository = FirebaseLinkRepository.instance
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg: StateFlow<String?> = _errorMsg.asStateFlow()

    private val _successMsg = MutableStateFlow<String?>(null)
    val successMsg: StateFlow<String?> = _successMsg.asStateFlow()

    private val _pendingClaims = MutableStateFlow<List<PendingClaimItem>>(emptyList())
    val pendingClaims: StateFlow<List<PendingClaimItem>> = _pendingClaims.asStateFlow()

    fun loadPendingClaims(teamPlayers: List<Player>) {
        if (teamPlayers.isEmpty()) {
            _pendingClaims.value = emptyList()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMsg.value = null

            val invitationsResult = linkRepository.getTrainerInvitations()
            if (invitationsResult.isFailure) {
                _errorMsg.value = invitationsResult.exceptionOrNull()?.message ?: "Error al cargar invitaciones"
                _isLoading.value = false
                return@launch
            }

            val invitations = invitationsResult.getOrNull() ?: emptyList()
            val playerMap = teamPlayers.associateBy { it.id }
            val relevantInvitations = invitations.filter { inv -> inv.playerId in playerMap && inv.status == "PENDING" }

            val list = mutableListOf<PendingClaimItem>()
            for (inv in relevantInvitations) {
                val claimsResult = linkRepository.getInvitationClaims(inv.inviteId)
                if (claimsResult.isSuccess) {
                    val claims = claimsResult.getOrNull() ?: emptyList()
                    val pending = claims.filter { it.status == "PENDING" }
                    val pName = playerMap[inv.playerId]?.fullName ?: "Jugador"
                    for (c in pending) {
                        list.add(PendingClaimItem(inv.inviteId, inv.playerId, pName, c))
                    }
                }
            }

            _pendingClaims.value = list
            _isLoading.value = false
        }
    }

    fun approveClaim(inviteId: String, userUid: String, teamPlayers: List<Player>) {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMsg.value = null
            _successMsg.value = null

            val result = linkRepository.approveClaim(inviteId, userUid)
            if (result.isSuccess) {
                _successMsg.value = "Cuenta vinculada correctamente"
                loadPendingClaims(teamPlayers)
            } else {
                _errorMsg.value = result.exceptionOrNull()?.message ?: "Error al aprobar la vinculación"
            }
            _isLoading.value = false
        }
    }

    fun clearMessages() {
        _errorMsg.value = null
        _successMsg.value = null
    }
}
