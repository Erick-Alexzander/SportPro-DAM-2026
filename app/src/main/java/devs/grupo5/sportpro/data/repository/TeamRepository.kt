package devs.grupo5.sportpro.data.repository

import devs.grupo5.sportpro.data.model.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface TeamRepository {
    val teams: StateFlow<List<Team>>
    val syncError: StateFlow<String?>
    fun getTeamById(id: String): Team?
    suspend fun addTeam(team: Team): Result<Unit>
    suspend fun updateTeam(team: Team): Result<Unit>
    suspend fun addPlayerToTeam(teamId: String, playerId: String): Result<Unit>
    suspend fun removePlayerFromTeam(teamId: String, playerId: String): Result<Unit>
}

class MockTeamRepository private constructor() : TeamRepository {

    private val _syncError = MutableStateFlow<String?>(null)
    override val syncError: StateFlow<String?> = _syncError.asStateFlow()

    private val initialTeams = listOf(
        Team(
            id = "t1",
            name = "SportPro Primera",
            category = "Primera",
            description = "Equipo principal competitivo para la liga oficial de adultos.",
            coachName = "Entrenador DT",
            playerIds = listOf("p1", "p2", "p3"),
            playerCount = 3
        ),
        Team(
            id = "t2",
            name = "SportPro Sub-15",
            category = "Sub-15",
            description = "Cantera de alto rendimiento para jugadores de 13 a 15 años.",
            coachName = "Entrenador DT",
            playerIds = listOf("p4", "p5", "p6"),
            playerCount = 3
        ),
        Team(
            id = "t3",
            name = "SportPro Sub-10",
            category = "Sub-10",
            description = "Formación inicial técnica y recreativa para niños de 8 a 10 años.",
            coachName = "Entrenador DT",
            playerIds = listOf("p7", "p8"),
            playerCount = 2
        )
    )

    private val _teams = MutableStateFlow(initialTeams)
    override val teams: StateFlow<List<Team>> = _teams.asStateFlow()

    override fun getTeamById(id: String): Team? {
        return _teams.value.find { it.id == id }
    }

    override suspend fun addTeam(team: Team): Result<Unit> {
        val newTeam = if (team.id.isEmpty()) {
            team.copy(
                id = "t_${System.currentTimeMillis()}",
                playerCount = team.playerIds.size
            )
        } else {
            team.copy(playerCount = team.playerIds.size)
        }
        _teams.value = _teams.value + newTeam
        return Result.success(Unit)
    }

    override suspend fun updateTeam(team: Team): Result<Unit> {
        _teams.value = _teams.value.map {
            if (it.id == team.id) team else it
        }
        return Result.success(Unit)
    }

    override suspend fun addPlayerToTeam(teamId: String, playerId: String): Result<Unit> {
        _teams.value = _teams.value.map { team ->
            if (team.id == teamId) {
                if (playerId !in team.playerIds) {
                    val updatedIds = team.playerIds + playerId
                    team.copy(playerIds = updatedIds, playerCount = updatedIds.size)
                } else team
            } else team
        }
        return Result.success(Unit)
    }

    override suspend fun removePlayerFromTeam(teamId: String, playerId: String): Result<Unit> {
        _teams.value = _teams.value.map { team ->
            if (team.id == teamId) {
                val updatedIds = team.playerIds.filter { it != playerId }
                team.copy(playerIds = updatedIds, playerCount = updatedIds.size)
            } else team
        }
        return Result.success(Unit)
    }

    companion object {
        val instance: MockTeamRepository by lazy { MockTeamRepository() }
    }
}
