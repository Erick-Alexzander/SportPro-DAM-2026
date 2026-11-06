package devs.grupo5.sportpro.presentation.trainer.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import devs.grupo5.sportpro.data.model.AiSummary
import devs.grupo5.sportpro.data.model.Match
import devs.grupo5.sportpro.data.model.MatchEvent
import devs.grupo5.sportpro.data.model.Post
import devs.grupo5.sportpro.data.model.Team
import devs.grupo5.sportpro.data.repository.CommunityRepository
import devs.grupo5.sportpro.data.repository.MatchRepository
import devs.grupo5.sportpro.data.repository.FirebaseMatchRepository
import devs.grupo5.sportpro.data.repository.FirebasePlayerRepository
import devs.grupo5.sportpro.data.repository.FirebaseTeamRepository
import devs.grupo5.sportpro.data.repository.MockCommunityRepository
import devs.grupo5.sportpro.data.repository.MockMatchRepository
import devs.grupo5.sportpro.data.repository.MockTeamRepository
import devs.grupo5.sportpro.data.repository.MockPlayerRepository
import devs.grupo5.sportpro.data.repository.PlayerRepository
import devs.grupo5.sportpro.data.repository.TeamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MatchViewModel(
    private val matchRepository: MatchRepository = FirebaseMatchRepository.instance,
    private val teamRepository: TeamRepository = FirebaseTeamRepository.instance,
    private val communityRepository: CommunityRepository = MockCommunityRepository.instance,
    private val playerRepository: PlayerRepository = FirebasePlayerRepository.instance
) : ViewModel() {

    val matches: StateFlow<List<Match>> = matchRepository.matches
    val teams: StateFlow<List<Team>> = teamRepository.teams

    private val _selectedStatus = MutableStateFlow("Todos")
    val selectedStatus: StateFlow<String> = _selectedStatus.asStateFlow()

    // Temporary event registration state
    private val _selectedEventForRegistration = MutableStateFlow<String?>(null)
    val selectedEventForRegistration: StateFlow<String?> = _selectedEventForRegistration.asStateFlow()

    // Event notification feedback
    private val _eventConfirmationToast = MutableStateFlow<String?>(null)
    val eventConfirmationToast: StateFlow<String?> = _eventConfirmationToast.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun setSelectedStatus(status: String) {
        _selectedStatus.value = status
    }

    fun getMatchById(matchId: String): Match? {
        return matchRepository.getMatchById(matchId)
    }

    fun getPlayersForTeam(teamId: String): List<devs.grupo5.sportpro.data.model.Player> {
        val team = teamRepository.getTeamById(teamId) ?: return emptyList()
        return playerRepository.getPlayersByIds(team.playerIds)
    }

    fun addMatch(
        teamId: String,
        rival: String,
        date: String,
        time: String,
        stadium: String,
        category: String,
        isHome: Boolean,
        onResult: (Boolean) -> Unit = {}
    ) {
        val newMatch = Match(
            teamId = teamId,
            rival = rival,
            date = date,
            time = time,
            stadium = stadium,
            category = category,
            status = "Programado",
            isHome = isHome,
            homeScore = 0,
            awayScore = 0,
            currentMinute = 0
        )

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = matchRepository.addMatch(newMatch)
            if (result.isSuccess) {
                onResult(true)
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Error al registrar partido"
                onResult(false)
            }
            _isLoading.value = false
        }
    }

    fun addMatchEvent(
        matchId: String,
        minute: Int,
        eventType: String,
        icon: String,
        player: String,
        team: String,
        status: String = "Local", // "Local" o "Visitante"
        detail: String = ""
    ) {
        val event = MatchEvent(
            id = "e_${java.util.UUID.randomUUID()}",
            matchId = matchId,
            minute = minute,
            eventType = eventType,
            icon = icon,
            player = player,
            team = team, // Esto DEBE SER "Local" o "Visitante" (o mapeable a uno u otro consistentemente para no romper las reglas de Firebase).
            status = status,
            detail = detail
        )
        viewModelScope.launch {
            _isLoading.value = true
            val result = matchRepository.addMatchEvent(matchId, event)
            if (result.isSuccess) {
                _eventConfirmationToast.value = "Evento '$eventType' registrado exitosamente"
            }
            _isLoading.value = false
        }
    }

    fun clearConfirmationToast() {
        _eventConfirmationToast.value = null
    }

    fun toggleMatchPause(matchId: String) {
        viewModelScope.launch {
            matchRepository.toggleMatchPause(matchId)
        }
    }

    fun updateMatchMinute(matchId: String, minute: Int) {
        viewModelScope.launch {
            matchRepository.updateMatchMinute(matchId, minute)
        }
    }

    fun startMatch(matchId: String, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = matchRepository.startMatch(matchId)
            if (result.isSuccess) {
                onResult(true)
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Error al iniciar partido"
                onResult(false)
            }
            _isLoading.value = false
        }
    }

    fun finishMatch(matchId: String, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = matchRepository.finishMatch(matchId)
            if (result.isSuccess) {
                onResult(true)
            } else {
                onResult(false)
            }
            _isLoading.value = false
        }
    }

    fun generateAiSummary(matchId: String, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = matchRepository.generateAiSummary(matchId)
            if (result.isSuccess) {
                onResult(true)
            } else {
                onResult(false)
            }
            _isLoading.value = false
        }
    }

    fun updateAiSummary(matchId: String, aiSummary: AiSummary) {
        viewModelScope.launch {
            matchRepository.updateAiSummary(matchId, aiSummary)
        }
    }

    fun approveAiSummary(matchId: String) {
        viewModelScope.launch {
            matchRepository.approveAiSummary(matchId)
        }
    }

    fun publishSummaryToCommunity(matchId: String) {
        val match = matchRepository.getMatchById(matchId) ?: return
        val summary = match.aiSummary ?: return

        val homeName = if (match.isHome) "SportPro" else match.rival
        val awayName = if (match.isHome) match.rival else "SportPro"
        val score = "${match.homeScore ?: 0} - ${match.awayScore ?: 0}"

        val fullContent = """
            ${summary.introduction}
            
            ${summary.development}
            
            ${summary.highlights}
            
            Nota: ${summary.analysisNote}
        """.trimIndent()

        val post = Post(
            authorName = "Entrenador DT",
            authorRole = "Cuerpo Técnico SportPro",
            date = "Ahora",
            content = fullContent,
            category = "Resumen IA",
            likesCount = 0,
            commentsCount = 0,
            isLiked = false,
            matchTitle = "$homeName vs $awayName",
            matchScore = score,
            matchCategory = match.category,
            isAiSummary = true
        )

        communityRepository.addPost(post)
    }
}
