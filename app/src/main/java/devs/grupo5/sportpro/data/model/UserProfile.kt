package devs.grupo5.sportpro.data.model

enum class UserRole {
    TRAINER,
    PLAYER,
    PARENT,
    ADMIN
}

data class UserProfile(
    val uid: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val role: UserRole = UserRole.TRAINER,
    val birthDate: String? = null
)
