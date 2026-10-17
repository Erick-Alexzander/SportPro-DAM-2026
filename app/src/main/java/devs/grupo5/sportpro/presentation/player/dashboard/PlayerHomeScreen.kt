package devs.grupo5.sportpro.presentation.player.dashboard

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import devs.grupo5.sportpro.presentation.player.components.PlayerBottomNavigationBar
import devs.grupo5.sportpro.presentation.player.components.PlayerTab
import devs.grupo5.sportpro.presentation.player.theme.PlayerBackground
import devs.grupo5.sportpro.presentation.player.theme.PlayerBlue
import devs.grupo5.sportpro.presentation.player.theme.PlayerBlueBorder
import devs.grupo5.sportpro.presentation.player.theme.PlayerBlueLight
import devs.grupo5.sportpro.presentation.player.theme.PlayerBorder
import devs.grupo5.sportpro.presentation.player.theme.PlayerBrown
import devs.grupo5.sportpro.presentation.player.theme.PlayerGreenContainer
import devs.grupo5.sportpro.presentation.player.theme.PlayerNeonGreen
import devs.grupo5.sportpro.presentation.player.theme.PlayerSurface
import devs.grupo5.sportpro.presentation.player.theme.PlayerSurfaceVariant
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextMuted
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextPrimary
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextSecondary
import devs.grupo5.sportpro.presentation.player.theme.SportProPlayerTheme

private data class PlayerNews(
    val author: String,
    val time: String,
    val content: String
)

private val sampleNews = listOf(
    PlayerNews(
        author = "Academia SportPro",
        time = "Hace 2h",
        content = "Nueva jornada de entrenamiento confirmada para el primer equipo este miércoles a las 18:00."
    ),
    PlayerNews(
        author = "Coach Ramírez",
        time = "Hace 5h",
        content = "Recuerden llegar 30 minutos antes al partido del sábado para el calentamiento previo."
    )
)

@Composable
fun PlayerHomeScreen(
    playerName: String = "Jugador",
    category: String = "Primera",
    jerseyNumber: Int = 9,
    position: String = "DEL",
    onLogoutClick: () -> Unit = {},
    onConvocatoriaClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onCommunityClick: () -> Unit = {},
    onTabSelected: (PlayerTab) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(PlayerTab.HOME) }

    SportProPlayerTheme {
        Scaffold(
            containerColor = PlayerBackground,
            bottomBar = {
                PlayerBottomNavigationBar(
                    currentTab = selectedTab,
                    onTabSelected = { tab ->
                        selectedTab = tab
                        onTabSelected(tab)
                    }
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item {
                    PlayerHeader(
                        playerName = playerName,
                        category = category,
                        jerseyNumber = jerseyNumber,
                        position = position,
                        onLogoutClick = onLogoutClick
                    )
                }

                item {
                    Text(
                        text = "Mi temporada",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlayerTextPrimary
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SeasonStatCard(value = "12", label = "Goles", modifier = Modifier.weight(1f))
                        SeasonStatCard(value = "5", label = "Asist.", modifier = Modifier.weight(1f))
                        SeasonStatCard(value = "18", label = "PJ", modifier = Modifier.weight(1f))
                        SeasonStatCard(value = "92%", label = "Asist.%", modifier = Modifier.weight(1f))
                    }
                }

                item {
                    CallUpCard(onClick = onConvocatoriaClick)
                }

                item {
                    QuickAccessGrid(
                        onMatchesClick = onMatchesClick,
                        onCalendarClick = onCalendarClick,
                        onProfileClick = onProfileClick,
                        onCommunityClick = onCommunityClick
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Noticias",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PlayerTextPrimary
                        )
                        Text(
                            text = "Ver más",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PlayerNeonGreen,
                            modifier = Modifier.clickable { }
                        )
                    }
                }

                items(sampleNews) { news ->
                    NewsCard(news = news)
                }
            }
        }
    }
}

@Composable
private fun PlayerHeader(
    playerName: String,
    category: String,
    jerseyNumber: Int,
    position: String,
    onLogoutClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(PlayerBrown),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initialsOf(playerName),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playerName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PlayerTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = category, fontSize = 13.sp, color = PlayerTextSecondary)
                Text(text = " · ", fontSize = 13.sp, color = PlayerTextMuted)
                Text(
                    text = "#$jerseyNumber",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlayerNeonGreen
                )
                Text(text = " · ", fontSize = 13.sp, color = PlayerTextMuted)
                Text(
                    text = position,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlayerNeonGreen
                )
            }
        }

        IconButton(onClick = onLogoutClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Cerrar sesión",
                tint = PlayerTextMuted
            )
        }
    }
}

@Composable
private fun SeasonStatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, PlayerBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = PlayerSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PlayerNeonGreen
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                color = PlayerTextSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun CallUpCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.5.dp, PlayerBlueBorder, RoundedCornerShape(18.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = PlayerBlue)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(PlayerBlueLight)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "CONVOCATORIA",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlayerBlueLight
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "vs. Deportes Centro · 2024-11-09",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PlayerTextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "¿Estás disponible? Confirma tu asistencia",
                fontSize = 13.sp,
                color = PlayerTextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PlayerNeonGreen,
                        contentColor = Color(0xFF00210B)
                    )
                ) {
                    Text(text = "✓ Disponible", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PlayerSurfaceVariant,
                        contentColor = PlayerTextPrimary
                    )
                ) {
                    Text(text = "✗ No disponible", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun QuickAccessGrid(
    onMatchesClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onProfileClick: () -> Unit,
    onCommunityClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickAccessCard(
                title = "Mis partidos",
                icon = Icons.Default.SportsSoccer,
                onClick = onMatchesClick,
                modifier = Modifier.weight(1f)
            )
            QuickAccessCard(
                title = "Calendario",
                icon = Icons.Default.CalendarMonth,
                onClick = onCalendarClick,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickAccessCard(
                title = "Mi perfil",
                icon = Icons.Default.Person,
                onClick = onProfileClick,
                modifier = Modifier.weight(1f)
            )
            QuickAccessCard(
                title = "Comunidad",
                icon = Icons.Default.Forum,
                onClick = onCommunityClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickAccessCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, PlayerBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = PlayerSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
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
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PlayerTextPrimary
            )
        }
    }
}

@Composable
private fun NewsCard(news: PlayerNews) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, PlayerBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = PlayerSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(PlayerGreenContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = news.author.take(1).uppercase(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlayerNeonGreen
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = news.author,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlayerTextPrimary
                    )
                    Text(
                        text = news.time,
                        fontSize = 12.sp,
                        color = PlayerTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = news.content,
                fontSize = 13.sp,
                color = PlayerTextPrimary,
                lineHeight = 19.sp
            )
        }
    }
}

private fun initialsOf(name: String): String {
    val parts = name.trim().split(" ").filter { it.isNotBlank() }
    return when {
        parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}".uppercase()
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> "?"
    }
}
