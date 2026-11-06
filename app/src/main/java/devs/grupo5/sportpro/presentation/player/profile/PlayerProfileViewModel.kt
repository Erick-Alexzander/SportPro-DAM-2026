package devs.grupo5.sportpro.presentation.player.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import devs.grupo5.sportpro.data.model.PlayerProfile
import devs.grupo5.sportpro.data.model.UserProfile
import devs.grupo5.sportpro.data.repository.AuthRepository
import devs.grupo5.sportpro.data.repository.FirebaseAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerProfileViewModel(
    private val repository: AuthRepository = FirebaseAuthRepository.instance
) : ViewModel() {

    val currentUser: StateFlow<UserProfile?> = repository.currentUser

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    fun saveProfile(
        firstName: String,
        lastName: String,
        birthDate: String?,
        playerProfile: PlayerProfile,
        onSuccess: () -> Unit = {}
    ) {
        val current = repository.currentUser.value
        if (current == null) {
            _errorMessage.value = "No hay una sesión activa"
            return
        }
        if (firstName.isBlank() || lastName.isBlank()) {
            _errorMessage.value = "El nombre y los apellidos son obligatorios"
            return
        }

        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null
            _saveSuccess.value = false

            val updated = current.copy(
                firstName = firstName.trim(),
                lastName = lastName.trim(),
                birthDate = birthDate?.trim()?.takeIf { it.isNotBlank() },
                playerProfile = playerProfile
            )

            repository.updateProfile(updated).fold(
                onSuccess = {
                    _saveSuccess.value = true
                    onSuccess()
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "No se pudo guardar el perfil"
                }
            )
            _isSaving.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun consumeSaveSuccess() {
        _saveSuccess.value = false
    }
}
