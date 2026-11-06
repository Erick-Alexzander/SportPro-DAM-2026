package devs.grupo5.sportpro.presentation.player.profile

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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import devs.grupo5.sportpro.data.model.PlayerAttributes
import devs.grupo5.sportpro.data.model.PlayerProfile
import devs.grupo5.sportpro.data.model.PlayerSeasonStats
import devs.grupo5.sportpro.presentation.player.components.PlayerBottomNavigationBar
import devs.grupo5.sportpro.presentation.player.components.PlayerTab
import devs.grupo5.sportpro.presentation.player.theme.PlayerBackground
import devs.grupo5.sportpro.presentation.player.theme.PlayerBorder
import devs.grupo5.sportpro.presentation.player.theme.PlayerBrown
import devs.grupo5.sportpro.presentation.player.theme.PlayerError
import devs.grupo5.sportpro.presentation.player.theme.PlayerGreenContainer
import devs.grupo5.sportpro.presentation.player.theme.PlayerNeonGreen
import devs.grupo5.sportpro.presentation.player.theme.PlayerSurface
import devs.grupo5.sportpro.presentation.player.theme.PlayerSurfaceVariant
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextMuted
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextPrimary
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextSecondary
import devs.grupo5.sportpro.presentation.player.theme.PlayerWarning
import devs.grupo5.sportpro.presentation.player.theme.SportProPlayerTheme

@Composable
fun ProfileScreen(
    fullName: String = "",
    email: String = "",
    role: String = "Jugador",
    academy: String = "Academia SportPro",
    category: String = "",
    initials: String = "",
    birthDate: String? = null,
    playerProfile: PlayerProfile = PlayerProfile(),
    uid: String = "",
    onBackClick: () -> Unit = {},
    onThemeClick: () -> Unit = {},
    onEditPhotoClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onHelpClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onTabSelected: (PlayerTab) -> Unit = {}
) {
    var showShareDialog by remember { mutableStateOf(false) }

    val displayName = fullName.ifBlank { "—" }
    val displayEmail = email.ifBlank { "—" }
    val effectiveInitials = initials.ifBlank {
        displayName.split(" ")
            .filter { it.isNotBlank() && it != "—" }
            .take(2)
            .map { it.first().uppercase() }
            .joinToString("")
            .ifBlank { "?" }
    }
    val academyLine = listOf(
        academy.takeIf { it.isNotBlank() },
        category.takeIf { it.isNotBlank() }
    ).filterNotNull().joinToString(" · ")

    if (showShareDialog) {
        ShareProfileDialog(
            uid = uid,
            fullName = displayName,
            onDismiss = { showShareDialog = false }
        )
    }

    SportProPlayerTheme {
        Scaffold(
            containerColor = PlayerBackground,
            bottomBar = {
                PlayerBottomNavigationBar(
                    currentTab = PlayerTab.PROFILE,
                    onTabSelected = onTabSelected
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    ProfileTopBar(
                        onBackClick = onBackClick,
                        onShareClick = { showShareDialog = true },
                        onThemeClick = onThemeClick
                    )
                }

                item {
                    ProfileHeaderCard(
                        fullName = displayName,
                        role = role,
                        academyLine = academyLine,
                        initials = effectiveInitials,
                        status = playerProfile.status,
                        playerProfile = playerProfile,
                        onEditPhotoClick = onEditPhotoClick
                    )
                }

                item {
                    if (playerProfile.hasSportData) {
                        SportDataCard(playerProfile = playerProfile)
                    } else {
                        EmptySportDataCard(onEditProfileClick = onEditProfileClick)
                    }
                }

                item {
                    SeasonStatsCard(stats = playerProfile.stats)
                }

                item {
                    AttributesCard(
                        attributes = playerProfile.attributes,
                        onCompleteClick = onEditProfileClick
                    )
                }

                item {
                    AccountInfoCard(
                        fullName = displayName,
                        email = displayEmail,
                        role = role,
                        academyLine = academyLine,
                        birthDate = birthDate
                    )
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        SettingsOptionItem(
                            icon = Icons.Default.Edit,
                            title = "Editar perfil",
                            subtitle = "Datos personales, deportivos y atributos",
                            onClick = onEditProfileClick
                        )
                        SettingsOptionItem(
                            icon = Icons.Default.Share,
                            title = "Compartir perfil",
                            subtitle = "Envía tu perfil o muestra tu código QR",
                            onClick = { showShareDialog = true }
                        )
                        SettingsOptionItem(
                            icon = Icons.Default.Notifications,
                            title = "Notificaciones",
                            subtitle = "Convocatorias, partidos, entrenam.",
                            onClick = onNotificationsClick
                        )
                        SettingsOptionItem(
                            icon = Icons.Default.Lock,
                            title = "Privacidad",
                            subtitle = "Controla quién ve tus datos",
                            onClick = onPrivacyClick
                        )
                        SettingsOptionItem(
                            icon = Icons.Default.Settings,
                            title = "Ajustes",
                            subtitle = "Preferencias de la app",
                            onClick = onSettingsClick
                        )
                        SettingsOptionItem(
                            icon = Icons.AutoMirrored.Filled.HelpOutline,
                            title = "Ayuda y soporte",
                            subtitle = "Preguntas frecuentes",
                            onClick = onHelpClick
                        )
                    }
                }

                item {
                    OutlinedButton(
                        onClick = onLogoutClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 24.dp)
                            .height(52.dp),
                        shape = RoundedCornerShape(percent = 50),
                        border = BorderStroke(1.dp, PlayerError.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = PlayerError,
                            containerColor = PlayerError.copy(alpha = 0.08f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = PlayerError,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Cerrar sesión",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PlayerError
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileTopBar(
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
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
                    tint = PlayerTextPrimary
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Mi perfil",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PlayerTextPrimary
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onShareClick) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Compartir perfil",
                    tint = PlayerTextPrimary
                )
            }
            IconButton(onClick = onThemeClick) {
                Icon(
                    imageVector = Icons.Filled.LightMode,
                    contentDescription = "Cambiar tema",
                    tint = PlayerTextPrimary
                )
            }
        }
    }
}

@Composable
private fun ProfileHeaderCard(
    fullName: String,
    role: String,
    academyLine: String,
    initials: String,
    status: String,
    playerProfile: PlayerProfile,
    onEditPhotoClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, PlayerBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = PlayerSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(92.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                        .background(PlayerBrown)
                        .border(2.dp, PlayerBrown, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                if (playerProfile.jerseyNumber > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 2.dp, y = (-2).dp)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(PlayerNeonGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${playerProfile.jerseyNumber}",
                            color = Color(0xFF00210B),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 2.dp, y = 2.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(PlayerBackground)
                        .border(1.dp, PlayerBorder, CircleShape)
                        .clickable { onEditPhotoClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar fotografía",
                        tint = PlayerNeonGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = fullName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PlayerTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(PlayerGreenContainer)
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = role,
                        color = PlayerNeonGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                StatusBadge(status = status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (academyLine.isNotBlank()) {
                Text(
                    text = academyLine,
                    fontSize = 13.sp,
                    color = PlayerTextSecondary
                )
            }

            if (playerProfile.hasSportData) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(thickness = 0.5.dp, color = PlayerBorder)
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    HeaderMetric(label = "Posición", value = playerProfile.positionLabel)
                    HeaderMetric(label = "Dorsal", value = playerProfile.jerseyLabel)
                    HeaderMetric(
                        label = "Categoría",
                        value = playerProfile.category.ifBlank { "—" }
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderMetric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = PlayerNeonGreen
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = PlayerTextMuted
        )
    }
}

@Composable
private fun StatusBadge(status: String) {
    val color = when (status.lowercase()) {
        "lesionado" -> PlayerWarning
        "suspendido" -> PlayerError
        else -> PlayerNeonGreen
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.14f))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(
            text = status.ifBlank { "Activo" },
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SportDataCard(playerProfile: PlayerProfile) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, PlayerBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = PlayerSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Perfil deportivo",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PlayerTextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            AccountRow(
                label = "Equipo",
                value = playerProfile.teamName.ifBlank { "—" }
            )
            DividerRow()
            AccountRow(label = "Posición", value = playerProfile.positionLabel)
            DividerRow()
            AccountRow(
                label = "Pie dominante",
                value = playerProfile.dominantFoot.ifBlank { "—" }
            )
            DividerRow()
            AccountRow(
                label = "Altura",
                value = playerProfile.heightLabel
            )
            DividerRow()
            AccountRow(
                label = "Peso",
                value = playerProfile.weightLabel
            )
            DividerRow()
            AccountRow(
                label = "Talla",
                value = playerProfile.size.ifBlank { "—" }
            )
            if (playerProfile.bmi > 0.0) {
                DividerRow()
                AccountRow(
                    label = "IMC",
                    value = String.format("%.1f", playerProfile.bmi)
                )
            }
        }
    }
}

@Composable
private fun EmptySportDataCard(onEditProfileClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, PlayerBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = PlayerSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Completa tu perfil deportivo",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PlayerTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Agrega tu posición, dorsal, categoría y datos físicos para que tu perfil destaque.",
                fontSize = 13.sp,
                color = PlayerTextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onEditProfileClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, PlayerNeonGreen.copy(alpha = 0.6f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PlayerNeonGreen,
                    containerColor = PlayerGreenContainer
                )
            ) {
                Text(text = "Editar perfil", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SeasonStatsCard(stats: PlayerSeasonStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, PlayerBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = PlayerSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Estadísticas de temporada",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PlayerTextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatTile(value = stats.matchesPlayed.toString(), label = "Partidos")
                StatTile(value = stats.goals.toString(), label = "Goles")
                StatTile(value = stats.assists.toString(), label = "Asistencias")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatTile(
                    value = stats.yellowCards.toString(),
                    label = "Amarillas",
                    valueColor = PlayerWarning
                )
                StatTile(
                    value = stats.redCards.toString(),
                    label = "Rojas",
                    valueColor = PlayerError
                )
            }
        }
    }
}

@Composable
private fun StatTile(
    value: String,
    label: String,
    valueColor: Color = PlayerNeonGreen
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = valueColor
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = PlayerTextSecondary
        )
    }
}

@Composable
private fun AttributesCard(
    attributes: PlayerAttributes,
    onCompleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, PlayerBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = PlayerSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Atributos",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlayerTextPrimary
                )
                if (attributes.hasData) {
                    Text(
                        text = "Media ${attributes.overall}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlayerNeonGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (attributes.hasData) {
                AttributeBar("Velocidad", attributes.speed)
                AttributeBar("Tiro", attributes.shooting)
                AttributeBar("Pase", attributes.passing)
                AttributeBar("Defensa", attributes.defense)
                AttributeBar("Físico", attributes.physical)
            } else {
                Text(
                    text = "Aún no has definido tus atributos.",
                    fontSize = 13.sp,
                    color = PlayerTextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onCompleteClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, PlayerNeonGreen.copy(alpha = 0.6f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PlayerNeonGreen,
                        containerColor = PlayerGreenContainer
                    )
                ) {
                    Text(text = "Definir atributos", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AttributeBar(label: String, value: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 12.sp, color = PlayerTextSecondary)
            Text(
                text = "$value",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PlayerTextPrimary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { value.coerceIn(0, 100) / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = PlayerNeonGreen,
            trackColor = PlayerSurfaceVariant
        )
    }
}

@Composable
private fun AccountInfoCard(
    fullName: String,
    email: String,
    role: String,
    academyLine: String,
    birthDate: String? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, PlayerBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = PlayerSurface)
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
                color = PlayerTextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            AccountRow(label = "Nombre", value = fullName)
            DividerRow()
            AccountRow(label = "Correo", value = email)
            DividerRow()
            AccountRow(label = "Rol", value = role)
            if (!birthDate.isNullOrBlank()) {
                DividerRow()
                AccountRow(label = "Nacimiento", value = birthDate)
            }
            if (academyLine.isNotBlank()) {
                DividerRow()
                AccountRow(label = "Academia", value = academyLine)
            }
        }
    }
}

@Composable
private fun DividerRow() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 10.dp),
        thickness = 0.5.dp,
        color = PlayerBorder
    )
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
            color = PlayerTextMuted
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = PlayerTextPrimary
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
            .border(1.dp, PlayerBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = PlayerSurface)
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
                    .background(PlayerGreenContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PlayerNeonGreen,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlayerTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = PlayerTextSecondary
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = PlayerTextMuted,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
