package devs.grupo5.sportpro.presentation.trainer.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import devs.grupo5.sportpro.data.model.Player
import devs.grupo5.sportpro.data.repository.FirebasePlayerRepository
import devs.grupo5.sportpro.data.repository.FirebaseTeamRepository
import devs.grupo5.sportpro.data.repository.PlayerRepository
import devs.grupo5.sportpro.data.repository.TeamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class PlayerViewModel(
    private val playerRepository: PlayerRepository = FirebasePlayerRepository.instance,
    private val teamRepository: TeamRepository = FirebaseTeamRepository.instance
) : ViewModel() {

    val players: StateFlow<List<Player>> = playerRepository.players
    val syncError: StateFlow<String?> = playerRepository.syncError

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Todos")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Draft registration state
    var registrationDraft = Player()
    var currentTeamIdForRegistration: String? = null

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun startRegistrationForTeam(teamId: String?) {
        currentTeamIdForRegistration = teamId
        registrationDraft = Player()
    }

    fun calculateAge(birthDateStr: String): Int {
        if (birthDateStr.isBlank()) return 0
        return try {
            val parts = birthDateStr.split("/", "-", ".").mapNotNull { it.trim().toIntOrNull() }
            if (parts.size == 3) {
                val (day, month, year) = if (parts[0] > 1000) Triple(parts[2], parts[1], parts[0]) else Triple(parts[0], parts[1], parts[2])
                val today = LocalDate.now()
                val birthDate = LocalDate.of(year, month, day)
                var age = today.year - birthDate.year
                if (today.monthValue < birthDate.monthValue || (today.monthValue == birthDate.monthValue && today.dayOfMonth < birthDate.dayOfMonth)) {
                    age--
                }
                age.coerceAtLeast(0)
            } else 15
        } catch (_: Exception) {
            15
        }
    }

    fun updatePersonalData(fullName: String, dateOfBirth: String, contactEmail: String, category: String) {
        val calculatedAge = calculateAge(dateOfBirth)
        registrationDraft = registrationDraft.copy(
            fullName = fullName.trim(),
            dateOfBirth = dateOfBirth.trim(),
            contactEmail = contactEmail.trim(),
            category = category,
            age = calculatedAge
        )
    }

    fun updateSportData(position: String, dominantFoot: String, jerseyNumber: Int, size: String, weight: Double) {
        registrationDraft = registrationDraft.copy(
            position = position,
            dominantFoot = dominantFoot,
            jerseyNumber = jerseyNumber,
            size = size,
            weight = weight
        )
    }

    fun updateResponsibleData(parentPhone: String, parentName: String, parentRelation: String, emergencyPhone: String) {
        registrationDraft = registrationDraft.copy(
            parentPhone = parentPhone.trim(),
            parentName = parentName.trim(),
            parentRelation = parentRelation.trim(),
            emergencyPhone = emergencyPhone.trim()
        )
    }

    fun registerPlayer(onResult: (Boolean) -> Unit = {}) {
        val teamId = currentTeamIdForRegistration
        
        viewModelScope.launch {
            _isLoading.value = true
            
            val result = if (teamId != null) {
                playerRepository.addPlayerWithTeam(registrationDraft, teamId)
            } else {
                playerRepository.addPlayer(registrationDraft)
            }
            
            if (result.isSuccess) {
                // Reset draft
                registrationDraft = Player()
                currentTeamIdForRegistration = null
                onResult(true)
            } else {
                onResult(false)
            }
            _isLoading.value = false
        }
    }
}
