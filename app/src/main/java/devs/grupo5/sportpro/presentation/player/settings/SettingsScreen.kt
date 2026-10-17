package devs.grupo5.sportpro.presentation.player.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import devs.grupo5.sportpro.presentation.player.components.PlayerBottomNavigationBar
import devs.grupo5.sportpro.presentation.player.components.PlayerTab
import devs.grupo5.sportpro.presentation.player.theme.PlayerBackground
import devs.grupo5.sportpro.presentation.player.theme.PlayerBorder
import devs.grupo5.sportpro.presentation.player.theme.PlayerError
import devs.grupo5.sportpro.presentation.player.theme.PlayerNeonGreen
import devs.grupo5.sportpro.presentation.player.theme.PlayerSurface
import devs.grupo5.sportpro.presentation.player.theme.PlayerSurfaceVariant
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextMuted
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextPrimary
import devs.grupo5.sportpro.presentation.player.theme.SportProPlayerTheme

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onEditPersonalInfoClick: () -> Unit = {},
    onDeleteAccountClick: () -> Unit = {},
    onTabSelected: (PlayerTab) -> Unit = {}
) {
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
                    SettingsTopBar(onBackClick = onBackClick)
                }

                item {
                    SectionTitle(text = "Notificaciones")
                }
                item {
                    NotificationSettingsBlock()
                }

                item {
                    SectionTitle(text = "Apariencia")
                }
                item {
                    AppearanceBlock()
                }

                item {
                    SectionTitle(text = "Cuenta")
                }
                item {
                    AccountBlock(
                        onChangePasswordClick = onChangePasswordClick,
                        onEditPersonalInfoClick = onEditPersonalInfoClick,
                        onDeleteAccountClick = onDeleteAccountClick
                    )
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun SettingsTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = PlayerTextPrimary
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Ajustes",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = PlayerTextPrimary
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = PlayerTextMuted
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, PlayerBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = PlayerSurface)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            content()
        }
    }
}

@Composable
private fun NotificationSettingsBlock() {
    var matchCallUps by remember { mutableStateOf(true) }
    var trainings by remember { mutableStateOf(true) }
    var scheduleChanges by remember { mutableStateOf(true) }
    var communityPosts by remember { mutableStateOf(false) }

    SettingsCard {
        SwitchRow(
            title = "Convocatorias de partido",
            checked = matchCallUps,
            onCheckedChange = { matchCallUps = it }
        )
        RowDivider()
        SwitchRow(
            title = "Entrenamientos",
            checked = trainings,
            onCheckedChange = { trainings = it }
        )
        RowDivider()
        SwitchRow(
            title = "Cambios de horario",
            checked = scheduleChanges,
            onCheckedChange = { scheduleChanges = it }
        )
        RowDivider()
        SwitchRow(
            title = "Publicaciones comunidad",
            checked = communityPosts,
            onCheckedChange = { communityPosts = it }
        )
    }
}

@Composable
private fun AppearanceBlock() {
    var darkTheme by remember { mutableStateOf(true) }

    SettingsCard {
        SwitchRow(
            title = "Tema oscuro",
            checked = darkTheme,
            onCheckedChange = { darkTheme = it }
        )
    }
}

@Composable
private fun AccountBlock(
    onChangePasswordClick: () -> Unit,
    onEditPersonalInfoClick: () -> Unit,
    onDeleteAccountClick: () -> Unit
) {
    SettingsCard {
        ClickableRow(
            title = "Cambiar contraseña",
            color = PlayerTextPrimary,
            onClick = onChangePasswordClick
        )
        RowDivider()
        ClickableRow(
            title = "Editar información personal",
            color = PlayerTextPrimary,
            onClick = onEditPersonalInfoClick
        )
        RowDivider()
        ClickableRow(
            title = "Eliminar cuenta",
            color = PlayerError,
            onClick = onDeleteAccountClick
        )
    }
}

@Composable
private fun SwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = PlayerTextPrimary,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF00210B),
                checkedTrackColor = PlayerNeonGreen,
                uncheckedThumbColor = PlayerTextMuted,
                uncheckedTrackColor = PlayerSurfaceVariant
            )
        )
    }
}

@Composable
private fun ClickableRow(
    title: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = color,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = PlayerTextMuted,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 12.dp),
        thickness = 0.5.dp,
        color = PlayerBorder
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF101413)
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen()
}
