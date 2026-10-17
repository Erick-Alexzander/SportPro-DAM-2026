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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import devs.grupo5.sportpro.presentation.player.components.PlayerBottomNavigationBar
import devs.grupo5.sportpro.presentation.player.components.PlayerTab
import devs.grupo5.sportpro.presentation.player.theme.PlayerBackground
import devs.grupo5.sportpro.presentation.player.theme.PlayerBorder
import devs.grupo5.sportpro.presentation.player.theme.PlayerError
import devs.grupo5.sportpro.presentation.player.theme.PlayerGreenContainer
import devs.grupo5.sportpro.presentation.player.theme.PlayerNeonGreen
import devs.grupo5.sportpro.presentation.player.theme.PlayerPink
import devs.grupo5.sportpro.presentation.player.theme.PlayerSurface
import devs.grupo5.sportpro.presentation.player.theme.PlayerSurfaceVariant
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextMuted
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextPrimary
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextSecondary
import devs.grupo5.sportpro.presentation.player.theme.SportProPlayerTheme

private data class PlayerMatch(
    val id: String,
    val status: String, // "En vivo", "Finalizado", "Programado"
    val category: String,
    val date: String,
    val time: String,
    val leftTeam: String,
    val leftRole: String, // "Local", "Visitante"
    val rightTeam: String,
    val rightRole: String, // "Local", "Visitante"
    val leftScore: Int?,
    val rightScore: Int?,
    val location: String
)

private val filterOptions = listOf("Todos", "Programado", "En vivo", "Finalizado")

private val sampleMatches = listOf(
    PlayerMatch(
        id = "m1",
        status = "En vivo",
        category = "Primera",
        date = "2024-11-02",
        time = "15:00",
        leftTeam = "SportPro",
        leftRole = "Local",
        rightTeam = "Club Deportivo Norte",
        rightRole = "Visitante",
        leftScore = 2,
        rightScore = 1,
        location = "Estadio Municipal"
    ),
    PlayerMatch(
        id = "m2",
        status = "Finalizado",
        category = "Primera",
        date = "2024-10-26",
        time = "11:00",
        leftTeam = "SportPro",
        leftRole = "Visitante",
        rightTeam = "Atlético Sur",
        rightRole = "Local",
        leftScore = 1,
        rightScore = 3,
        location = "Cancha Sur"
    ),
    PlayerMatch(
        id = "m3",
        status = "Programado",
        category = "Primera",
        date = "2024-11-09",
        time = "10:00",
        leftTeam = "SportPro",
        leftRole = "Local",
        rightTeam = "Deportes Centro",
        rightRole = "Visitante",
        leftScore = null,
        rightScore = null,
        location = "Estadio Central"
    ),
    PlayerMatch(
        id = "m4",
        status = "Programado",
        category = "Sub-15",
        date = "2024-11-09",
        time = "09:00",
        leftTeam = "SportPro",
        leftRole = "Visitante",
        rightTeam = "Los Tigres",
        rightRole = "Local",
        leftScore = null,
        rightScore = null,
        location = "Cancha Norte"
    )
)

@Composable
fun MatchesScreen(
    onBackClick: () -> Unit = {},
    onTabSelected: (PlayerTab) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("Todos") }

    val filteredMatches = if (selectedFilter == "Todos") {
        sampleMatches
    } else {
        sampleMatches.filter { it.status.equals(selectedFilter, ignoreCase = true) }
    }

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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Top bar
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
                        text = "Partidos",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlayerTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Filter chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filterOptions) { option ->
                        FilterChip(
                            label = option,
                            isSelected = selectedFilter == option,
                            onClick = { selectedFilter = option }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (filteredMatches.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay partidos para este filtro.",
                            color = PlayerTextMuted,
                            fontSize = 15.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(filteredMatches, key = { it.id }) { match ->
                            MatchCard(match = match)
                        }
                        item { Spacer(modifier = Modifier.height(4.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) PlayerNeonGreen else PlayerSurface)
            .border(
                1.dp,
                if (isSelected) PlayerNeonGreen else PlayerBorder,
                RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.Black else PlayerTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MatchCard(match: PlayerMatch) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, PlayerBorder, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = PlayerSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Status / category / date row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(status = match.status)
                    Spacer(modifier = Modifier.width(8.dp))
                    CategoryBadge(category = match.category)
                }
                Text(
                    text = "${match.date} · ${match.time}",
                    fontSize = 11.sp,
                    color = PlayerTextMuted,
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Score / fixture row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamColumn(
                    name = match.leftTeam,
                    role = match.leftRole,
                    modifier = Modifier.weight(1f)
                )

                if (match.leftScore != null && match.rightScore != null) {
                    Text(
                        text = "${match.leftScore} - ${match.rightScore}",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PlayerNeonGreen,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(PlayerBackground)
                            .padding(horizontal = 14.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "VS",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PlayerNeonGreen
                        )
                    }
                }

                TeamColumn(
                    name = match.rightTeam,
                    role = match.rightRole,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Location row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = PlayerPink,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = match.location,
                    fontSize = 12.sp,
                    color = PlayerTextSecondary
                )
            }
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
private fun StatusBadge(status: String) {
    val (bgColor, textColor) = when (status.lowercase()) {
        "en vivo" -> PlayerError.copy(alpha = 0.18f) to PlayerError
        "programado" -> PlayerGreenContainer to PlayerNeonGreen
        "finalizado" -> PlayerSurfaceVariant to PlayerTextSecondary
        else -> PlayerSurfaceVariant to PlayerTextMuted
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (status.equals("En vivo", ignoreCase = true)) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(PlayerNeonGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = status,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CategoryBadge(category: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(PlayerSurfaceVariant)
            .border(1.dp, PlayerBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = category,
            color = PlayerTextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
