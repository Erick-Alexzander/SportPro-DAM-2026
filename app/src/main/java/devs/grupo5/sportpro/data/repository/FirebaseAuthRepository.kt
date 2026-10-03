package devs.grupo5.sportpro.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import devs.grupo5.sportpro.data.model.UserProfile
import devs.grupo5.sportpro.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthRepository {

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    override val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    private val _isSessionChecked = MutableStateFlow(false)
    override val isSessionChecked: StateFlow<Boolean> = _isSessionChecked.asStateFlow()

    private val registeredEmailsCache = mutableSetOf<String>()

    override val isEmailRegistered: (String) -> Boolean = { email ->
        registeredEmailsCache.contains(email.lowercase().trim())
    }

    init {
        checkCurrentSession()
    }

    private fun checkCurrentSession() {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            val uid = firebaseUser.uid
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        val user = doc.toUserProfile()
                        _currentUser.value = user
                        registeredEmailsCache.add(user.email.lowercase())
                    } else {
                        // Fallback profile if Firestore doc doesn't exist yet
                        val user = UserProfile(
                            uid = uid,
                            email = firebaseUser.email ?: "",
                            role = UserRole.TRAINER
                        )
                        _currentUser.value = user
                    }
                    _isSessionChecked.value = true
                }
                .addOnFailureListener {
                    _currentUser.value = null
                    _isSessionChecked.value = true
                }
        } else {
            _currentUser.value = null
            _isSessionChecked.value = true
        }
    }

    override suspend fun login(email: String, password: String): Result<UserProfile> {
        val normalizedEmail = email.trim()
        if (normalizedEmail.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Ingresa tu correo y contraseña"))
        }

        return try {
            val authResult = auth.signInWithEmailAndPassword(normalizedEmail, password).await()
            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Error al autenticar con Firebase"))

            val uid = firebaseUser.uid
            val doc = firestore.collection("users").document(uid).get().await()

            val userProfile = if (doc.exists()) {
                doc.toUserProfile()
            } else {
                UserProfile(
                    uid = uid,
                    email = firebaseUser.email ?: normalizedEmail,
                    role = UserRole.TRAINER
                )
            }

            _currentUser.value = userProfile
            registeredEmailsCache.add(userProfile.email.lowercase())
            Result.success(userProfile)
        } catch (e: Exception) {
            val message = parseAuthErrorMessage(e)
            Result.failure(Exception(message))
        }
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

        return try {
            val authResult = auth.createUserWithEmailAndPassword(normalizedEmail, password).await()
            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Error al crear cuenta en Firebase"))

            val uid = firebaseUser.uid
            val userProfile = UserProfile(
                uid = uid,
                firstName = firstName.trim(),
                lastName = lastName.trim(),
                email = normalizedEmail,
                role = role,
                birthDate = birthDate
            )

            val userData = mapOf(
                "uid" to uid,
                "firstName" to userProfile.firstName,
                "lastName" to userProfile.lastName,
                "email" to userProfile.email,
                "role" to userProfile.role.name,
                "birthDate" to (userProfile.birthDate ?: "")
            )

            firestore.collection("users").document(uid).set(userData).await()

            _currentUser.value = userProfile
            registeredEmailsCache.add(normalizedEmail)
            Result.success(userProfile)
        } catch (e: Exception) {
            val message = parseAuthErrorMessage(e)
            Result.failure(Exception(message))
        }
    }

    override fun logout() {
        auth.signOut()
        _currentUser.value = null
    }

    private fun parseAuthErrorMessage(e: Exception): String {
        return when (e) {
            is FirebaseAuthUserCollisionException -> "Este correo electrónico ya se encuentra registrado."
            is FirebaseAuthWeakPasswordException -> "La contraseña es muy débil. Usa al menos 6 caracteres."
            is FirebaseAuthInvalidUserException -> "No existe una cuenta asociada a este correo electrónico."
            is FirebaseAuthInvalidCredentialsException -> "Correo electrónico o contraseña incorrectos."
            is FirebaseAuthException -> "Error de autenticación: ${e.message}"
            else -> e.message ?: "Ocurrió un error inesperado al conectar con el servidor."
        }
    }

    companion object {
        val instance: FirebaseAuthRepository by lazy { FirebaseAuthRepository() }
    }
}

fun DocumentSnapshot.toUserProfile(): UserProfile {
    val roleStr = getString("role") ?: UserRole.TRAINER.name
    val role = try {
        UserRole.valueOf(roleStr)
    } catch (e: Exception) {
        UserRole.TRAINER
    }
    return UserProfile(
        uid = getString("uid") ?: id,
        firstName = getString("firstName") ?: "",
        lastName = getString("lastName") ?: "",
        email = getString("email") ?: "",
        role = role,
        birthDate = getString("birthDate")
    )
}
