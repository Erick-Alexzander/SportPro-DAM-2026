package devs.grupo5.sportpro.presentation.player.community

import androidx.compose.ui.graphics.Color
import devs.grupo5.sportpro.presentation.player.theme.PlayerBrown
import devs.grupo5.sportpro.presentation.player.theme.PlayerNeonGreen
import devs.grupo5.sportpro.presentation.player.theme.PlayerPurple

/**
 * Tono cromático del avatar de un autor dentro de la comunidad.
 */
internal enum class AvatarTone {
    GREEN,
    BROWN,
    PURPLE
}

internal fun AvatarTone.backgroundColor(): Color = when (this) {
    AvatarTone.GREEN -> PlayerNeonGreen
    AvatarTone.BROWN -> PlayerBrown
    AvatarTone.PURPLE -> PlayerPurple
}

internal fun AvatarTone.contentColor(): Color = when (this) {
    AvatarTone.GREEN -> Color(0xFF00210B)
    AvatarTone.BROWN -> Color.White
    AvatarTone.PURPLE -> Color.White
}

internal data class CommunityPost(
    val id: String,
    val initials: String,
    val tone: AvatarTone,
    val author: String,
    val subtitle: String,
    val text: String,
    val hasImage: Boolean,
    val likes: Int,
    val comments: Int
)

internal data class OpenCallUp(
    val id: String,
    val club: String,
    val category: String,
    val position: String,
    val description: String,
    val date: String,
    val location: String
)

internal data class PostComment(
    val id: String,
    val initials: String,
    val tone: AvatarTone,
    val author: String,
    val text: String,
    val meta: String?
)

internal val communityPosts = listOf(
    CommunityPost(
        id = "post1",
        initials = "AS",
        tone = AvatarTone.GREEN,
        author = "Academia SportPro",
        subtitle = "Academia SportPro · Hace 2h",
        text = "¡Gran victoria este fin de semana! 🏆 Felicitaciones al equipo Primera por su rendimiento. " +
            "Carlos Mendoza con doblete. Seguimos trabajando para el próximo partido del 9 de noviembre. " +
            "¡Arriba los colores!",
        hasImage = true,
        likes = 48,
        comments = 12
    ),
    CommunityPost(
        id = "post2",
        initials = "CR",
        tone = AvatarTone.BROWN,
        author = "Coach Ramírez",
        subtitle = "Academia SportPro · Hace 5h",
        text = "Excelente sesión de entrenamiento hoy. El trabajo en posicionamiento defensivo está " +
            "evolucionando muy bien. Mañana a las 16:00 continuamos. ¡Nos vemos en la cancha!",
        hasImage = false,
        likes = 31,
        comments = 7
    )
)

internal val callUpFilters = listOf("Todos", "Sub-10", "Sub-15", "Primera", "POR", "DEF", "MED", "DEL")

internal val openCallUps = listOf(
    OpenCallUp(
        id = "call1",
        club = "Academia SportPro",
        category = "Sub-15",
        position = "MED",
        description = "Buscamos volante creativo con buena visión de juego para completar nuestro plantel.",
        date = "16 nov 2024",
        location = "Estadio Municipal, Santiago"
    ),
    OpenCallUp(
        id = "call2",
        club = "Club Deportivo Norte",
        category = "Primera",
        position = "POR",
        description = "Necesitamos portero titular para primera división regional. Mínimo 17 años.",
        date = "23 nov 2024",
        location = "Cancha Norte, Las Condes"
    ),
    OpenCallUp(
        id = "call3",
        club = "Estrellas FC",
        category = "Sub-10",
        position = "DEL",
        description = "Captación masiva Sub-10. Todos los niños entre 8 y 10 años son bienvenidos.",
        date = "30 nov 2024",
        location = "Complejo Estrellas, Ñuñoa"
    )
)

internal val postComments = listOf(
    PostComment(
        id = "comment1",
        initials = "DH",
        tone = AvatarTone.PURPLE,
        author = "Diego Herrera",
        text = "¡Excelente trabajo equipo! A seguir así 💪",
        meta = "Hace 1h · 💚 5 · Responder · Reportar"
    ),
    PostComment(
        id = "comment2",
        initials = "MG",
        tone = AvatarTone.BROWN,
        author = "María González",
        text = "Muy orgullosa del progreso de los chicos 🙌",
        meta = "Hace 2h · 💚 3 · Responder · Reportar"
    ),
    PostComment(
        id = "comment3",
        initials = "CR",
        tone = AvatarTone.BROWN,
        author = "Carlos Ramírez",
        text = "Gracias a todos por el apoyo. ¡Seguimos trabajando!",
        meta = null
    )
)

internal fun findPostById(id: String): CommunityPost =
    communityPosts.firstOrNull { it.id == id } ?: communityPosts.last()
