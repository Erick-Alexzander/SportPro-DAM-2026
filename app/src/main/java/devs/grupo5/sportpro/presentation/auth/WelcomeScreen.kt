package devs.grupo5.sportpro.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import devs.grupo5.sportpro.ui.theme.BackgroundDark
import devs.grupo5.sportpro.ui.theme.NeonGreen
import devs.grupo5.sportpro.ui.theme.NeonGreenDark
import devs.grupo5.sportpro.ui.theme.OutlineDark
import devs.grupo5.sportpro.ui.theme.SurfaceElevatedDark
import devs.grupo5.sportpro.ui.theme.TextSecondaryDark

@Composable
fun WelcomeScreen(
    onCreateAccountClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .systemBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo Badge
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(SurfaceElevatedDark)
                    .border(2.dp, NeonGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SportsSoccer,
                    contentDescription = "SportPro Logo",
                    tint = NeonGreen,
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "BIENVENIDO A",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color.White)) {
                        append("SPORT")
                    }
                    withStyle(SpanStyle(color = NeonGreen)) {
                        append("PRO")
                    }
                },
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "La plataforma completa para gestión de academias de fútbol. Jugadores, entrenamientos, partidos, estadísticas y comunidad en un solo lugar.",
                fontSize = 14.sp,
                color = TextSecondaryDark,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Benefits list
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceElevatedDark)
                    .border(1.dp, OutlineDark, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                BenefitItem("Gestión de academia y equipos")
                BenefitItem("Control de entrenamientos y partidos")
                BenefitItem("Estadísticas y rendimiento")
                BenefitItem("Comunidad deportiva")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onCreateAccountClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(percent = 50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonGreenDark,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Crear cuenta",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(percent = 50),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonGreen),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = NeonGreen
                )
            ) {
                Text(
                    text = "Iniciar sesión",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun BenefitItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = NeonGreen,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}
