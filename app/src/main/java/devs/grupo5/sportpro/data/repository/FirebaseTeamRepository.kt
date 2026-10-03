package devs.grupo5.sportpro.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import devs.grupo5.sportpro.data.model.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FirebaseTeamRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : TeamRepository {

    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    override val teams: StateFlow<List<Team>> = _teams.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

    init {
        setupTeamsListener()
        auth.addAuthStateListener {
            setupTeamsListener()
        }
    }

    private fun setupTeamsListener() {
        listenerRegistration?.remove()
        val currentUserId = auth.currentUser?.uid
        if (currentUserId.isNullOrEmpty()) {
            _teams.value = emptyList()
            return
        }

        listenerRegistration = firestore.collection("teams")
            .whereEqualTo("trainerId", currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    return@addSnapshotListener
                }
                val teamList = snapshot.documents.map { doc -> doc.toTeam() }
                _teams.value = teamList
            }
    }

    override fun getTeamById(id: String): Team? {
        return _teams.value.find { it.id == id }
    }

    override fun addTeam(team: Team) {
        val currentUserId = auth.currentUser?.uid ?: ""
        val teamRef = if (team.id.isNotBlank()) {
            firestore.collection("teams").document(team.id)
        } else {
            firestore.collection("teams").document()
        }

        val teamId = teamRef.id
        val newTeam = team.copy(
            id = teamId,
            trainerId = currentUserId,
            playerCount = team.playerIds.size
        )

        val teamData = mapOf(
            "id" to newTeam.id,
            "name" to newTeam.name,
            "category" to newTeam.category,
            "description" to newTeam.description,
            "coachName" to newTeam.coachName,
            "playerIds" to newTeam.playerIds,
            "playerCount" to newTeam.playerCount,
            "trainerId" to newTeam.trainerId
        )

        teamRef.set(teamData)
    }

    override fun addPlayerToTeam(teamId: String, playerId: String) {
        val teamRef = firestore.collection("teams").document(teamId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(teamRef)
            if (snapshot.exists()) {
                val currentTeam = snapshot.toTeam()
                if (!currentTeam.playerIds.contains(playerId)) {
                    val updatedPlayerIds = currentTeam.playerIds + playerId
                    transaction.update(
                        teamRef,
                        "playerIds", updatedPlayerIds,
                        "playerCount", updatedPlayerIds.size
                    )
                }
            }
        }
    }

    override fun removePlayerFromTeam(teamId: String, playerId: String) {
        val teamRef = firestore.collection("teams").document(teamId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(teamRef)
            if (snapshot.exists()) {
                val currentTeam = snapshot.toTeam()
                if (currentTeam.playerIds.contains(playerId)) {
                    val updatedPlayerIds = currentTeam.playerIds.filter { it != playerId }
                    transaction.update(
                        teamRef,
                        "playerIds", updatedPlayerIds,
                        "playerCount", updatedPlayerIds.size
                    )
                }
            }
        }
    }

    companion object {
        val instance: FirebaseTeamRepository by lazy { FirebaseTeamRepository() }
    }
}

fun DocumentSnapshot.toTeam(): Team {
    @Suppress("UNCHECKED_CAST")
    val playersList = (get("playerIds") as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
    return Team(
        id = id,
        name = getString("name") ?: "",
        category = getString("category") ?: "",
        description = getString("description") ?: "",
        coachName = getString("coachName") ?: "Entrenador DT",
        playerIds = playersList,
        playerCount = getLong("playerCount")?.toInt() ?: playersList.size,
        trainerId = getString("trainerId") ?: ""
    )
}
