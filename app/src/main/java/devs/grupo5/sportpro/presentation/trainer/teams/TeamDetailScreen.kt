package devs.grupo5.sportpro.presentation.trainer.teams

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBorder
import devs.grupo5.sportpro.presentation.trainer.theme.SportProDarkBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProError
import devs.grupo5.sportpro.presentation.trainer.theme.SportProGreen
import devs.grupo5.sportpro.presentation.trainer.theme.SportProGreenContainer
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextMuted
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextPrimary
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextSecondary

@Composable
fun TeamDetailScreen(
    teamId: String,
    viewModel: TeamViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onRegisterNewPlayerClick: (teamId: String) -> Unit = {}
) {
    val teams by viewModel.teams.collectAsState()
    val availablePlayers by viewModel.availablePlayers.collectAsState()

    val team = teams.find { it.id == teamId } ?: viewModel.getTeamById(teamId)

    var selectedTabIndex by remember { mutableStateOf(0) }
    var showAddPlayerDialog by remember { mutableStateOf(false) }

    val tabs = listOf("Jugadores", "Entrenamientos", "Partidos")

    if (team == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SportProDarkBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("Equipo no encontrado.", color = SportProTextMuted)
        }
        return
    }

    val teamPlayers = viewModel.getTeamPlayers(team)
    val teamTrainings = viewModel.getTeamTrainings(team.id)
    val teamMatches = viewModel.getTeamMatches(team.id)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SportProDarkBackground)
            .padding(16.dp)
    ) {
        // Top bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = SportProTextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = team.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = SportProTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Team Overview Card
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
                    CategoryChip(category = team.category)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            tint = SportProGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${team.playerCount} Jugadores",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportProGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = team.description,
                    fontSize = 13.sp,
                    color = SportProTextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = SportProTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Entrenador: ${team.coachName}",
                        fontSize = 13.sp,
                        color = SportProTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = SportProCardBackground,
            contentColor = SportProGreen,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = SportProGreen
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == index) SportProGreen else SportProTextSecondary
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTabIndex) {
            0 -> {
                // Jugadores Tab
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Plantilla del equipo",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportProTextPrimary
                    )

                    Button(
                        onClick = { showAddPlayerDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SportProGreen, contentColor = Color.Black),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Agregar jugador", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (teamPlayers.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay jugadores en este equipo.", color = SportProTextMuted)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(teamPlayers, key = { it.id }) { player ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SportProCardBackground)
                                    .border(1.dp, SportProCardBorder, RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(SportProGreen.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "#${player.jerseyNumber}",
                                            color = SportProGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = player.fullName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = SportProTextPrimary
                                        )
                                        Text(
                                            text = "${player.position} • ${player.age} años • ${player.status}",
                                            fontSize = 12.sp,
                                            color = SportProTextSecondary
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.removePlayerFromTeam(team.id, player.id) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remover",
                                        tint = SportProError.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Entrenamientos Tab
                if (teamTrainings.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay entrenamientos para este equipo.", color = SportProTextMuted)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(teamTrainings) { training ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, SportProCardBorder, RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(containerColor = SportProCardBackground)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(training.title, fontWeight = FontWeight.Bold, color = SportProTextPrimary)
                                        Text(training.date, fontSize = 12.sp, color = SportProGreen)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(training.objective, fontSize = 12.sp, color = SportProTextSecondary)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "${training.durationMinutes} min • ${training.exercises.size} ejercicios",
                                        fontSize = 11.sp,
                                        color = SportProTextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Partidos Tab
                if (teamMatches.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay partidos programados para este equipo.", color = SportProTextMuted)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(teamMatches) { match ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, SportProCardBorder, RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(containerColor = SportProCardBackground)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("vs ${match.rival}", fontWeight = FontWeight.Bold, color = SportProTextPrimary)
                                        Text(match.status, fontSize = 12.sp, color = SportProGreen)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("${match.date} - ${match.time} • ${match.stadium}", fontSize = 12.sp, color = SportProTextSecondary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (match.isHome) "Local" else "Visitante",
                                        fontSize = 11.sp,
                                        color = SportProTextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal de Agregar Jugador: Ofrece Registrar Nuevo Jugador O Seleccionar Existente
    if (showAddPlayerDialog) {
        val nonTeamPlayers = availablePlayers.filter { it.id !in team.playerIds }

        AlertDialog(
            onDismissRequest = { showAddPlayerDialog = false },
            containerColor = SportProCardBackground,
            title = {
                Text(
                    text = "Agregar jugador a ${team.name}",
                    color = SportProTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Opción A: Registrar nuevo jugador
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SportProGreenContainer)
                            .border(1.dp, SportProGreen, RoundedCornerShape(12.dp))
                            .clickable {
                                showAddPlayerDialog = false
                                onRegisterNewPlayerClick(team.id)
                            }
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = null,
                                tint = SportProGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "+ Registrar nuevo jugador",
                                color = SportProGreen,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Jugadores disponibles",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportProTextMuted
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Opción B: Seleccionar jugadores existentes
                    if (nonTeamPlayers.isEmpty()) {
                        Text(
                            text = "No hay otros jugadores registrados disponibles.",
                            color = SportProTextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.height(220.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(nonTeamPlayers, key = { it.id }) { player ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SportProDarkBackground)
                                        .clickable {
                                            viewModel.addPlayerToTeam(team.id, player.id)
                                            showAddPlayerDialog = false
                                        }
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = player.fullName,
                                            color = SportProTextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "${player.position} • ${player.category}",
                                            fontSize = 11.sp,
                                            color = SportProTextSecondary
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Agregar",
                                        tint = SportProGreen
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddPlayerDialog = false }) {
                    Text("Cerrar", color = SportProGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
