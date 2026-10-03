package devs.grupo5.sportpro.presentation.trainer.matches

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import devs.grupo5.sportpro.data.model.Match
import devs.grupo5.sportpro.presentation.trainer.teams.CategoryChip
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBorder
import devs.grupo5.sportpro.presentation.trainer.theme.SportProDarkBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProGreen
import devs.grupo5.sportpro.presentation.trainer.theme.SportProGreenContainer
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextMuted
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextPrimary
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextSecondary
import devs.grupo5.sportpro.presentation.trainer.theme.SportProWarning

@Composable
fun MatchesScreen(
    viewModel: MatchViewModel = viewModel(),
    onCreateMatchClick: () -> Unit = {}
) {
    val matches by viewModel.matches.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()

    val statuses = listOf("Todos", "Programado", "En vivo", "Finalizado")

    val filteredMatches = matches.filter {
        selectedStatus == "Todos" || it.status.equals(selectedStatus, ignoreCase = true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SportProDarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Partidos",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportProTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Calendario y resultados del club",
                        fontSize = 14.sp,
                        color = SportProTextSecondary
                    )
                }

                FloatingActionButton(
                    onClick = onCreateMatchClick,
                    containerColor = SportProGreen,
                    contentColor = Color.Black,
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Nuevo partido")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                statuses.forEach { status ->
                    val isSelected = selectedStatus == status
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) SportProGreen else SportProCardBackground)
                            .border(
                                1.dp,
                                if (isSelected) SportProGreen else SportProCardBorder,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { viewModel.setSelectedStatus(status) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = status,
                            color = if (isSelected) Color.Black else SportProTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredMatches.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay partidos registrados.",
                        color = SportProTextMuted,
                        fontSize = 15.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredMatches, key = { it.id }) { match ->
                        MatchCard(match = match)
                    }
                }
            }
        }
    }
}

@Composable
fun MatchCard(match: Match) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, SportProCardBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = SportProCardBackground)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryChip(category = match.category)
                MatchStatusChip(status = match.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Score or VS display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("SportPro", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SportProTextPrimary)
                    Text(if (match.isHome) "Local" else "Visitante", fontSize = 11.sp, color = SportProTextSecondary)
                }

                if (match.homeScore != null && match.awayScore != null) {
                    Text(
                        text = "${match.homeScore} - ${match.awayScore}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SportProGreen
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SportProDarkBackground)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("VS", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SportProGreen)
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(match.rival, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SportProTextPrimary)
                    Text(if (match.isHome) "Visitante" else "Local", fontSize = 11.sp, color = SportProTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        tint = SportProGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${match.date} • ${match.time}", fontSize = 12.sp, color = SportProTextSecondary)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = SportProTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(match.stadium, fontSize = 12.sp, color = SportProTextMuted)
                }
            }
        }
    }
}

@Composable
fun MatchStatusChip(status: String) {
    val (bgColor, textColor) = when (status.lowercase()) {
        "en vivo" -> Pair(SportProWarning.copy(alpha = 0.2f), SportProWarning)
        "programado" -> Pair(SportProGreenContainer, SportProGreen)
        else -> Pair(SportProCardBorder, SportProTextMuted)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
