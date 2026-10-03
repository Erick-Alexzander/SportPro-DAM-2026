package devs.grupo5.sportpro.data.repository

import devs.grupo5.sportpro.data.model.UserProfile
import devs.grupo5.sportpro.data.model.UserRole
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUser: StateFlow<UserProfile?>
    val isEmailRegistered: (String) -> Boolean

    suspend fun login(email: String, password: String): Result<UserProfile>

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        role: UserRole,
        birthDate: String? = null
    ): Result<UserProfile>

    fun logout()
}
