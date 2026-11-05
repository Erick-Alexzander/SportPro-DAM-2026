package devs.grupo5.sportpro.presentation.trainer.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import devs.grupo5.sportpro.data.model.Match
import devs.grupo5.sportpro.data.model.Training
import devs.grupo5.sportpro.data.repository.MatchRepository
import devs.grupo5.sportpro.data.repository.MockMatchRepository
import devs.grupo5.sportpro.data.repository.FirebasePlayerRepository
import devs.grupo5.sportpro.data.repository.FirebaseTeamRepository
import devs.grupo5.sportpro.data.repository.FirebaseTrainingRepository
import devs.grupo5.sportpro.data.repository.PlayerRepository
import devs.grupo5.sportpro.data.repository.TeamRepository
import devs.grupo5.sportpro.data.repository.TrainingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TrainerDashboardViewModel(
    playerRepository: PlayerRepository = FirebasePlayerRepository.instance,
    trainingRepository: TrainingRepository = FirebaseTrainingRepository.instance,
    teamRepository: TeamRepository = FirebaseTeamRepository.instance,
    matchRepository: MatchRepository = MockMatchRepository.instance
) : ViewModel() {

    val totalPlayersCount: StateFlow<Int> = playerRepository.players.map { it.size }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )
    val consecutiveWins: Int = 4
    val attendanceRate: String = "92%"

    private fun parseDateTime(dateStr: String, timeStr: String): LocalDateTime? {
        val time = try {
            LocalTime.parse(timeStr.trim().padStart(5, '0'))
        } catch (_: DateTimeParseException) {
            null
        }

        val date = dateStr.trim().lowercase()
        return when {
            date.startsWith("hoy") -> time?.let { LocalDateTime.of(LocalDate.now(), it) }
            date.startsWith("mañana") -> time?.let { LocalDateTime.of(LocalDate.now().plusDays(1), it) }
            date.startsWith("ayer") -> time?.let { LocalDateTime.of(LocalDate.now().minusDays(1), it) }
            else -> {
                try {
                    // Try parsing "d/M/yyyy" which handles both "5/11/2026" and "05/11/2026"
                    val formatter = DateTimeFormatter.ofPattern("d/M/yyyy")
                    time?.let { LocalDateTime.of(LocalDate.parse(dateStr.trim(), formatter), it) }
                } catch (_: Exception) {
                    null
                }
            }
        }
    }

    val liveMatch: Match? = matchRepository.matches.value.find { it.status == "En vivo" }
    
    val nextTraining: StateFlow<Pair<Training, String>?> = combine(
        trainingRepository.trainings,
        teamRepository.teams
    ) { trainings, teams ->
        val next = trainings.filter { it.status == "Programado" }
            .filter { training ->
                val startTime = parseDateTime(training.date, training.startTime)
                if (startTime != null) {
                    // Protegemos contra duraciones negativas o gigantes
                    val safeDuration = training.durationMinutes.coerceIn(0, 1440).toLong()
                    val endTime = startTime.plusMinutes(safeDuration)
                    endTime.isAfter(LocalDateTime.now()) 
                } else {
                    false // Descartamos si la fecha/hora es inválida
                }
            }
            .sortedBy { parseDateTime(it.date, it.startTime) ?: LocalDateTime.MAX }
            .firstOrNull()
            
        if (next != null) {
            val teamName = teams.find { it.id == next.teamId }?.name ?: next.category
            Pair(next, teamName)
        } else {
            null
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
    
    val nextMatch: Match? = matchRepository.matches.value.find { it.status == "Programado" }
}
