package devs.grupo5.sportpro.presentation.player.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PlayerBackground = Color(0xFF101413)
val PlayerSurface = Color(0xFF1A211E)
val PlayerSurfaceVariant = Color(0xFF232C28)
val PlayerBorder = Color(0xFF2A322E)

val PlayerNeonGreen = Color(0xFF00E676)
val PlayerNeonGreenDark = Color(0xFF00B85C)
val PlayerGreenContainer = Color(0xFF0A2A1C)

val PlayerBlue = Color(0xFF13294B)
val PlayerBlueBorder = Color(0xFF3B82F6)
val PlayerBlueLight = Color(0xFF60A5FA)

val PlayerBrown = Color(0xFF7A4E2D)

val PlayerTextPrimary = Color(0xFFFFFFFF)
val PlayerTextSecondary = Color(0xFF9AA4A0)
val PlayerTextMuted = Color(0xFF6C7571)

val PlayerWarning = Color(0xFFFFB300)
val PlayerError = Color(0xFFFF5252)
val PlayerPink = Color(0xFFFF6B9D)
val PlayerPurple = Color(0xFF8B5CF6)

private val PlayerColorScheme = darkColorScheme(
    primary = PlayerNeonGreen,
    onPrimary = Color(0xFF00210B),
    primaryContainer = PlayerGreenContainer,
    onPrimaryContainer = PlayerNeonGreen,
    background = PlayerBackground,
    onBackground = PlayerTextPrimary,
    surface = PlayerSurface,
    onSurface = PlayerTextPrimary,
    surfaceVariant = PlayerSurfaceVariant,
    onSurfaceVariant = PlayerTextSecondary,
    outline = PlayerBorder
)

@Composable
fun SportProPlayerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PlayerColorScheme,
        content = content
    )
}
