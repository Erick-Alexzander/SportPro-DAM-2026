package devs.grupo5.sportpro.presentation.player.notifications

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import devs.grupo5.sportpro.presentation.player.components.PlayerBottomNavigationBar
import devs.grupo5.sportpro.presentation.player.components.PlayerTab
import devs.grupo5.sportpro.presentation.player.theme.PlayerBackground
import devs.grupo5.sportpro.presentation.player.theme.PlayerBorder
import devs.grupo5.sportpro.presentation.player.theme.PlayerNeonGreen
import devs.grupo5.sportpro.presentation.player.theme.PlayerSurface
import devs.grupo5.sportpro.presentation.player.theme.PlayerSurfaceVariant
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextMuted
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextPrimary
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextSecondary
import devs.grupo5.sportpro.presentation.player.theme.SportProPlayerTheme

private data class PlayerNotification(
    val id: String,
    val icon: String,
    val title: String,
    val subtitle: String,
    val time: String,
    val isUnread: Boolean
)

private val notifications = listOf(
    PlayerNotification(
        id = "n1",
        icon = "⚽",
        title = "Convocatoria: vs Deportes Centro",
        subtitle = "Confirma tu disponibilidad antes del viernes",
        time = "Hace 2h",
        isUnread = true
    ),
    PlayerNotification(
        id = "n2",
        icon = "📋",
        title = "Entrenamiento mañana 16:00",
        subtitle = "Sesión de táctica y preparación física",
        time = "Hace 5h",
        isUnread = true
    ),
    PlayerNotification(
        id = "n3",
        icon = "🔄",
        title = "Cambio de horario",
        subtitle = "El entrenamiento del martes pasa a las 17:30",
        time = "Ayer",
        isUnread = false
    ),
    PlayerNotification(
        id = "n4",
        icon = "💰",
        title = "Mensualidad noviembre",
        subtitle = "El pago de noviembre ha sido registrado",
        time = "Hace 2 días",
        isUnread = false
    )
)

@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit = {},
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // TopAppBar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = PlayerTextPrimary
                        )
                    }
                    Text(
                        text = "Notificaciones",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlayerTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notifications, key = { it.id }) { notification ->
                        NotificationCard(notification = notification)
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(notification: PlayerNotification) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, PlayerBorder, RoundedCornerShape(16.dp)),
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
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(PlayerSurfaceVariant)
                    .border(1.dp, PlayerBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = notification.icon,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlayerTextPrimary
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = notification.subtitle,
                    fontSize = 13.sp,
                    color = PlayerTextSecondary,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (notification.isUnread) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(PlayerNeonGreen)
                    )
                }
                Text(
                    text = notification.time,
                    fontSize = 11.sp,
                    color = PlayerTextMuted
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF101413)
@Composable
private fun NotificationsScreenPreview() {
    NotificationsScreen()
}
