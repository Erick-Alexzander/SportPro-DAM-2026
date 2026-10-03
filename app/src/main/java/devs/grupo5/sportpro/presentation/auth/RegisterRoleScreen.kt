package devs.grupo5.sportpro.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EscalatorWarning
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import devs.grupo5.sportpro.data.model.UserRole
import devs.grupo5.sportpro.ui.theme.BackgroundDark
import devs.grupo5.sportpro.ui.theme.ErrorRed
import devs.grupo5.sportpro.ui.theme.NeonGreen
import devs.grupo5.sportpro.ui.theme.NeonGreenDark
import devs.grupo5.sportpro.ui.theme.OutlineDark
import devs.grupo5.sportpro.ui.theme.SurfaceElevatedDark
import devs.grupo5.sportpro.ui.theme.TextSecondaryDark

@Composable
fun RegisterRoleScreen(
    viewModel: AuthViewModel = viewModel(),
    onBack: () -> Unit = {},
    onRegisterSuccess: (UserRole) -> Unit = {}
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var selectedRole by rememberSaveable { mutableStateOf(UserRole.TRAINER) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .systemBarsPadding()
            .imePadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = SurfaceElevatedDark,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }

            Spacer(Modifier.width(16.dp))

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BarraPaso(modifier = Modifier.weight(1f), activo = true)
                BarraPaso(modifier = Modifier.weight(1f), activo = true)
            }

            Spacer(Modifier.width(16.dp))

            Text(
                text = "Paso 2 de 2",
                fontSize = 12.sp,
                color = TextSecondaryDark
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            Text(
                text = "Tu rol",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Selecciona cómo usarás SportPro",
                fontSize = 14.sp,
                color = TextSecondaryDark
            )

            Spacer(Modifier.height(8.dp))

            if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ErrorRed.copy(alpha = 0.15f))
                        .border(1.dp, ErrorRed, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = ErrorRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Role Options
            RoleOptionCard(
                title = "Entrenador / DT",
                subtitle = "Dirige equipo y entrenamientos",
                icon = Icons.Default.Sports,
                isSelected = selectedRole == UserRole.TRAINER,
                onClick = { selectedRole = UserRole.TRAINER }
            )

            RoleOptionCard(
                title = "Jugador",
                subtitle = "Consulta tu información deportiva",
                icon = Icons.Default.SportsKabaddi,
                isSelected = selectedRole == UserRole.PLAYER,
                onClick = { selectedRole = UserRole.PLAYER }
            )

            RoleOptionCard(
                title = "Padre / Apoderado",
                subtitle = "Sigue a tu hijo en la academia",
                icon = Icons.Default.EscalatorWarning,
                isSelected = selectedRole == UserRole.PARENT,
                onClick = { selectedRole = UserRole.PARENT }
            )

            Spacer(Modifier.height(12.dp))

            // Admin info note
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, OutlineDark, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceElevatedDark)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Nota de administrador",
                        tint = TextSecondaryDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Las cuentas de Administrador se crean y habilitan únicamente mediante el proceso administrativo interno.",
                        fontSize = 12.sp,
                        color = TextSecondaryDark,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
            Button(
                onClick = {
                    viewModel.registerWithRole(
                        role = selectedRole,
                        onSuccess = { role ->
                            onRegisterSuccess(role)
                        }
                    )
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(percent = 50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonGreenDark,
                    contentColor = Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        text = "Crear mi cuenta",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun RoleOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) NeonGreen else OutlineDark,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SurfaceElevatedDark else BackgroundDark
        )
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) NeonGreen.copy(alpha = 0.2f) else SurfaceElevatedDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isSelected) NeonGreen else TextSecondaryDark,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextSecondaryDark
                )
            }

            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(50))
                    .border(
                        2.dp,
                        if (isSelected) NeonGreen else TextSecondaryDark,
                        RoundedCornerShape(50)
                    )
                    .background(if (isSelected) NeonGreen else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(BackgroundDark)
                    )
                }
            }
        }
    }
}

@Composable
private fun BarraPaso(modifier: Modifier = Modifier, activo: Boolean) {
    Box(
        modifier = modifier
            .height(4.dp)
            .background(
                color = if (activo) NeonGreen else OutlineDark,
                shape = RoundedCornerShape(percent = 50)
            )
    )
}
