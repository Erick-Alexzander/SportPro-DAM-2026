package devs.grupo5.sportpro.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import devs.grupo5.sportpro.data.model.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FirebasePlayerRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : PlayerRepository {

    private val _players = MutableStateFlow<List<Player>>(emptyList())
    override val players: StateFlow<List<Player>> = _players.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

    init {
        setupPlayersListener()
        auth.addAuthStateListener {
            setupPlayersListener()
        }
    }

    private fun setupPlayersListener() {
        listenerRegistration?.remove()
        val currentUserId = auth.currentUser?.uid
        if (currentUserId.isNullOrEmpty()) {
            _players.value = emptyList()
            return
        }

        listenerRegistration = firestore.collection("players")
            .whereEqualTo("trainerId", currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    return@addSnapshotListener
                }
                val playerList = snapshot.documents.map { doc -> doc.toPlayer() }
                _players.value = playerList
            }
    }

    override fun getPlayerById(id: String): Player? {
        return _players.value.find { it.id == id }
    }

    override fun getPlayersByIds(ids: List<String>): List<Player> {
        return _players.value.filter { it.id in ids }
    }

    override fun addPlayer(player: Player) {
        val currentUserId = auth.currentUser?.uid ?: ""
        val playerRef = if (player.id.isNotBlank()) {
            firestore.collection("players").document(player.id)
        } else {
            firestore.collection("players").document()
        }

        val playerId = playerRef.id
        val newPlayer = player.copy(id = playerId, trainerId = currentUserId)

        val playerData = mapOf(
            "id" to newPlayer.id,
            "fullName" to newPlayer.fullName,
            "category" to newPlayer.category,
            "position" to newPlayer.position,
            "jerseyNumber" to newPlayer.jerseyNumber,
            "age" to newPlayer.age,
            "status" to newPlayer.status,
            "dateOfBirth" to newPlayer.dateOfBirth,
            "contactEmail" to newPlayer.contactEmail,
            "dominantFoot" to newPlayer.dominantFoot,
            "size" to newPlayer.size,
            "weight" to newPlayer.weight,
            "parentName" to newPlayer.parentName,
            "parentRelation" to newPlayer.parentRelation,
            "parentPhone" to newPlayer.parentPhone,
            "emergencyPhone" to newPlayer.emergencyPhone,
            "trainerId" to newPlayer.trainerId,
            "teamId" to newPlayer.teamId
        )

        playerRef.set(playerData)
    }

    override fun updatePlayer(player: Player) {
        addPlayer(player)
    }

    companion object {
        val instance: FirebasePlayerRepository by lazy { FirebasePlayerRepository() }
    }
}

fun DocumentSnapshot.toPlayer(): Player {
    return Player(
        id = id,
        fullName = getString("fullName") ?: "",
        category = getString("category") ?: "",
        position = getString("position") ?: "",
        jerseyNumber = getLong("jerseyNumber")?.toInt() ?: 0,
        age = getLong("age")?.toInt() ?: 0,
        status = getString("status") ?: "Activo",
        dateOfBirth = getString("dateOfBirth") ?: "",
        contactEmail = getString("contactEmail") ?: "",
        dominantFoot = getString("dominantFoot") ?: "Derecho",
        size = getString("size") ?: "M",
        weight = getDouble("weight") ?: 0.0,
        parentName = getString("parentName") ?: "",
        parentRelation = getString("parentRelation") ?: "",
        parentPhone = getString("parentPhone") ?: "",
        emergencyPhone = getString("emergencyPhone") ?: "",
        trainerId = getString("trainerId") ?: "",
        teamId = getString("teamId") ?: ""
    )
}
