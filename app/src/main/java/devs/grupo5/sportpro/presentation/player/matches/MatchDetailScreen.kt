package devs.grupo5.sportpro.presentation.player.matches

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.SportsSoccer
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import devs.grupo5.sportpro.presentation.player.components.PlayerBottomNavigationBar
import devs.grupo5.sportpro.presentation.player.components.PlayerTab
import devs.grupo5.sportpro.presentation.player.theme.PlayerBackground
import devs.grupo5.sportpro.presentation.player.theme.PlayerBlue
import devs.grupo5.sportpro.presentation.player.theme.PlayerBlueBorder
import devs.grupo5.sportpro.presentation.player.theme.PlayerBlueLight
import devs.grupo5.sportpro.presentation.player.theme.PlayerBorder
import devs.grupo5.sportpro.presentation.player.theme.PlayerGreenContainer
import devs.grupo5.sportpro.presentation.player.theme.PlayerNeonGreen
import devs.grupo5.sportpro.presentation.player.theme.PlayerSurface
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextMuted
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextPrimary
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextSecondary
import devs.grupo5.sportpro.presentation.player.theme.PlayerWarning
import devs.grupo5.sportpro.presentation.player.theme.SportProPlayerTheme

private data class TimelineEvent(
    val id: String,
    val minute: String,
    val type: EventType,
    val player: String,
    val subtitle: String,
    val tags: List<EventTag>
)

private enum class EventType { GOAL, YELLOW_CARD }

private data class EventTag(val label: String, val kind: TagKind)

private enum class TagKind { LOCAL, AWAY, PENDING }

private val timelineEvents = listOf(
    TimelineEvent(
        id = "e1",
        minute = "12'",
        type = EventType.GOAL,
        player = "Carlos Mendoza",
        subtitle = "Coach Ramírez",
        tags = listOf(EventTag("Local", TagKind.LOCAL))
    ),
    TimelineEvent(
        id = "e2",
        minute = "28'",
        type = EventType.YELLOW_CARD,
        player = "J. Perez (Rival)",
        subtitle = "Coach Ramírez",
        tags = listOf(EventTag("Vis.", TagKind.AWAY))
    ),
    TimelineEvent(
        id = "e3",
        minute = "35'",
        type = EventType.GOAL,
        player = "Desconocido",
        subtitle = "Coach Ramírez",
        tags = listOf(EventTag("Vis.", TagKind.AWAY), EventTag("Pendiente", TagKind.PENDING))
    ),
    TimelineEvent(
        id = "e4",
        minute = "67'",
        type = EventType.GOAL,
        player = "Diego Herrera",
        subtitle = "Coach Ramírez",
        tags = listOf(EventTag("Local", TagKind.LOCAL))
    )
)

@Composable
fun MatchDetailScreen(
    matchId: String = "",
    onBackClick: () -> Unit = {},
    onTabSelected: (PlayerTab) -> Unit = {}
) {
    val match = sampleMatches.find { it.id == matchId } ?: sampleMatches.first()

    SportProPlayerTheme {
        Scaffold(
            containerColor = PlayerBackground,
            bottomBar = {
                PlayerBottomNavigationBar(
                    currentTab = PlayerTab.MATCHES,
                    onTabSelected = onTabSelected
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    MatchDetailTopBar(onBackClick = onBackClick)
                }

                item {
                    ScoreboardCard(match = match)
                }

                item {
                    TimelineHeader(onSeeAllClick = {})
                }

                items(timelineEvents, key = { it.id }) { event ->
                    TimelineEventRow(event = event)
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun MatchDetailTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
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
            text = "Partido En Vivo",
            modifier = Modifier.weight(1f),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = PlayerTextPrimary
        )
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = "Acta del partido",
                tint = PlayerTextPrimary
            )
        }
    }
}

@Composable
private fun ScoreboardCard(match: PlayerMatch) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, PlayerNeonGreen.copy(alpha = 0.35f), RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = PlayerGreenContainer)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(PlayerNeonGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "EN VIVO",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PlayerNeonGreen
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "Pausar",
                        tint = PlayerTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "01:14",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = PlayerTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamColumn(
                    name = match.leftTeam,
                    role = match.leftRole,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = if (match.leftScore != null && match.rightScore != null) {
                        "${match.leftScore} - ${match.rightScore}"
                    } else {
                        "VS"
                    },
                    modifier = Modifier.padding(horizontal = 10.dp),
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PlayerNeonGreen
                )

                TeamColumn(
                    name = match.rightTeam,
                    role = match.rightRole,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Min 74' · ${match.category} · ${match.location}",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 12.sp,
                color = PlayerTextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TeamColumn(
    name: String,
    role: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = name,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = PlayerTextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "($role)",
            fontSize = 11.sp,
            color = PlayerTextSecondary
        )
    }
}

@Composable
private fun TimelineHeader(onSeeAllClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Cronología reciente",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = PlayerTextPrimary
        )
        Text(
            text = "Ver todo",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = PlayerNeonGreen,
            modifier = Modifier.clickable { onSeeAllClick() }
        )
    }
}

@Composable
private fun TimelineEventRow(event: TimelineEvent) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, PlayerBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = PlayerSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = event.minute,
                modifier = Modifier.width(34.dp),
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PlayerNeonGreen
            )

            EventIcon(type = event.type)

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.player,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlayerTextPrimary
                )
                Text(
                    text = event.subtitle,
                    fontSize = 11.sp,
                    color = PlayerTextMuted
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                event.tags.forEach { tag ->
                    EventTagBadge(tag = tag)
                }
            }
        }
    }
}

@Composable
private fun EventIcon(type: EventType) {
    when (type) {
        EventType.GOAL -> {
            Icon(
                imageVector = Icons.Default.SportsSoccer,
                contentDescription = "Gol",
                tint = PlayerTextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        EventType.YELLOW_CARD -> {
            Box(
                modifier = Modifier
                    .size(width = 14.dp, height = 19.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(PlayerWarning)
            )
        }
    }
}

@Composable
private fun EventTagBadge(tag: EventTag) {
    val (background, border, textColor) = when (tag.kind) {
        TagKind.LOCAL -> Triple(
            PlayerGreenContainer,
            PlayerNeonGreen.copy(alpha = 0.5f),
            PlayerNeonGreen
        )

        TagKind.AWAY -> Triple(
            PlayerBlue,
            PlayerBlueBorder,
            PlayerBlueLight
        )

        TagKind.PENDING -> Triple(
            PlayerWarning.copy(alpha = 0.16f),
            PlayerWarning.copy(alpha = 0.5f),
            PlayerWarning
        )
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .border(1.dp, border, RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = tag.label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
