package devs.grupo5.sportpro.presentation.player.link

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import devs.grupo5.sportpro.data.repository.FirebaseLinkRepository
import devs.grupo5.sportpro.data.repository.LinkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerClaimViewModel(
    private val linkRepository: LinkRepository = FirebaseLinkRepository.instance
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg: StateFlow<String?> = _errorMsg.asStateFlow()

    private val _claimSuccess = MutableStateFlow(false)
    val claimSuccess: StateFlow<Boolean> = _claimSuccess.asStateFlow()

    private val _hasExistingLink = MutableStateFlow(false)
    val hasExistingLink: StateFlow<Boolean> = _hasExistingLink.asStateFlow()

    init {
        checkExistingLink()
    }

    private fun checkExistingLink() {
        viewModelScope.launch {
            val result = linkRepository.getCurrentUserLink()
            if (result.isSuccess && result.getOrNull() != null) {
                _hasExistingLink.value = true
            }
        }
    }

    fun submitInviteCode(code: String) {
        if (_isLoading.value) return

        val sanitizedCode = code.trim()
        if (sanitizedCode.isEmpty()) {
            _errorMsg.value = "Por favor, ingresa el código de invitación."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMsg.value = null
            _claimSuccess.value = false

            val result = linkRepository.submitClaim(sanitizedCode)
            if (result.isSuccess) {
                _claimSuccess.value = true
            } else {
                _errorMsg.value = result.exceptionOrNull()?.message ?: "Ocurrió un error al enviar la solicitud."
            }
            _isLoading.value = false
        }
    }

    fun clearState() {
        _errorMsg.value = null
        _claimSuccess.value = false
    }
}
