package devs.grupo5.sportpro.presentation.parent.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val ParentBackground = Color(0xFF0E1311)
val ParentBackgroundDeep = Color(0xFF0A0F0D)
val ParentSurface = Color(0xFF151D18)
val ParentSurfaceVariant = Color(0xFF19221D)
val ParentBorder = Color(0xFF253029)

val ParentNeonGreen = Color(0xFF00E676)
val ParentEmerald = Color(0xFF00C853)
val ParentGreenContainer = Color(0xFF0C2417)

val ParentStatusBrown = Color(0xFF3D2C02)
val ParentStatusGold = Color(0xFFFFC400)
val ParentWarning = Color(0xFFFF9100)
val ParentError = Color(0xFFD50000)

val ParentTextPrimary = Color(0xFFFFFFFF)
val ParentTextSecondary = Color(0xFFB0B8B4)
val ParentTextMuted = Color(0xFF79837E)

private val ParentColorScheme = darkColorScheme(
    primary = ParentNeonGreen,
    onPrimary = Color(0xFF00210B),
    primaryContainer = ParentGreenContainer,
    onPrimaryContainer = ParentNeonGreen,
    secondary = ParentEmerald,
    onSecondary = Color(0xFF00210B),
    background = ParentBackground,
    onBackground = ParentTextPrimary,
    surface = ParentSurface,
    onSurface = ParentTextPrimary,
    surfaceVariant = ParentSurfaceVariant,
    onSurfaceVariant = ParentTextSecondary,
    outline = ParentBorder,
    error = ParentError
)

@Composable
fun SportProParentTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ParentColorScheme,
        content = content
    )
}
