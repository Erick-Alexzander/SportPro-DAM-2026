package devs.grupo5.sportpro.presentation.trainer.profile

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBorder
import devs.grupo5.sportpro.presentation.trainer.theme.SportProDarkBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProError
import devs.grupo5.sportpro.presentation.trainer.theme.SportProGreen
import devs.grupo5.sportpro.presentation.trainer.theme.SportProGreenContainer
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextMuted
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextPrimary
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextSecondary
import devs.grupo5.sportpro.presentation.trainer.theme.SportProWarning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    fullName: String = "Carlos Ramírez",
    email: String = "entrenador@sportpro.com",
    role: String = "Entrenador / DT",
    academy: String = "Academia SportPro",
    teams: String = "Primera • Sub-15",
    initials: String = "CR",
    onBackClick: () -> Unit = {},
    onThemeClick: () -> Unit = {},
    onEditPhotoClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    var activeBottomSheetSection by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SportProDarkBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top App Bar
            item {
                ProfileTopBar(
                    onBackClick = onBackClick,
                    onThemeClick = onThemeClick
                )
            }

            // Header Card
            item {
                ProfileHeaderCard(
                    fullName = fullName,
                    academy = academy,
                    role = role,
                    teams = teams,
                    initials = initials,
                    onEditPhotoClick = onEditPhotoClick
                )
            }

            // Account Info Card
            item {
                AccountInfoCard(
                    fullName = fullName,
                    email = email,
                    role = role,
                    academy = academy
                )
            }

            // Settings Options: Notificaciones / Privacidad
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SettingsOptionItem(
                        icon = Icons.Default.Notifications,
                        title = "Notificaciones",
                        subtitle = "Convocatorias, partidos, entrenamientos",
                        onClick = { activeBottomSheetSection = "Notificaciones" }
                    )
                    SettingsOptionItem(
                        icon = Icons.Default.Lock,
                        title = "Privacidad",
                        subtitle = "Controla quién ve tus datos y perfil",
                        onClick = { activeBottomSheetSection = "Privacidad" }
                    )
                }
            }

            // Sección Cuenta: Botón Cerrar Sesión
            item {
                Column(
                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                ) {
                    Text(
                        text = "CUENTA",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportProTextMuted,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { showLogoutConfirmDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = SportProError,
                            containerColor = SportProError.copy(alpha = 0.08f)
                        ),
                        border = BorderStroke(1.dp, SportProError.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = SportProError,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Cerrar sesión",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportProError
                        )
                    }
                }
            }
        }

        // Confirmation Dialog for Logout
        if (showLogoutConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutConfirmDialog = false },
                containerColor = SportProCardBackground,
                shape = RoundedCornerShape(20.dp),
                title = {
                    Text(
                        text = "¿Cerrar sesión?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportProTextPrimary
                    )
                },
                text = {
                    Text(
                        text = "¿Estás seguro de que deseas cerrar tu sesión?",
                        fontSize = 14.sp,
                        color = SportProTextSecondary,
                        lineHeight = 20.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutConfirmDialog = false
                            onLogoutClick()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SportProError,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Cerrar sesión",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showLogoutConfirmDialog = false },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, SportProCardBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SportProTextPrimary)
                    ) {
                        Text("Cancelar", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        // BottomSheet for Notificaciones / Privacidad
        activeBottomSheetSection?.let { section ->
            ModalBottomSheet(
                onDismissRequest = { activeBottomSheetSection = null },
                containerColor = SportProCardBackground,
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = section,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportProTextPrimary
                        )
                        IconButton(onClick = { activeBottomSheetSection = null }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = SportProTextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (section == "Notificaciones") {
                        NotificationSettingsContent()
                    } else {
                        PrivacySettingsContent()
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { activeBottomSheetSection = null },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SportProGreen,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Guardar preferencias", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun ProfileTopBar(
    onBackClick: () -> Unit,
    onThemeClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = SportProTextPrimary
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Mi perfil",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SportProTextPrimary
            )
        }

        IconButton(onClick = onThemeClick) {
            Icon(
                imageVector = Icons.Filled.LightMode,
                contentDescription = "Cambiar tema",
                tint = SportProTextPrimary
            )
        }
    }
}

@Composable
private fun ProfileHeaderCard(
    fullName: String,
    academy: String,
    role: String,
    teams: String,
    initials: String,
    onEditPhotoClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, SportProCardBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = SportProCardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar circular + lápiz de edición verde neón
            Box(
                modifier = Modifier.size(92.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                        .background(SportProGreenContainer)
                        .border(2.dp, SportProGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = SportProGreen,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 2.dp, y = 2.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(SportProDarkBackground)
                        .border(1.dp, SportProCardBorder, CircleShape)
                        .clickable { onEditPhotoClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar fotografía",
                        tint = SportProGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = fullName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SportProTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Chip "Entrenador / DT"
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SportProGreenContainer)
                    .padding(horizontal = 14.dp, vertical = 5.dp)
            ) {
                Text(
                    text = role,
                    color = SportProGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = academy,
                fontSize = 13.sp,
                color = SportProTextSecondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = null,
                    tint = SportProGreen,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Equipos: $teams",
                    fontSize = 12.sp,
                    color = SportProGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun AccountInfoCard(
    fullName: String,
    email: String,
    role: String,
    academy: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, SportProCardBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = SportProCardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Información de cuenta",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = SportProTextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            AccountRow(label = "Nombre", value = fullName)
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                thickness = 0.5.dp,
                color = SportProCardBorder
            )
            AccountRow(label = "Correo", value = email)
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                thickness = 0.5.dp,
                color = SportProCardBorder
            )
            AccountRow(label = "Rol", value = role)
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                thickness = 0.5.dp,
                color = SportProCardBorder
            )
            AccountRow(label = "Academia", value = academy)
        }
    }
}

@Composable
private fun AccountRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = SportProTextMuted
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = SportProTextPrimary
        )
    }
}

@Composable
private fun SettingsOptionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, SportProCardBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SportProCardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SportProWarning.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SportProWarning,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportProTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = SportProTextSecondary
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = SportProTextMuted,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun NotificationSettingsContent() {
    var matchAlerts by remember { mutableStateOf(true) }
    var trainingAlerts by remember { mutableStateOf(true) }
    var communityAlerts by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SwitchPreferenceRow(
            title = "Alertas de Partidos",
            subtitle = "Notificaciones de inicio, cambios y marcador",
            checked = matchAlerts,
            onCheckedChange = { matchAlerts = it }
        )
        HorizontalDivider(thickness = 0.5.dp, color = SportProCardBorder)
        SwitchPreferenceRow(
            title = "Citaciones a Entrenamiento",
            subtitle = "Recordatorios de sesiones y convocatorias",
            checked = trainingAlerts,
            onCheckedChange = { trainingAlerts = it }
        )
        HorizontalDivider(thickness = 0.5.dp, color = SportProCardBorder)
        SwitchPreferenceRow(
            title = "Novedades en Comunidad",
            subtitle = "Comentarios y me gusta en tus publicaciones",
            checked = communityAlerts,
            onCheckedChange = { communityAlerts = it }
        )
    }
}

@Composable
private fun PrivacySettingsContent() {
    var profilePublic by remember { mutableStateOf(true) }
    var showEmail by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SwitchPreferenceRow(
            title = "Perfil visible en Comunidad",
            subtitle = "Permite que apoderados y jugadores vean tu perfil de DT",
            checked = profilePublic,
            onCheckedChange = { profilePublic = it }
        )
        HorizontalDivider(thickness = 0.5.dp, color = SportProCardBorder)
        SwitchPreferenceRow(
            title = "Mostrar correo de contacto",
            subtitle = "Visible para los miembros autorizados del club",
            checked = showEmail,
            onCheckedChange = { showEmail = it }
        )
    }
}

@Composable
private fun SwitchPreferenceRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SportProTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = SportProTextSecondary
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = SportProGreen,
                uncheckedThumbColor = SportProTextMuted,
                uncheckedTrackColor = SportProDarkBackground
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1117)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen(
        fullName = "Carlos Ramírez",
        email = "entrenador@sportpro.com",
        role = "Entrenador / DT",
        academy = "Academia SportPro",
        initials = "CR"
    )
}
