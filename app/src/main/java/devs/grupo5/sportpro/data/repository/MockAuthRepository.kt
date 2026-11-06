package devs.grupo5.sportpro.data.repository

import devs.grupo5.sportpro.data.model.UserProfile
import devs.grupo5.sportpro.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockAuthRepository private constructor() : AuthRepository {

    private val initialUsers = mutableMapOf(
        "entrenador@sportpro.com" to Pair(
            UserProfile(
                uid = "u_trainer_1",
                firstName = "Carlos",
                lastName = "Ramírez",
                email = "entrenador@sportpro.com",
                role = UserRole.TRAINER
            ),
            "12345678"
        ),
        "apoderado@sportpro.com" to Pair(
            UserProfile(
                uid = "u_parent_1",
                firstName = "María",
                lastName = "González",
                email = "apoderado@sportpro.com",
                role = UserRole.PARENT
            ),
            "12345678"
        )
    )

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    override val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    override val isEmailRegistered: (String) -> Boolean = { email ->
        initialUsers.containsKey(email.lowercase().trim())
    }

    override suspend fun login(email: String, password: String): Result<UserProfile> {
        val normalizedEmail = email.lowercase().trim()
        val userPair = initialUsers[normalizedEmail]

        if (userPair != null) {
            val (user, storedPassword) = userPair
            // Accept either 12345678 or 123456 for the test account for convenience
            if (password == storedPassword || (normalizedEmail == "entrenador@sportpro.com" && password == "123456")) {
                _currentUser.value = user
                return Result.success(user)
            }
        }
        return Result.failure(Exception("Credenciales incorrectas"))
    }

    override suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        role: UserRole,
        birthDate: String?
    ): Result<UserProfile> {
        val normalizedEmail = email.lowercase().trim()
        if (initialUsers.containsKey(normalizedEmail)) {
            return Result.failure(Exception("Este correo ya está registrado"))
        }

        val newUser = UserProfile(
            uid = "u_${System.currentTimeMillis()}",
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            email = normalizedEmail,
            role = role,
            birthDate = birthDate
        )

        initialUsers[normalizedEmail] = Pair(newUser, password)
        _currentUser.value = newUser
        return Result.success(newUser)
    }

    override suspend fun updateProfile(profile: UserProfile): Result<UserProfile> {
        val currentUid = _currentUser.value?.uid
        if (currentUid == null || currentUid != profile.uid) {
            return Result.failure(Exception("No hay una sesión activa para actualizar el perfil"))
        }

        val normalizedEmail = profile.email.lowercase().trim()
        var storedPassword = ""
        val entries = initialUsers.entries.toList()
        entries.forEach { (email, pair) ->
            if (pair.first.uid == profile.uid) {
                storedPassword = pair.second
                initialUsers.remove(email)
            }
        }
        initialUsers[normalizedEmail] = Pair(profile, storedPassword)
        _currentUser.value = profile
        return Result.success(profile)
    }

    override suspend fun getUserProfile(uid: String): Result<UserProfile> {
        val profile = initialUsers.values.map { it.first }.find { it.uid == uid }
            ?: return Result.failure(Exception("Perfil no encontrado"))
        return Result.success(profile)
    }

    override fun logout() {
        _currentUser.value = null
    }

    companion object {
        val instance: MockAuthRepository by lazy { MockAuthRepository() }
    }
}
