package devs.grupo5.sportpro.presentation.trainer.announcements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import devs.grupo5.sportpro.data.model.AnnouncementAudience
import devs.grupo5.sportpro.data.model.ParentAnnouncement
import devs.grupo5.sportpro.data.repository.AnnouncementRepository
import devs.grupo5.sportpro.data.repository.FirebaseAnnouncementRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TrainerAnnouncementsViewModel(
    private val repository: AnnouncementRepository = FirebaseAnnouncementRepository.instance
) : ViewModel() {

    val announcements: StateFlow<List<ParentAnnouncement>> = repository.announcements
    val syncError: StateFlow<String?> = repository.syncError

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _actionError = MutableStateFlow<String?>(null)
    val actionError: StateFlow<String?> = _actionError.asStateFlow()

    init {
        repository.startListening()
    }

    fun clearActionError() {
        _actionError.value = null
    }

    fun saveAnnouncement(
        editingId: String?,
        title: String,
        body: String,
        audience: AnnouncementAudience,
        authorName: String,
        teamId: String?,
        pinned: Boolean,
        onSuccess: () -> Unit
    ) {
        if (title.isBlank() || body.isBlank()) {
            _actionError.value = "Título y mensaje son obligatorios"
            return
        }
        viewModelScope.launch {
            _isSaving.value = true
            _actionError.value = null
            val result = if (editingId == null) {
                repository.createAnnouncement(title, body, audience, authorName, teamId, pinned)
            } else {
                repository.updateAnnouncement(editingId, title, body, audience, authorName, teamId, pinned)
            }
            _isSaving.value = false
            result.onSuccess { onSuccess() }
                .onFailure { _actionError.value = it.message ?: "No se pudo guardar el comunicado" }
        }
    }

    fun deleteAnnouncement(announcementId: String) {
        viewModelScope.launch {
            _actionError.value = null
            repository.deleteAnnouncement(announcementId)
                .onFailure { _actionError.value = it.message ?: "No se pudo eliminar el comunicado" }
        }
    }

    override fun onCleared() {
        repository.stopListening()
        super.onCleared()
    }
}
