package devs.grupo5.sportpro.presentation.trainer.matches

import androidx.lifecycle.ViewModel
import devs.grupo5.sportpro.data.model.Match
import devs.grupo5.sportpro.data.model.Team
import devs.grupo5.sportpro.data.repository.MatchRepository
import devs.grupo5.sportpro.data.repository.MockMatchRepository
import devs.grupo5.sportpro.data.repository.MockTeamRepository
import devs.grupo5.sportpro.data.repository.TeamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MatchViewModel(
    private val matchRepository: MatchRepository = MockMatchRepository.instance,
    private val teamRepository: TeamRepository = MockTeamRepository.instance
) : ViewModel() {

    val matches: StateFlow<List<Match>> = matchRepository.matches
    val teams: StateFlow<List<Team>> = teamRepository.teams

    private val _selectedStatus = MutableStateFlow("Todos")
    val selectedStatus: StateFlow<String> = _selectedStatus.asStateFlow()

    fun setSelectedStatus(status: String) {
        _selectedStatus.value = status
    }

    fun addMatch(
        teamId: String,
        rival: String,
        date: String,
        time: String,
        stadium: String,
        category: String,
        isHome: Boolean
    ) {
        val newMatch = Match(
            teamId = teamId,
            rival = rival,
            date = date,
            time = time,
            stadium = stadium,
            category = category,
            status = "Programado",
            isHome = isHome
        )
        matchRepository.addMatch(newMatch)
    }
}
