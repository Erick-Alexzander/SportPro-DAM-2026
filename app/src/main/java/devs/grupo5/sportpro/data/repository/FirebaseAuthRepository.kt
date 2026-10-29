package devs.grupo5.sportpro.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import devs.grupo5.sportpro.data.model.UserProfile
import devs.grupo5.sportpro.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.atomic.AtomicBoolean

class FirebaseAuthRepository private constructor() : AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    override val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    // Evita que el AuthStateListener reaccione y cierre sesión prematuramente durante el registro
    private val isRegistering = AtomicBoolean(false)

    init {
        // Observe auth state changes and fetch profile from users/{uid}
        auth.addAuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                if (isRegistering.get()) return@addAuthStateListener
                
                fetchUserProfile(firebaseUser.uid) { profile ->
                    // Verificar que no cerró sesión o cambió de cuenta durante la consulta
                    if (auth.currentUser?.uid == firebaseUser.uid && !isRegistering.get()) {
                        if (profile != null) {
                            _currentUser.value = profile
                        } else {
                            // Perfil inexistente o inválido: invalidar sesión y expulsar
                            auth.signOut()
                            _currentUser.value = null
                        }
                    }
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
                    // Validación Estricta: Verificar existencia explícita del rol en Firestore
                    val roleStr = document.getString("role")
                    val hasValidRole = roleStr != null && runCatching { UserRole.valueOf(roleStr) }.isSuccess
                    
                    if (!hasValidRole) {
                        onResult(null)
                        return@addOnSuccessListener
                    }

                    val profile = document.toObject(UserProfile::class.java)
                    // Validar correspondencia estricta de UID
                    if (profile != null && profile.uid == uid) {
                        onResult(profile)
                    } else {
                        onResult(null)
                    }
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    // Retorna false para evitar Email Enumeration Protection. 
    // La validación real ocurre en el registro interceptando FirebaseAuthUserCollisionException.
    override val isEmailRegistered: (String) -> Boolean = { _ -> false }

    override suspend fun login(email: String, password: String): Result<UserProfile> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                val doc = firestore.collection("users").document(firebaseUser.uid).get().await()
                if (doc.exists()) {
                    // Validación estricta de rol
                    val roleStr = doc.getString("role")
                    val hasValidRole = roleStr != null && runCatching { UserRole.valueOf(roleStr) }.isSuccess
                    
                    if (!hasValidRole) {
                        auth.signOut()
                        _currentUser.value = null
                        return Result.failure(Exception("El perfil de usuario carece de un rol válido."))
                    }

                    val profile = doc.toObject(UserProfile::class.java)
                    if (profile != null && profile.uid == firebaseUser.uid) {
                        _currentUser.value = profile
                        Result.success(profile)
                    } else {
                        auth.signOut()
                        _currentUser.value = null
                        Result.failure(Exception("El perfil de usuario es inválido o está corrupto."))
                    }
                } else {
                    auth.signOut()
                    _currentUser.value = null
                    Result.failure(Exception("No se encontró el perfil de usuario en la base de datos."))
                }
            } else {
                Result.failure(Exception("No se pudo autenticar al usuario."))
            }
        } catch (e: Exception) {
            // Si la lectura en Firestore falla por red, limpiamos si hay sesión residual
            if (auth.currentUser != null) {
                auth.signOut()
                _currentUser.value = null
            }
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
        if (role == UserRole.ADMIN) {
            return Result.failure(Exception("No tienes permisos para registrar una cuenta de Administrador."))
        }

        val normalizedEmail = email.lowercase().trim()
        var firebaseUser: FirebaseUser? = null
        
        isRegistering.set(true)
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
                _currentUser.value = userProfile
                return Result.success(userProfile)
            } else {
                return Result.failure(Exception("Error al crear la cuenta de usuario."))
            }
        } catch (e: Exception) {
            var rollbackWarning = ""
            // Rollback: if user was created in Auth but Firestore write failed, delete the auth user
            if (firebaseUser != null) {
                try {
                    firebaseUser.delete().await()
                } catch (_: Exception) {
                    rollbackWarning = " (Aviso: La cuenta de correo fue creada pero quedó huérfana y requerirá soporte técnico para eliminarse)."
                }
            }
            
            val errorMsg = when (e) {
                is FirebaseAuthUserCollisionException -> "Este correo ya está registrado"
                is FirebaseAuthWeakPasswordException -> "La contraseña es demasiado débil"
                is FirebaseAuthInvalidCredentialsException -> "El formato del correo es inválido"
                else -> e.message ?: "Error al registrar la cuenta"
            }
            return Result.failure(Exception(errorMsg + rollbackWarning))
        } finally {
            isRegistering.set(false)
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
