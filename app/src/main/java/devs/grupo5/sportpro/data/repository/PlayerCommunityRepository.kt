package devs.grupo5.sportpro.data.repository

import devs.grupo5.sportpro.presentation.player.community.AvatarTone
import devs.grupo5.sportpro.presentation.player.community.PostComment
import devs.grupo5.sportpro.presentation.player.community.postComments
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

interface PlayerCommunityRepository {
    val comments: StateFlow<List<PostComment>>
    fun getCommentsForPost(postId: String): List<PostComment>
    fun addComment(postId: String, authorUid: String, authorName: String, text: String, replyToId: String? = null): Result<Unit>
    fun deleteComment(commentId: String, authorUid: String): Result<Unit>
}

class MockPlayerCommunityRepository private constructor() : PlayerCommunityRepository {
    private val _comments = MutableStateFlow<List<PostComment>>(postComments)
    override val comments = _comments.asStateFlow()

    override fun getCommentsForPost(postId: String): List<PostComment> {
        // En un caso real filtraría por postId. Como los mocks actuales no tienen postId atado en su definición,
        // devolvemos la lista entera temporalmente.
        return _comments.value
    }

    override fun addComment(postId: String, authorUid: String, authorName: String, text: String, replyToId: String?): Result<Unit> {
        if (text.isBlank()) return Result.failure(Exception("El comentario no puede estar vacío"))

        val newComment = PostComment(
            id = UUID.randomUUID().toString(),
            initials = authorName.take(2).uppercase(),
            tone = AvatarTone.GREEN,
            author = authorName,
            authorUid = authorUid, // Se asume expansión del modelo
            text = text,
            meta = "Ahora",
            replyToId = replyToId
        )
        
        _comments.value = _comments.value + newComment
        return Result.success(Unit)
    }

    override fun deleteComment(commentId: String, authorUid: String): Result<Unit> {
        val comment = _comments.value.find { it.id == commentId }
        if (comment == null) return Result.failure(Exception("Comentario no encontrado"))
        
        if (comment.authorUid != authorUid) {
            return Result.failure(Exception("No tienes permiso para eliminar este comentario"))
        }

        _comments.value = _comments.value.filter { it.id != commentId }
        return Result.success(Unit)
    }

    companion object {
        val instance: MockPlayerCommunityRepository by lazy { MockPlayerCommunityRepository() }
    }
}
