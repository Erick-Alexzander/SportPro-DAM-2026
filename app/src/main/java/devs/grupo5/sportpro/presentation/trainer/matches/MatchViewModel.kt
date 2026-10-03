package devs.grupo5.sportpro.presentation.trainer.matches

import androidx.lifecycle.ViewModel
import devs.grupo5.sportpro.data.model.AiSummary
import devs.grupo5.sportpro.data.model.Match
import devs.grupo5.sportpro.data.model.MatchEvent
import devs.grupo5.sportpro.data.model.Post
import devs.grupo5.sportpro.data.model.Team
import devs.grupo5.sportpro.data.repository.CommunityRepository
import devs.grupo5.sportpro.data.repository.MatchRepository
import devs.grupo5.sportpro.data.repository.MockCommunityRepository
import devs.grupo5.sportpro.data.repository.MockMatchRepository
import devs.grupo5.sportpro.data.repository.MockTeamRepository
import devs.grupo5.sportpro.data.repository.TeamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MatchViewModel(
    private val matchRepository: MatchRepository = MockMatchRepository.instance,
    private val teamRepository: TeamRepository = MockTeamRepository.instance,
    private val communityRepository: CommunityRepository = MockCommunityRepository.instance
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

    fun setSelectedStatus(status: String) {
        _selectedStatus.value = status
    }

    fun getMatchById(matchId: String): Match? {
        return matchRepository.getMatchById(matchId)
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

    fun addMatchEvent(
        matchId: String,
        minute: Int,
        eventType: String,
        icon: String,
        player: String,
        team: String,
        status: String = "Local",
        detail: String = ""
    ) {
        val event = MatchEvent(
            matchId = matchId,
            minute = minute,
            eventType = eventType,
            icon = icon,
            player = player,
            team = team,
            status = status,
            detail = detail
        )
        matchRepository.addMatchEvent(matchId, event)
        _eventConfirmationToast.value = "Evento '$eventType' registrado exitosamente"
    }

    fun clearConfirmationToast() {
        _eventConfirmationToast.value = null
    }

    fun toggleMatchPause(matchId: String) {
        matchRepository.toggleMatchPause(matchId)
    }

    fun updateMatchMinute(matchId: String, minute: Int) {
        matchRepository.updateMatchMinute(matchId, minute)
    }

    fun finishMatch(matchId: String): Match? {
        return matchRepository.finishMatch(matchId)
    }

    fun generateAiSummary(matchId: String): AiSummary {
        return matchRepository.generateAiSummary(matchId)
    }

    fun updateAiSummary(matchId: String, aiSummary: AiSummary) {
        matchRepository.updateAiSummary(matchId, aiSummary)
    }

    fun approveAiSummary(matchId: String) {
        matchRepository.approveAiSummary(matchId)
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
