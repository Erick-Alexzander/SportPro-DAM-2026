package devs.grupo5.sportpro.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import devs.grupo5.sportpro.data.model.Match
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FirebaseMatchRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : MatchRepository {

    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    override val matches: StateFlow<List<Match>> = _matches.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

    init {
        setupMatchesListener()
        auth.addAuthStateListener {
            setupMatchesListener()
        }
    }

    private fun setupMatchesListener() {
        listenerRegistration?.remove()
        val currentUserId = auth.currentUser?.uid
        if (currentUserId.isNullOrEmpty()) {
            _matches.value = emptyList()
            return
        }

        listenerRegistration = firestore.collection("matches")
            .whereEqualTo("trainerId", currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    return@addSnapshotListener
                }
                val matchList = snapshot.documents.map { doc -> doc.toMatch() }
                _matches.value = matchList
            }
    }

    override fun getMatchById(id: String): Match? {
        return _matches.value.find { it.id == id }
    }

    override fun addMatch(match: Match) {
        val currentUserId = auth.currentUser?.uid ?: ""
        val matchRef = if (match.id.isNotBlank()) {
            firestore.collection("matches").document(match.id)
        } else {
            firestore.collection("matches").document()
        }

        val matchId = matchRef.id
        val newMatch = match.copy(id = matchId, trainerId = currentUserId)

        val matchData = mutableMapOf<String, Any?>(
            "id" to newMatch.id,
            "teamId" to newMatch.teamId,
            "rival" to newMatch.rival,
            "date" to newMatch.date,
            "time" to newMatch.time,
            "stadium" to newMatch.stadium,
            "category" to newMatch.category,
            "status" to newMatch.status,
            "isHome" to newMatch.isHome,
            "homeScore" to newMatch.homeScore,
            "awayScore" to newMatch.awayScore,
            "trainerId" to newMatch.trainerId
        )

        matchRef.set(matchData)
    }

    override fun updateMatch(match: Match) {
        addMatch(match)
    }

    companion object {
        val instance: FirebaseMatchRepository by lazy { FirebaseMatchRepository() }
    }
}

fun DocumentSnapshot.toMatch(): Match {
    return Match(
        id = id,
        teamId = getString("teamId") ?: "",
        rival = getString("rival") ?: "",
        date = getString("date") ?: "",
        time = getString("time") ?: "",
        stadium = getString("stadium") ?: "",
        category = getString("category") ?: "",
        status = getString("status") ?: "Programado",
        isHome = getBoolean("isHome") ?: true,
        homeScore = getLong("homeScore")?.toInt(),
        awayScore = getLong("awayScore")?.toInt(),
        trainerId = getString("trainerId") ?: ""
    )
}
