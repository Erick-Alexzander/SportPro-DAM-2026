package devs.grupo5.sportpro.presentation.trainer.dashboard

import androidx.lifecycle.ViewModel
import devs.grupo5.sportpro.data.model.Match
import devs.grupo5.sportpro.data.model.Training
import devs.grupo5.sportpro.data.repository.MatchRepository
import devs.grupo5.sportpro.data.repository.MockMatchRepository
import devs.grupo5.sportpro.data.repository.MockPlayerRepository
import devs.grupo5.sportpro.data.repository.MockTrainingRepository
import devs.grupo5.sportpro.data.repository.PlayerRepository
import devs.grupo5.sportpro.data.repository.TrainingRepository
import kotlinx.coroutines.flow.StateFlow

class TrainerDashboardViewModel(
    playerRepository: PlayerRepository = MockPlayerRepository.instance,
    trainingRepository: TrainingRepository = MockTrainingRepository.instance,
    matchRepository: MatchRepository = MockMatchRepository.instance
) : ViewModel() {

    val totalPlayersCount: Int = playerRepository.players.value.size
    val consecutiveWins: Int = 4
    val attendanceRate: String = "92%"

    val liveMatch: Match? = matchRepository.matches.value.find { it.status == "En vivo" }
    val nextTraining: Training? = trainingRepository.trainings.value.find { it.status == "Programado" }
    val nextMatch: Match? = matchRepository.matches.value.find { it.status == "Programado" }
}
