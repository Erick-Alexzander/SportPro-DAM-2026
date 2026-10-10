package devs.grupo5.sportpro.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import devs.grupo5.sportpro.data.model.UserProfile
import devs.grupo5.sportpro.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository private constructor() : AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    override val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    private val registeredEmails = mutableSetOf<String>()

    init {
        // Observe auth state changes and fetch profile from users/{uid}
        auth.addAuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                fetchUserProfile(firebaseUser.uid) { profile ->
                    _currentUser.value = profile ?: UserProfile(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        firstName = firebaseUser.displayName ?: ""
                    )
                }
            } else {
                _currentUser.value = null
            }
        }
    }

    private fun fetchUserProfile(uid: String, onResult: (UserProfile?) -> Unit) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val profile = document.toObject(UserProfile::class.java)
                    if (profile != null && !profile.email.isBlank()) {
                        registeredEmails.add(profile.email.lowercase().trim())
                    }
                    onResult(profile)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    override val isEmailRegistered: (String) -> Boolean = { email ->
        registeredEmails.contains(email.lowercase().trim())
    }

    override suspend fun login(email: String, password: String): Result<UserProfile> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                val doc = firestore.collection("users").document(firebaseUser.uid).get().await()
                val profile = doc.toObject(UserProfile::class.java) ?: UserProfile(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: email,
                    firstName = firebaseUser.displayName ?: ""
                )
                registeredEmails.add(profile.email.lowercase().trim())
                _currentUser.value = profile
                Result.success(profile)
            } else {
                Result.failure(Exception("No se pudo autenticar al usuario"))
            }
        } catch (e: Exception) {
            Result.failure(e)
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
        if (registeredEmails.contains(normalizedEmail)) {
            return Result.failure(Exception("Este correo ya está registrado"))
        }

        var firebaseUser: FirebaseUser? = null
        try {
            val authResult = auth.createUserWithEmailAndPassword(normalizedEmail, password).await()
            firebaseUser = authResult.user
            if (firebaseUser != null) {
                val userProfile = UserProfile(
                    uid = firebaseUser.uid,
                    firstName = firstName.trim(),
                    lastName = lastName.trim(),
                    email = normalizedEmail,
                    role = role,
                    birthDate = birthDate
                )
                // Save profile to Firestore collection "users/{uid}"
                firestore.collection("users").document(firebaseUser.uid).set(userProfile).await()
                registeredEmails.add(normalizedEmail)
                _currentUser.value = userProfile
                return Result.success(userProfile)
            } else {
                return Result.failure(Exception("Error al crear la cuenta de usuario"))
            }
        } catch (e: Exception) {
            // Rollback: if user was created in Auth but Firestore write failed, delete the auth user
            try {
                firebaseUser?.delete()?.await()
            } catch (_: Exception) {
                // If deletion fails, ignore or handle
            }
            val errorMsg = when {
                e.message?.contains("email-already-in-use", ignoreCase = true) == true -> "Este correo ya está registrado"
                else -> e.message ?: "Error al registrar la cuenta"
            }
            return Result.failure(Exception(errorMsg))
        }
    }

    override fun logout() {
        auth.signOut()
        _currentUser.value = null
    }

    companion object {
        val instance: FirebaseAuthRepository by lazy { FirebaseAuthRepository() }
    }
}
