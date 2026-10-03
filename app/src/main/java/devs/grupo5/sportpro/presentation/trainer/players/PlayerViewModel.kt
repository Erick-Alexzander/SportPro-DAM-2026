package devs.grupo5.sportpro.presentation.trainer.players

import androidx.lifecycle.ViewModel
import devs.grupo5.sportpro.data.model.Player
import devs.grupo5.sportpro.data.repository.MockPlayerRepository
import devs.grupo5.sportpro.data.repository.PlayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayerViewModel(
    private val playerRepository: PlayerRepository = MockPlayerRepository.instance
) : ViewModel() {

    val players: StateFlow<List<Player>> = playerRepository.players

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Todos")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Temporary registration state
    var registrationDraft = Player()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun updatePersonalData(fullName: String, dateOfBirth: String, contactEmail: String, category: String) {
        registrationDraft = registrationDraft.copy(
            fullName = fullName,
            dateOfBirth = dateOfBirth,
            contactEmail = contactEmail,
            category = category
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
            parentPhone = parentPhone,
            parentName = parentName,
            parentRelation = parentRelation,
            emergencyPhone = emergencyPhone
        )
    }

    fun registerPlayer() {
        if (registrationDraft.fullName.isNotBlank()) {
            playerRepository.addPlayer(registrationDraft)
            registrationDraft = Player() // Reset draft
        }
    }
}
