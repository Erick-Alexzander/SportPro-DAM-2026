package devs.grupo5.sportpro.presentation.trainer.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import devs.grupo5.sportpro.data.model.Match
import devs.grupo5.sportpro.data.model.Training
import devs.grupo5.sportpro.data.model.UserProfile
import devs.grupo5.sportpro.data.repository.AuthRepository
import devs.grupo5.sportpro.data.repository.FirebaseAuthRepository
import devs.grupo5.sportpro.data.repository.FirebaseMatchRepository
import devs.grupo5.sportpro.data.repository.FirebasePlayerRepository
import devs.grupo5.sportpro.data.repository.FirebaseTeamRepository
import devs.grupo5.sportpro.data.repository.FirebaseTrainingRepository
import devs.grupo5.sportpro.data.repository.MatchRepository
import devs.grupo5.sportpro.data.repository.PlayerRepository
import devs.grupo5.sportpro.data.repository.TeamRepository
import devs.grupo5.sportpro.data.repository.TrainingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TrainerDashboardViewModel(
    authRepository: AuthRepository = FirebaseAuthRepository.instance,
    playerRepository: PlayerRepository = FirebasePlayerRepository.instance,
    trainingRepository: TrainingRepository = FirebaseTrainingRepository.instance,
    matchRepository: MatchRepository = FirebaseMatchRepository.instance,
    teamRepository: TeamRepository = FirebaseTeamRepository.instance
) : ViewModel() {

    val currentUser: StateFlow<UserProfile?> = authRepository.currentUser

    val totalPlayersCount: StateFlow<Int> = playerRepository.players
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalTeamsCount: StateFlow<Int> = teamRepository.teams
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val consecutiveWins: Int = 4
    val attendanceRate: String = "92%"

    val liveMatch: StateFlow<Match?> = matchRepository.matches
        .map { list -> list.find { it.status.equals("En vivo", ignoreCase = true) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val nextTraining: StateFlow<Training?> = trainingRepository.trainings
        .map { list -> list.find { it.status.equals("Programado", ignoreCase = true) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val nextMatch: StateFlow<Match?> = matchRepository.matches
        .map { list -> list.find { it.status.equals("Programado", ignoreCase = true) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
