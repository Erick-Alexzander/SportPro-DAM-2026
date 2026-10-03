package devs.grupo5.sportpro.presentation.trainer.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val SportProDarkBackground = Color(0xFF0F1117)
val SportProCardBackground = Color(0xFF181C26)
val SportProCardBorder = Color(0xFF282E3D)

val SportProGreen = Color(0xFF00E676)
val SportProGreenDark = Color(0xFF00B050)
val SportProGreenContainer = Color(0xFF0A291A)

val SportProTextPrimary = Color(0xFFFFFFFF)
val SportProTextSecondary = Color(0xFF9DA4B4)
val SportProTextMuted = Color(0xFF6C758A)

val SportProWarning = Color(0xFFFFB300)
val SportProError = Color(0xFFFF5252)

private val TrainerColorScheme = darkColorScheme(
    primary = SportProGreen,
    onPrimary = Color(0xFF00210B),
    primaryContainer = SportProGreenContainer,
    onPrimaryContainer = SportProGreen,
    background = SportProDarkBackground,
    onBackground = SportProTextPrimary,
    surface = SportProCardBackground,
    onSurface = SportProTextPrimary,
    surfaceVariant = Color(0xFF222834),
    onSurfaceVariant = SportProTextSecondary,
    outline = SportProCardBorder
)

@Composable
fun SportProTrainerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TrainerColorScheme,
        content = content
    )
}
