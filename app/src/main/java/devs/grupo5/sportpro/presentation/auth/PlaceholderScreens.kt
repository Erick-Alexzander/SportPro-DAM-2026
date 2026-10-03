package devs.grupo5.sportpro.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.EscalatorWarning
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import devs.grupo5.sportpro.ui.theme.BackgroundDark
import devs.grupo5.sportpro.ui.theme.NeonGreen
import devs.grupo5.sportpro.ui.theme.NeonGreenDark
import devs.grupo5.sportpro.ui.theme.OutlineDark
import devs.grupo5.sportpro.ui.theme.SurfaceElevatedDark
import devs.grupo5.sportpro.ui.theme.TextSecondaryDark

@Composable
fun PlayerPlaceholderScreen(
    onLogoutClick: () -> Unit = {}
) {
    RolePlaceholderScreen(
        roleTitle = "Módulo de Jugador",
        message = "El módulo de Jugador está en desarrollo.\nPronto podrás consultar tus estadísticas, historial de partidos y llamados a convocatorias.",
        icon = Icons.Default.SportsKabaddi,
        onLogoutClick = onLogoutClick
    )
}

@Composable
fun ParentPlaceholderScreen(
    onLogoutClick: () -> Unit = {}
) {
    RolePlaceholderScreen(
        roleTitle = "Módulo de Padre / Apoderado",
        message = "El módulo de Padre/Apoderado está en desarrollo.\nPronto podrás hacer seguimiento al rendimiento de tu hijo y comunicarte con la academia.",
        icon = Icons.Default.EscalatorWarning,
        onLogoutClick = onLogoutClick
    )
}

@Composable
private fun RolePlaceholderScreen(
    roleTitle: String,
    message: String,
    icon: ImageVector,
    onLogoutClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceElevatedDark)
                .border(1.dp, OutlineDark, RoundedCornerShape(20.dp))
                .padding(28.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(NeonGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NeonGreen,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = roleTitle,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = message,
                fontSize = 14.sp,
                color = TextSecondaryDark,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(percent = 50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonGreenDark,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Cerrar sesión",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Volver al inicio",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
