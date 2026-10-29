package devs.grupo5.sportpro.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import devs.grupo5.sportpro.data.model.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class FirebaseTeamRepository private constructor() : TeamRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    override val teams: StateFlow<List<Team>> = _teams.asStateFlow()

    private val _syncError = MutableStateFlow<String?>(null)
    override val syncError: StateFlow<String?> = _syncError.asStateFlow()

    private var teamsListener: ListenerRegistration? = null
    
    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                startListening(user.uid)
            } else {
                stopListening()
                _teams.value = emptyList()
            }
        }
    }

    private fun startListening(uid: String) {
        // Prevenir listeners duplicados
        teamsListener?.remove()
        teamsListener = firestore.collection("teams")
            .whereEqualTo("ownerUid", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _syncError.value = error.message
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    _syncError.value = null
                    val teamsList = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Team::class.java)?.copy(id = doc.id)
                    }
                    _teams.value = teamsList
                }
            }
    }

    private fun stopListening() {
        teamsListener?.remove()
        teamsListener = null
        _syncError.value = null
    }

    override fun getTeamById(id: String): Team? {
        return _teams.value.find { it.id == id }
    }

    override suspend fun addTeam(team: Team): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))
        
        return try {
            val docRef = firestore.collection("teams").document()
            
            // Forzamos el ID y owner correctos
            val finalTeam = team.copy(
                id = docRef.id,
                ownerUid = uid,
                playerCount = team.playerIds.size
            )
            
            docRef.set(finalTeam).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTeam(team: Team): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))

        return try {
            val docRef = firestore.collection("teams").document(team.id)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                if (!snapshot.exists()) {
                    throw Exception("El equipo no existe")
                }
                
                val ownerUid = snapshot.getString("ownerUid")
                if (ownerUid != uid) {
                    throw Exception("No tienes permisos para modificar este equipo")
                }

                // Actualizar solo los campos permitidos, evitando sobreescribir playerIds 
                // con info desactualizada de la UI.
                transaction.update(docRef, mapOf(
                    "name" to team.name,
                    "category" to team.category,
                    "description" to team.description,
                    "coachName" to team.coachName
                ))
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addPlayerToTeam(teamId: String, playerId: String): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            val teamRef = firestore.collection("teams").document(teamId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(teamRef)
                if (!snapshot.exists()) {
                    throw Exception("El equipo no existe")
                }
                val ownerUid = snapshot.getString("ownerUid")
                if (ownerUid != uid) {
                    throw Exception("No tienes permisos para modificar este equipo")
                }

                val currentPlayers = snapshot.get("playerIds") as? List<*> ?: emptyList<Any>()
                if (!currentPlayers.contains(playerId)) {
                    transaction.update(teamRef, "playerIds", FieldValue.arrayUnion(playerId))
                    transaction.update(teamRef, "playerCount", currentPlayers.size + 1)
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removePlayerFromTeam(teamId: String, playerId: String): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))
        return try {
            val teamRef = firestore.collection("teams").document(teamId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(teamRef)
                if (!snapshot.exists()) {
                    throw Exception("El equipo no existe")
                }
                val ownerUid = snapshot.getString("ownerUid")
                if (ownerUid != uid) {
                    throw Exception("No tienes permisos para modificar este equipo")
                }

                val currentPlayers = snapshot.get("playerIds") as? List<*> ?: emptyList<Any>()
                if (currentPlayers.contains(playerId)) {
                    transaction.update(teamRef, "playerIds", FieldValue.arrayRemove(playerId))
                    // Asegurar de no dejar count en negativo
                    val newCount = maxOf(0, currentPlayers.size - 1)
                    transaction.update(teamRef, "playerCount", newCount)
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        val instance: FirebaseTeamRepository by lazy { FirebaseTeamRepository() }
    }
}
