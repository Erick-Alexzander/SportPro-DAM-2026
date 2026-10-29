package devs.grupo5.sportpro.presentation.trainer.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import devs.grupo5.sportpro.data.model.Match
import devs.grupo5.sportpro.data.model.Player
import devs.grupo5.sportpro.data.model.Team
import devs.grupo5.sportpro.data.model.Training
import devs.grupo5.sportpro.data.repository.MatchRepository
import devs.grupo5.sportpro.data.repository.MockMatchRepository
import devs.grupo5.sportpro.data.repository.MockPlayerRepository
import devs.grupo5.sportpro.data.repository.FirebaseTeamRepository
import devs.grupo5.sportpro.data.repository.MockTrainingRepository
import devs.grupo5.sportpro.data.repository.PlayerRepository
import devs.grupo5.sportpro.data.repository.TeamRepository
import devs.grupo5.sportpro.data.repository.TrainingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeamViewModel(
    private val teamRepository: TeamRepository = FirebaseTeamRepository.instance,
    private val playerRepository: PlayerRepository = MockPlayerRepository.instance,
    private val trainingRepository: TrainingRepository = MockTrainingRepository.instance,
    private val matchRepository: MatchRepository = MockMatchRepository.instance
) : ViewModel() {

    val teams: StateFlow<List<Team>> = teamRepository.teams
    val availablePlayers: StateFlow<List<Player>> = playerRepository.players

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()

    fun resetState() {
        _errorMessage.value = null
        _isSuccess.value = false
    }

    fun getTeamById(teamId: String): Team? {
        return teamRepository.getTeamById(teamId)
    }

    fun getTeamPlayers(team: Team): List<Player> {
        return playerRepository.getPlayersByIds(team.playerIds)
    }

    fun getTeamTrainings(teamId: String): List<Training> {
        return trainingRepository.trainings.value.filter { it.teamId == teamId }
    }

    fun getTeamMatches(teamId: String): List<Match> {
        return matchRepository.matches.value.filter { it.teamId == teamId }
    }

    fun createTeam(
        name: String,
        category: String,
        description: String,
        selectedPlayerIds: List<String>
    ) {
        val newTeam = Team(
            name = name,
            category = category,
            description = description,
            coachName = "Entrenador DT",
            playerIds = selectedPlayerIds,
            playerCount = selectedPlayerIds.size
        )
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _isSuccess.value = false
            
            val result = teamRepository.addTeam(newTeam)
            
            if (result.isSuccess) {
                _isSuccess.value = true
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Error al crear el equipo"
            }
            _isLoading.value = false
        }
    }

    fun addPlayerToTeam(teamId: String, playerId: String) {
        viewModelScope.launch {
            teamRepository.addPlayerToTeam(teamId, playerId)
        }
    }

    fun removePlayerFromTeam(teamId: String, playerId: String) {
        viewModelScope.launch {
            teamRepository.removePlayerFromTeam(teamId, playerId)
        }
    }
}
