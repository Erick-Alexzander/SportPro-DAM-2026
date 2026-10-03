package devs.grupo5.sportpro.presentation.trainer.community

import androidx.lifecycle.ViewModel
import devs.grupo5.sportpro.data.model.Post
import devs.grupo5.sportpro.data.repository.CommunityRepository
import devs.grupo5.sportpro.data.repository.FirebaseCommunityRepository
import devs.grupo5.sportpro.data.repository.MockCommunityRepository
import kotlinx.coroutines.flow.StateFlow

class CommunityViewModel(
    private val communityRepository: CommunityRepository = FirebaseCommunityRepository.instance
) : ViewModel() {

    val posts: StateFlow<List<Post>> = communityRepository.posts

    fun addPost(content: String, category: String) {
        val newPost = Post(
            authorName = "Entrenador DT",
            authorRole = "Director Técnico",
            date = "Hace un momento",
            content = content,
            category = category
        )
        communityRepository.addPost(newPost)
    }

    fun toggleLike(postId: String) {
        communityRepository.toggleLike(postId)
    }
}
