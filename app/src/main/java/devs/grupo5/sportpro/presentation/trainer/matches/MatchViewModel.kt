package devs.grupo5.sportpro.presentation.trainer.matches

import androidx.lifecycle.ViewModel
import devs.grupo5.sportpro.data.model.Match
import devs.grupo5.sportpro.data.repository.MatchRepository
import devs.grupo5.sportpro.data.repository.MockMatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MatchViewModel(
    private val matchRepository: MatchRepository = MockMatchRepository.instance
) : ViewModel() {

    val matches: StateFlow<List<Match>> = matchRepository.matches

    private val _selectedStatus = MutableStateFlow("Todos")
    val selectedStatus: StateFlow<String> = _selectedStatus.asStateFlow()

    fun setSelectedStatus(status: String) {
        _selectedStatus.value = status
    }

    fun addMatch(
        rival: String,
        date: String,
        time: String,
        stadium: String,
        category: String,
        isHome: Boolean
    ) {
        val newMatch = Match(
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
