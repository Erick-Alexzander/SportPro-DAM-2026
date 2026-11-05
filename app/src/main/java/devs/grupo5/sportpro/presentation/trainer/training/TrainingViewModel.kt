package devs.grupo5.sportpro.presentation.trainer.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import devs.grupo5.sportpro.data.model.Exercise
import devs.grupo5.sportpro.data.model.Player
import devs.grupo5.sportpro.data.model.Team
import devs.grupo5.sportpro.data.model.Training
import devs.grupo5.sportpro.data.repository.FirebasePlayerRepository
import devs.grupo5.sportpro.data.repository.FirebaseTeamRepository
import devs.grupo5.sportpro.data.repository.FirebaseTrainingRepository
import devs.grupo5.sportpro.data.repository.MockPlayerRepository
import devs.grupo5.sportpro.data.repository.MockTeamRepository
import devs.grupo5.sportpro.data.repository.MockTrainingRepository
import devs.grupo5.sportpro.data.repository.PlayerRepository
import devs.grupo5.sportpro.data.repository.TeamRepository
import devs.grupo5.sportpro.data.repository.TrainingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TrainingViewModel(
    private val trainingRepository: TrainingRepository = FirebaseTrainingRepository.instance,
    private val playerRepository: PlayerRepository = FirebasePlayerRepository.instance,
    private val teamRepository: TeamRepository = FirebaseTeamRepository.instance
) : ViewModel() {

    val trainings: StateFlow<List<Training>> = trainingRepository.trainings
    val players: StateFlow<List<Player>> = playerRepository.players
    val teams: StateFlow<List<Team>> = teamRepository.teams

    private val _selectedCategory = MutableStateFlow("Todos")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

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
        exercises: List<Exercise>,
        onResult: (Boolean) -> Unit = {}
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
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = trainingRepository.addTraining(newTraining)
            if (result.isSuccess) {
                onResult(true)
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Error al crear entrenamiento"
                onResult(false)
            }
            _isLoading.value = false
        }
    }

    fun updateAttendance(trainingId: String, attendeeIds: List<String>, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = trainingRepository.updateAttendance(trainingId, attendeeIds)
            if (result.isSuccess) {
                onResult(true)
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Error al actualizar asistencia"
                onResult(false)
            }
            _isLoading.value = false
        }
    }
}
