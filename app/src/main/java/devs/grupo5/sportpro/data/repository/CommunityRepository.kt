package devs.grupo5.sportpro.data.repository

import devs.grupo5.sportpro.data.model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface CommunityRepository {
    val posts: StateFlow<List<Post>>
    fun addPost(post: Post)
    fun toggleLike(postId: String)
}

class MockCommunityRepository private constructor() : CommunityRepository {

    private val initialPosts = listOf(
        Post(
            id = "post1",
            authorName = "Entrenador DT",
            authorRole = "Director Técnico",
            date = "Hace 2 horas",
            content = "Gran sesión de entrenamiento con la Sub-15. La intensidad en las transiciones rápidas mejoró notablemente. ¡Seguimos preparando el partido del sábado!",
            category = "Entrenamiento",
            likesCount = 14,
            commentsCount = 3,
            isLiked = true
        ),
        Post(
            id = "post2",
            authorName = "Prof. Marcelo Rivas",
            authorRole = "Preparador Físico",
            date = "Ayer",
            content = "Recordatorio para la plantilla de Primera: Mañana realizamos mediciones antropométricas antes del entrenamiento táctico.",
            category = "Anuncio",
            likesCount = 22,
            commentsCount = 7,
            isLiked = false
        )
    )

    private val _posts = MutableStateFlow(initialPosts)
    override val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    override fun addPost(post: Post) {
        val newPost = if (post.id.isEmpty()) {
            post.copy(id = "post_${System.currentTimeMillis()}")
        } else {
            post
        }
        _posts.value = listOf(newPost) + _posts.value
    }

    override fun toggleLike(postId: String) {
        _posts.value = _posts.value.map { p ->
            if (p.id == postId) {
                val newLiked = !p.isLiked
                val newLikesCount = if (newLiked) p.likesCount + 1 else p.likesCount - 1
                p.copy(isLiked = newLiked, likesCount = newLikesCount)
            } else p
        }
    }

    companion object {
        val instance: MockCommunityRepository by lazy { MockCommunityRepository() }
    }
}
