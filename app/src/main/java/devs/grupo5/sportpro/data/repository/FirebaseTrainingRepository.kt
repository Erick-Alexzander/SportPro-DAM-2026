package devs.grupo5.sportpro.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import devs.grupo5.sportpro.data.model.Training
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class FirebaseTrainingRepository private constructor() : TrainingRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _trainings = MutableStateFlow<List<Training>>(emptyList())
    override val trainings: StateFlow<List<Training>> = _trainings.asStateFlow()

    private val _syncError = MutableStateFlow<String?>(null)
    override val syncError: StateFlow<String?> = _syncError.asStateFlow()

    private var trainingsListener: ListenerRegistration? = null

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                startListening(user.uid)
            } else {
                stopListening()
                _trainings.value = emptyList()
            }
        }
    }

    private fun startListening(uid: String) {
        trainingsListener?.remove()
        trainingsListener = firestore.collection("trainings")
            .whereEqualTo("ownerUid", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _syncError.value = error.message
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    _syncError.value = null
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Training::class.java)?.copy(id = doc.id)
                    }
                    _trainings.value = list
                }
            }
    }

    private fun stopListening() {
        trainingsListener?.remove()
        trainingsListener = null
        _syncError.value = null
    }

    override fun getTrainingById(id: String): Training? {
        return _trainings.value.find { it.id == id }
    }

    override suspend fun addTraining(training: Training): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))

        return try {
            val docRef = firestore.collection("trainings").document()
            
            // Check if the team exists and belongs to the user
            val teamSnapshot = firestore.collection("teams").document(training.teamId).get().await()
            if (!teamSnapshot.exists()) {
                throw Exception("El equipo no existe")
            }
            if (teamSnapshot.getString("ownerUid") != uid) {
                throw Exception("No tienes permisos para usar este equipo")
            }

            val finalTraining = training.copy(
                id = docRef.id,
                ownerUid = uid
            )
            docRef.set(finalTraining).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAttendance(trainingId: String, attendees: List<String>): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))

        return try {
            val docRef = firestore.collection("trainings").document(trainingId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                if (!snapshot.exists()) {
                    throw Exception("El entrenamiento no existe")
                }
                val ownerUid = snapshot.getString("ownerUid")
                if (ownerUid != uid) {
                    throw Exception("No tienes permisos para modificar este entrenamiento")
                }

                transaction.update(docRef, "attendees", attendees)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        val instance: FirebaseTrainingRepository by lazy { FirebaseTrainingRepository() }
    }
}
