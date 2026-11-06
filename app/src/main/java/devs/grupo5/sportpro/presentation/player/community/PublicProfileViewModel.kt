package devs.grupo5.sportpro.presentation.player.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import devs.grupo5.sportpro.data.model.UserProfile
import devs.grupo5.sportpro.data.repository.AuthRepository
import devs.grupo5.sportpro.data.repository.FirebaseAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PublicProfileViewModel(
    private val repository: AuthRepository = FirebaseAuthRepository.instance
) : ViewModel() {

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var loadedUid: String? = null

    fun load(uid: String) {
        if (uid.isBlank()) {
            _errorMessage.value = "Perfil no disponible"
            _isLoading.value = false
            return
        }
        if (loadedUid == uid && _profile.value != null) return

        loadedUid = uid
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            repository.getUserProfile(uid).fold(
                onSuccess = { _profile.value = it },
                onFailure = {
                    _profile.value = null
                    _errorMessage.value = it.message ?: "Perfil no disponible"
                }
            )
            _isLoading.value = false
        }
    }
}
