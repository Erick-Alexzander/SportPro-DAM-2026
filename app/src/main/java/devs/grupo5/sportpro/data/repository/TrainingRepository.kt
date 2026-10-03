package devs.grupo5.sportpro.data.repository

import devs.grupo5.sportpro.data.model.Exercise
import devs.grupo5.sportpro.data.model.Training
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface TrainingRepository {
    val trainings: StateFlow<List<Training>>
    fun getTrainingById(id: String): Training?
    fun addTraining(training: Training)
    fun updateAttendance(trainingId: String, attendees: List<String>)
}

class MockTrainingRepository private constructor() : TrainingRepository {

    private val initialTrainings = listOf(
        Training(
            id = "tr1",
            teamId = "t1",
            title = "Posesión y Presión Alta",
            date = "Hoy, 18:30",
            startTime = "18:30",
            durationMinutes = 90,
            category = "Primera",
            objective = "Mejorar la velocidad de circulación de balón en campo rival y reacción tras pérdida.",
            exercises = listOf(
                Exercise("e1", "Rondo de calentamiento 5v2", 15, "Técnico"),
                Exercise("e2", "Juego de posición 7v7 + 3 comodines", 35, "Táctico"),
                Exercise("e3", "Partido reducido con repliegue", 30, "Físico")
            ),
            attendees = listOf("p1", "p2"),
            status = "Programado"
        ),
        Training(
            id = "tr2",
            teamId = "t2",
            title = "Transición Ofensiva Rápida",
            date = "Mañana, 16:00",
            startTime = "16:00",
            durationMinutes = 75,
            category = "Sub-15",
            objective = "Automatizar movimientos de extremos y mediocampistas en salida de contraataque.",
            exercises = listOf(
                Exercise("e4", "Movilidad articular y coordinativos", 15, "Físico"),
                Exercise("e5", "Oleadas de ataque 3v2", 30, "Táctico"),
                Exercise("e6", "Definición tras centro lateral", 20, "Técnico")
            ),
            attendees = listOf("p4", "p5", "p6"),
            status = "Programado"
        ),
        Training(
            id = "tr3",
            teamId = "t3",
            title = "Fundamentos de Pase y Control",
            date = "Ayer, 15:00",
            startTime = "15:00",
            durationMinutes = 60,
            category = "Sub-10",
            objective = "Perfeccionar el control orientativo y precisión con ambos perfiles.",
            exercises = listOf(
                Exercise("e7", "Juegos de coordinación con balón", 20, "Técnico"),
                Exercise("e8", "Circuitos de pases en parejas", 20, "Técnico"),
                Exercise("e9", "Mini fútbol 4v4", 20, "Táctico")
            ),
            attendees = listOf("p7", "p8"),
            status = "Finalizado"
        )
    )

    private val _trainings = MutableStateFlow(initialTrainings)
    override val trainings: StateFlow<List<Training>> = _trainings.asStateFlow()

    override fun getTrainingById(id: String): Training? {
        return _trainings.value.find { it.id == id }
    }

    override fun addTraining(training: Training) {
        val newTraining = if (training.id.isEmpty()) {
            training.copy(id = "tr_${System.currentTimeMillis()}")
        } else {
            training
        }
        _trainings.value = _trainings.value + newTraining
    }

    override fun updateAttendance(trainingId: String, attendees: List<String>) {
        _trainings.value = _trainings.value.map { training ->
            if (training.id == trainingId) {
                training.copy(attendees = attendees)
            } else training
        }
    }

    companion object {
        val instance: MockTrainingRepository by lazy { MockTrainingRepository() }
    }
}
