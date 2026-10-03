package devs.grupo5.sportpro.data.repository

import devs.grupo5.sportpro.data.model.Match
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface MatchRepository {
    val matches: StateFlow<List<Match>>
    fun getMatchById(id: String): Match?
    fun addMatch(match: Match)
    fun updateMatch(match: Match)
}

class MockMatchRepository private constructor() : MatchRepository {

    private val initialMatches = listOf(
        Match(
            id = "m1",
            teamId = "t1",
            rival = "Club Deportivo Norte",
            date = "Hoy",
            time = "20:00",
            stadium = "Estadio Municipal",
            category = "Primera",
            status = "En vivo",
            isHome = true,
            homeScore = 2,
            awayScore = 1
        ),
        Match(
            id = "m2",
            teamId = "t2",
            rival = "Academia Los Andes",
            date = "Sábado, 11:00",
            time = "11:00",
            stadium = "Cancha Central SportPro",
            category = "Sub-15",
            status = "Programado",
            isHome = true
        ),
        Match(
            id = "m3",
            teamId = "t3",
            rival = "Deportivo Juventud",
            date = "Domingo, 10:00",
            time = "10:00",
            stadium = "Complejo Deportivo Sur",
            category = "Sub-10",
            status = "Programado",
            isHome = false
        ),
        Match(
            id = "m4",
            teamId = "t1",
            rival = "Atlético Nacional",
            date = "24 Feb 2025",
            time = "19:00",
            stadium = "Estadio Bicentenario",
            category = "Primera",
            status = "Finalizado",
            isHome = false,
            homeScore = 0,
            awayScore = 3
        )
    )

    private val _matches = MutableStateFlow(initialMatches)
    override val matches: StateFlow<List<Match>> = _matches.asStateFlow()

    override fun getMatchById(id: String): Match? {
        return _matches.value.find { it.id == id }
    }

    override fun addMatch(match: Match) {
        val newMatch = if (match.id.isEmpty()) {
            match.copy(id = "m_${System.currentTimeMillis()}")
        } else {
            match
        }
        _matches.value = _matches.value + newMatch
    }

    override fun updateMatch(match: Match) {
        _matches.value = _matches.value.map {
            if (it.id == match.id) match else it
        }
    }

    companion object {
        val instance: MockMatchRepository by lazy { MockMatchRepository() }
    }
}
