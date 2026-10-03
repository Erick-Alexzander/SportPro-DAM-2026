package devs.grupo5.sportpro.presentation.trainer.teams

import androidx.lifecycle.ViewModel
import devs.grupo5.sportpro.data.model.Match
import devs.grupo5.sportpro.data.model.Player
import devs.grupo5.sportpro.data.model.Team
import devs.grupo5.sportpro.data.model.Training
import devs.grupo5.sportpro.data.repository.MatchRepository
import devs.grupo5.sportpro.data.repository.MockMatchRepository
import devs.grupo5.sportpro.data.repository.MockPlayerRepository
import devs.grupo5.sportpro.data.repository.MockTeamRepository
import devs.grupo5.sportpro.data.repository.MockTrainingRepository
import devs.grupo5.sportpro.data.repository.PlayerRepository
import devs.grupo5.sportpro.data.repository.TeamRepository
import devs.grupo5.sportpro.data.repository.TrainingRepository
import kotlinx.coroutines.flow.StateFlow

class TeamViewModel(
    private val teamRepository: TeamRepository = MockTeamRepository.instance,
    private val playerRepository: PlayerRepository = MockPlayerRepository.instance,
    private val trainingRepository: TrainingRepository = MockTrainingRepository.instance,
    private val matchRepository: MatchRepository = MockMatchRepository.instance
) : ViewModel() {

    val teams: StateFlow<List<Team>> = teamRepository.teams
    val availablePlayers: StateFlow<List<Player>> = playerRepository.players

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
        teamRepository.addTeam(newTeam)
    }

    fun addPlayerToTeam(teamId: String, playerId: String) {
        teamRepository.addPlayerToTeam(teamId, playerId)
    }

    fun removePlayerFromTeam(teamId: String, playerId: String) {
        teamRepository.removePlayerFromTeam(teamId, playerId)
    }
}
