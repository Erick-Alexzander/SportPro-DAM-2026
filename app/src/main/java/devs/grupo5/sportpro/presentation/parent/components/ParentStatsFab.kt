package devs.grupo5.sportpro.presentation.parent.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import devs.grupo5.sportpro.presentation.parent.theme.ParentNeonGreen

/**
 * Botón flotante de estadísticas del menor vinculado.
 */
@Composable
fun ParentStatsFab(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(56.dp),
        shape = CircleShape,
        containerColor = ParentNeonGreen,
        contentColor = Color(0xFF00210B)
    ) {
        Icon(
            imageVector = Icons.Default.Assessment,
            contentDescription = "Estadísticas del menor",
            modifier = Modifier.size(26.dp)
        )
    }
}
