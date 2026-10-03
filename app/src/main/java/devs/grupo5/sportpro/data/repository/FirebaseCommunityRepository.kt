package devs.grupo5.sportpro.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import devs.grupo5.sportpro.data.model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FirebaseCommunityRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : CommunityRepository {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    override val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

    init {
        setupPostsListener()
        auth.addAuthStateListener {
            setupPostsListener()
        }
    }

    private fun setupPostsListener() {
        listenerRegistration?.remove()

        listenerRegistration = firestore.collection("posts")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    return@addSnapshotListener
                }
                val postList = snapshot.documents.map { doc -> doc.toPost() }
                _posts.value = postList
            }
    }

    override fun addPost(post: Post) {
        val currentUserId = auth.currentUser?.uid ?: ""
        val postRef = if (post.id.isNotBlank()) {
            firestore.collection("posts").document(post.id)
        } else {
            firestore.collection("posts").document()
        }

        val postId = postRef.id
        val newPost = post.copy(id = postId, trainerId = currentUserId)

        val postData = mapOf(
            "id" to newPost.id,
            "authorName" to newPost.authorName,
            "authorRole" to newPost.authorRole,
            "date" to newPost.date,
            "content" to newPost.content,
            "category" to newPost.category,
            "likesCount" to newPost.likesCount,
            "commentsCount" to newPost.commentsCount,
            "isLiked" to newPost.isLiked,
            "trainerId" to newPost.trainerId
        )

        postRef.set(postData)
    }

    override fun toggleLike(postId: String) {
        val postRef = firestore.collection("posts").document(postId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(postRef)
            if (snapshot.exists()) {
                val currentPost = snapshot.toPost()
                val newIsLiked = !currentPost.isLiked
                val newLikesCount = if (newIsLiked) currentPost.likesCount + 1 else (currentPost.likesCount - 1).coerceAtLeast(0)
                transaction.update(
                    postRef,
                    "isLiked", newIsLiked,
                    "likesCount", newLikesCount
                )
            }
        }
    }

    companion object {
        val instance: FirebaseCommunityRepository by lazy { FirebaseCommunityRepository() }
    }
}

fun DocumentSnapshot.toPost(): Post {
    return Post(
        id = id,
        authorName = getString("authorName") ?: "Entrenador DT",
        authorRole = getString("authorRole") ?: "Director Técnico",
        date = getString("date") ?: "",
        content = getString("content") ?: "",
        category = getString("category") ?: "General",
        likesCount = getLong("likesCount")?.toInt() ?: 0,
        commentsCount = getLong("commentsCount")?.toInt() ?: 0,
        isLiked = getBoolean("isLiked") ?: false,
        trainerId = getString("trainerId") ?: ""
    )
}
