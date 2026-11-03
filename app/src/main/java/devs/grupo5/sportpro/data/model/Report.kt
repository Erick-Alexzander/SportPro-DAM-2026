package devs.grupo5.sportpro.data.model

enum class ReportTargetType {
    PUBLICATION,
    COMMENT,
    TRYOUT
}

enum class ReportReason(val displayName: String) {
    HARASSMENT("Acoso o insultos"),
    DISCRIMINATION("Discriminación"),
    SPAM("Spam"),
    PRIVATE_INFO_EXPOSURE("Exposición de información privada"),
    MINOR_INAPPROPRIATE("Contenido inapropiado para menores"),
    IMPERSONATION("Suplantación"),
    OTHER("Otro")
}

enum class ReportStatus {
    PENDING,
    IN_REVIEW,
    RESOLVED,
    DISMISSED
}

enum class ContentModerationStatus {
    VISIBLE,
    HIDDEN,
    BLOCKED
}

data class ContentReport(
    val id: String = "",
    val reporterUid: String = "",
    val targetType: ReportTargetType = ReportTargetType.PUBLICATION,
    val targetId: String = "",
    val parentPostId: String? = null,
    val reason: ReportReason = ReportReason.OTHER,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val status: ReportStatus = ReportStatus.PENDING,
    val minorProtectionFlag: Boolean = false // Marcador de prioridad
)

data class ModerationAction(
    val id: String = "",
    val reportId: String? = null,
    val actionType: ContentModerationStatus = ContentModerationStatus.HIDDEN,
    val targetType: ReportTargetType = ReportTargetType.PUBLICATION,
    val targetId: String = "",
    val adminUid: String = "",
    val date: Long = System.currentTimeMillis(),
    val reason: String = ""
)

sealed class ReportOperationResult<out T> {
    data class Success<out T>(val data: T) : ReportOperationResult<T>()
    object Duplicate : ReportOperationResult<Nothing>()
    object InvalidContent : ReportOperationResult<Nothing>()
    object Unauthorized : ReportOperationResult<Nothing>()
    data class StorageError(val exception: Exception) : ReportOperationResult<Nothing>()
}
