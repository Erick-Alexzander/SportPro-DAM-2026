
package devs.grupo5.sportpro.presentation.trainer.training

import androidx.lifecycle.ViewModel
import devs.grupo5.sportpro.data.model.AttendanceRecord
import devs.grupo5.sportpro.data.model.AttendanceStatus
import devs.grupo5.sportpro.data.model.Exercise
import devs.grupo5.sportpro.data.model.Player
import devs.grupo5.sportpro.data.model.Team
import devs.grupo5.sportpro.data.model.Training
import devs.grupo5.sportpro.data.repository.MockPlayerRepository
import devs.grupo5.sportpro.data.repository.MockTeamRepository
import devs.grupo5.sportpro.data.repository.MockTrainingRepository
import devs.grupo5.sportpro.data.repository.PlayerRepository
import devs.grupo5.sportpro.data.repository.TeamRepository
import devs.grupo5.sportpro.data.repository.TrainingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TrainingViewModel(
    private val trainingRepository: TrainingRepository = MockTrainingRepository.instance,
    private val playerRepository: PlayerRepository = MockPlayerRepository.instance,
    private val teamRepository: TeamRepository = MockTeamRepository.instance
) : ViewModel() {

    val trainings: StateFlow<List<Training>> = trainingRepository.trainings
    val players: StateFlow<List<Player>> = playerRepository.players
    val teams: StateFlow<List<Team>> = teamRepository.teams

    private val _selectedCategory = MutableStateFlow("Todos")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun getTrainingById(id: String): Training? {
        return trainingRepository.getTrainingById(id)
    }

    fun createTraining(
        teamId: String,
        title: String,
        date: String,
        startTime: String,
        durationMinutes: Int,
        category: String,
        objective: String,
        exercises: List<Exercise>
    ) {
        val newTraining = Training(
            teamId = teamId,
            title = title,
            date = date,
            startTime = startTime,
            durationMinutes = durationMinutes,
            category = category,
            objective = objective,
            exercises = exercises,
            status = "Programado"
        )

        trainingRepository.addTraining(newTraining)
    }

    // Función original: conserva compatibilidad
    fun updateAttendance(
        trainingId: String,
        attendeeIds: List<String>
    ) {
        trainingRepository.updateAttendance(trainingId, attendeeIds)
    }

    // HU-009: Guardar los tres estados de asistencia
    fun updateAttendanceRecords(
        trainingId: String,
        records: List<AttendanceRecord>
    ) {
        trainingRepository.updateAttendanceRecords(
            trainingId = trainingId,
            records = records
        )
    }

    // HU-009: Obtener el historial de asistencia de un jugador
    fun getPlayerAttendanceHistory(
        playerId: String
    ): List<Pair<Training, AttendanceStatus>> {
        return trainings.value
            .filter { training ->
                training.attendanceRecords.any { it.playerId == playerId }
            }
            .map { training ->
                val record = training.attendanceRecords.first {
                    it.playerId == playerId
                }
                training to record.status
            }
    }

    // HU-009: Calcular el porcentaje de asistencia registrada
    fun getPlayerAttendancePercentage(playerId: String): Int {
        val history = getPlayerAttendanceHistory(playerId)

        if (history.isEmpty()) return 0

        val presentCount = history.count {
            it.second == AttendanceStatus.PRESENTE
        }

        return (presentCount * 100) / history.size
    }
}
