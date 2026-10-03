package devs.grupo5.sportpro.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import devs.grupo5.sportpro.data.model.Exercise
import devs.grupo5.sportpro.data.model.Training
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FirebaseTrainingRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : TrainingRepository {

    private val _trainings = MutableStateFlow<List<Training>>(emptyList())
    override val trainings: StateFlow<List<Training>> = _trainings.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

    init {
        setupTrainingsListener()
        auth.addAuthStateListener {
            setupTrainingsListener()
        }
    }

    private fun setupTrainingsListener() {
        listenerRegistration?.remove()
        val currentUserId = auth.currentUser?.uid
        if (currentUserId.isNullOrEmpty()) {
            _trainings.value = emptyList()
            return
        }

        listenerRegistration = firestore.collection("trainings")
            .whereEqualTo("trainerId", currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    return@addSnapshotListener
                }
                val trainingList = snapshot.documents.map { doc -> doc.toTraining() }
                _trainings.value = trainingList
            }
    }

    override fun getTrainingById(id: String): Training? {
        return _trainings.value.find { it.id == id }
    }

    override fun addTraining(training: Training) {
        val currentUserId = auth.currentUser?.uid ?: ""
        val trainingRef = if (training.id.isNotBlank()) {
            firestore.collection("trainings").document(training.id)
        } else {
            firestore.collection("trainings").document()
        }

        val trainingId = trainingRef.id
        val newTraining = training.copy(id = trainingId, trainerId = currentUserId)

        val exercisesData = newTraining.exercises.map { exercise ->
            mapOf(
                "id" to exercise.id,
                "name" to exercise.name,
                "durationMinutes" to exercise.durationMinutes,
                "type" to exercise.type
            )
        }

        val trainingData = mapOf(
            "id" to newTraining.id,
            "teamId" to newTraining.teamId,
            "title" to newTraining.title,
            "date" to newTraining.date,
            "startTime" to newTraining.startTime,
            "durationMinutes" to newTraining.durationMinutes,
            "category" to newTraining.category,
            "objective" to newTraining.objective,
            "exercises" to exercisesData,
            "attendees" to newTraining.attendees,
            "status" to newTraining.status,
            "trainerId" to newTraining.trainerId
        )

        trainingRef.set(trainingData)
    }

    override fun updateAttendance(trainingId: String, attendees: List<String>) {
        val trainingRef = firestore.collection("trainings").document(trainingId)
        trainingRef.update("attendees", attendees)
    }

    companion object {
        val instance: FirebaseTrainingRepository by lazy { FirebaseTrainingRepository() }
    }
}

fun DocumentSnapshot.toTraining(): Training {
    @Suppress("UNCHECKED_CAST")
    val rawExercises = get("exercises") as? List<Map<String, Any>> ?: emptyList()
    val exercises = rawExercises.map { map ->
        Exercise(
            id = map["id"]?.toString() ?: "",
            name = map["name"]?.toString() ?: "",
            durationMinutes = (map["durationMinutes"] as? Long)?.toInt() ?: 15,
            type = map["type"]?.toString() ?: "Táctico"
        )
    }
    @Suppress("UNCHECKED_CAST")
    val attendees = (get("attendees") as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()

    return Training(
        id = id,
        teamId = getString("teamId") ?: "",
        title = getString("title") ?: "",
        date = getString("date") ?: "",
        startTime = getString("startTime") ?: "",
        durationMinutes = getLong("durationMinutes")?.toInt() ?: 90,
        category = getString("category") ?: "",
        objective = getString("objective") ?: "",
        exercises = exercises,
        attendees = attendees,
        status = getString("status") ?: "Programado",
        trainerId = getString("trainerId") ?: ""
    )
}
