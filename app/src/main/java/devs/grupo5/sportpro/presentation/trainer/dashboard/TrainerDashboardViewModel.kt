package devs.grupo5.sportpro.presentation.trainer.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import devs.grupo5.sportpro.data.model.Match
import devs.grupo5.sportpro.data.model.Training
import devs.grupo5.sportpro.data.repository.MatchRepository
import devs.grupo5.sportpro.data.repository.MockMatchRepository
import devs.grupo5.sportpro.data.repository.FirebasePlayerRepository
import devs.grupo5.sportpro.data.repository.MockTrainingRepository
import devs.grupo5.sportpro.data.repository.PlayerRepository
import devs.grupo5.sportpro.data.repository.TrainingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TrainerDashboardViewModel(
    playerRepository: PlayerRepository = FirebasePlayerRepository.instance,
    trainingRepository: TrainingRepository = MockTrainingRepository.instance,
    matchRepository: MatchRepository = MockMatchRepository.instance
) : ViewModel() {

    val totalPlayersCount: StateFlow<Int> = playerRepository.players.map { it.size }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )
    val consecutiveWins: Int = 4
    val attendanceRate: String = "92%"

    val liveMatch: Match? = matchRepository.matches.value.find { it.status == "En vivo" }
    val nextTraining: Training? = trainingRepository.trainings.value.find { it.status == "Programado" }
    val nextMatch: Match? = matchRepository.matches.value.find { it.status == "Programado" }
}
