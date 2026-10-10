package devs.grupo5.sportpro.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import devs.grupo5.sportpro.data.model.UserProfile
import devs.grupo5.sportpro.data.model.UserRole
import devs.grupo5.sportpro.data.repository.AuthRepository
import devs.grupo5.sportpro.data.repository.FirebaseAuthRepository
import devs.grupo5.sportpro.ui.screens.RegisterFormData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository = FirebaseAuthRepository.instance
) : ViewModel() {

    val currentUser: StateFlow<UserProfile?> = authRepository.currentUser

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Temporary registration draft between Step 1 and Step 2
    var registrationDraft: RegisterFormData? = null

    fun isEmailRegistered(email: String): Boolean {
        return authRepository.isEmailRegistered(email)
    }

    fun login(email: String, password: String, onSuccess: (UserRole) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = authRepository.login(email, password)
            _isLoading.value = false

            result.fold(
                onSuccess = { user ->
                    onSuccess(user.role)
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Credenciales incorrectas"
                }
            )
        }
    }

    fun saveStep1Data(formData: RegisterFormData) {
        registrationDraft = formData
    }

    fun registerWithRole(role: UserRole, onSuccess: (UserRole) -> Unit) {
        val draft = registrationDraft
        if (draft == null) {
            _errorMessage.value = "Ocurrió un error con los datos de registro. Inténtalo de nuevo."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val birthDateStr = draft.fechaNacimiento.toString()
            val result = authRepository.register(
                firstName = draft.nombre,
                lastName = draft.apellidos,
                email = draft.correo,
                password = draft.contrasena,
                role = role,
                birthDate = birthDateStr
            )
            _isLoading.value = false

            result.fold(
                onSuccess = { user ->
                    onSuccess(user.role)
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Error al registrar la cuenta"
                }
            )
        }
    }

    fun logout() {
        authRepository.logout()
        registrationDraft = null
        _errorMessage.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
