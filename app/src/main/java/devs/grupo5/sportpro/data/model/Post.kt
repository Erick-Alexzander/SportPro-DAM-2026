package devs.grupo5.sportpro.data.model

data class Post(
    val id: String = "",
    val authorName: String = "",
    val authorRole: String = "Entrenador DT",
    val date: String = "",
    val content: String = "",
    val category: String = "General",
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val isLiked: Boolean = false,
    val matchTitle: String? = null,
    val matchScore: String? = null,
    val matchCategory: String? = null,
    val isAiSummary: Boolean = false
)
