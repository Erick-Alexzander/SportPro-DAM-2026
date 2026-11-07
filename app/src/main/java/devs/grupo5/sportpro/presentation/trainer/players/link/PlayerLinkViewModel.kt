package devs.grupo5.sportpro.presentation.trainer.players.link

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import devs.grupo5.sportpro.data.repository.FirebaseLinkRepository
import devs.grupo5.sportpro.data.repository.LinkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PlayerLinkStatus {
    object Loading : PlayerLinkStatus()
    object Unlinked : PlayerLinkStatus()
    object Linked : PlayerLinkStatus()
    data class Error(val message: String) : PlayerLinkStatus()
}

class PlayerLinkViewModel(
    private val linkRepository: LinkRepository = FirebaseLinkRepository.instance
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg: StateFlow<String?> = _errorMsg.asStateFlow()

    private val _generatedInviteCode = MutableStateFlow<String?>(null)
    val generatedInviteCode: StateFlow<String?> = _generatedInviteCode.asStateFlow()

    private val _expiresAt = MutableStateFlow<Long?>(null)
    val expiresAt: StateFlow<Long?> = _expiresAt.asStateFlow()

    private val _playerLinkStatuses = MutableStateFlow<Map<String, PlayerLinkStatus>>(emptyMap())
    val playerLinkStatuses: StateFlow<Map<String, PlayerLinkStatus>> = _playerLinkStatuses.asStateFlow()

    fun checkPlayersLinkStatus(playerIds: List<String>) {
        if (playerIds.isEmpty()) return
        viewModelScope.launch {
            val currentMap = _playerLinkStatuses.value.toMutableMap()
            // Mark as loading if not present
            for (id in playerIds) {
                if (currentMap[id] == null) {
                    currentMap[id] = PlayerLinkStatus.Loading
                }
            }
            _playerLinkStatuses.value = currentMap

            for (id in playerIds) {
                val res = linkRepository.getPlayerAccountLink(id)
                if (res.isSuccess) {
                    val link = res.getOrNull()
                    currentMap[id] = if (link != null) PlayerLinkStatus.Linked else PlayerLinkStatus.Unlinked
                } else {
                    val ex = res.exceptionOrNull()
                    val msg = ex?.message ?: "Error de consulta"
                    currentMap[id] = PlayerLinkStatus.Error(msg)
                }
            }
            _playerLinkStatuses.value = currentMap.toMap()
        }
    }

    fun generateInvitation(playerId: String) {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMsg.value = null
            _generatedInviteCode.value = null
            _expiresAt.value = null

            val result = linkRepository.createInvitation(playerId)
            if (result.isSuccess) {
                val invitation = result.getOrThrow()
                _generatedInviteCode.value = invitation.inviteId
                _expiresAt.value = invitation.expiresAt
            } else {
                _errorMsg.value = result.exceptionOrNull()?.message ?: "Error desconocido al generar invitación."
            }
            _isLoading.value = false
        }
    }

    fun clearState() {
        _generatedInviteCode.value = null
        _errorMsg.value = null
        _expiresAt.value = null
    }
}
