package devs.grupo5.sportpro.presentation.trainer.dashboard

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
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
import devs.grupo5.sportpro.presentation.trainer.theme.SportProWarning

@Composable
fun TrainerDashboardScreen(
    viewModel: TrainerDashboardViewModel = viewModel(),
    onNavigateToTrainings: () -> Unit = {},
    onNavigateToMatches: () -> Unit = {},
    onNavigateToTeams: () -> Unit = {},
    onNavigateToCommunity: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val totalPlayers by viewModel.totalPlayersCount.collectAsState()
    val liveMatch by viewModel.liveMatch.collectAsState()
    val nextTraining by viewModel.nextTraining.collectAsState()
    val nextMatch by viewModel.nextMatch.collectAsState()

    var showLogoutDialog by remember { mutableStateOf(false) }

    val trainerName = currentUser?.let { "${it.firstName} ${it.lastName}".trim() }
        .takeIf { !it.isNullOrBlank() } ?: "Entrenador DT"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SportProDarkBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Bienvenido,",
                            fontSize = 15.sp,
                            color = SportProTextSecondary
                        )
                        Text(
                            text = trainerName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportProTextPrimary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(SportProGreenContainer)
                                .border(1.dp, SportProGreen, CircleShape)
                                .clickable { showLogoutDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Perfil DT",
                                tint = SportProGreen
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(onClick = { showLogoutDialog = true }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Cerrar sesión",
                                tint = SportProError
                            )
                        }
                    }
                }
            }

            // Live Match Card if exists
            if (liveMatch != null) {
                item {
                    val match = liveMatch!!
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .border(1.dp, SportProWarning.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                            .clickable { onNavigateToMatches() },
                        colors = CardDefaults.cardColors(containerColor = SportProCardBackground)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SportProWarning.copy(alpha = 0.2f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(SportProWarning)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "PARTIDO EN VIVO",
                                            color = SportProWarning,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Text(
                                    text = match.category,
                                    fontSize = 12.sp,
                                    color = SportProTextMuted,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("SportPro", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SportProTextPrimary)
                                    Text(if (match.isHome) "Local" else "Visitante", fontSize = 11.sp, color = SportProTextSecondary)
                                }

                                Text(
                                    text = "${match.homeScore ?: 0} - ${match.awayScore ?: 0}",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SportProGreen
                                )

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(match.rival, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SportProTextPrimary)
                                    Text(if (!match.isHome) "Local" else "Visitante", fontSize = 11.sp, color = SportProTextSecondary)
                                }
                            }
                        }
                    }
                }
            }

            // Quick Stats Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Jugadores",
                        value = "$totalPlayers",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Vic. seguidas",
                        value = "${viewModel.consecutiveWins}",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Asistencia",
                        value = viewModel.attendanceRate,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Next Training Card
            item {
                val training = nextTraining
                if (training != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, SportProCardBorder, RoundedCornerShape(16.dp))
                            .clickable { onNavigateToTrainings() },
                        colors = CardDefaults.cardColors(containerColor = SportProCardBackground)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "PRÓXIMO ENTRENAMIENTO",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SportProGreen
                                )
                                Text(
                                    text = training.date,
                                    fontSize = 12.sp,
                                    color = SportProTextMuted
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = training.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = SportProTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${training.category} • ${training.startTime} (${training.durationMinutes} min)",
                                fontSize = 13.sp,
                                color = SportProTextSecondary
                            )
                        }
                    }
                }
            }

            // Next Match Card
            item {
                val match = nextMatch
                if (match != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, SportProCardBorder, RoundedCornerShape(16.dp))
                            .clickable { onNavigateToMatches() },
                        colors = CardDefaults.cardColors(containerColor = SportProCardBackground)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "PRÓXIMO PARTIDO",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SportProGreen
                                )
                                Text(
                                    text = match.date,
                                    fontSize = 12.sp,
                                    color = SportProTextMuted
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "SportPro vs ${match.rival}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = SportProTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${match.category} • ${match.time} • ${match.stadium}",
                                fontSize = 13.sp,
                                color = SportProTextSecondary
                            )
                        }
                    }
                }
            }

            // Acciones Rápidas
            item {
                Text(
                    text = "Acciones rápidas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportProTextPrimary
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        QuickActionButton(
                            title = "Entrenamientos",
                            icon = Icons.Default.FitnessCenter,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToTrainings
                        )
                        QuickActionButton(
                            title = "Partidos",
                            icon = Icons.Default.SportsSoccer,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToMatches
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        QuickActionButton(
                            title = "Equipo",
                            icon = Icons.Default.Groups,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToTeams
                        )
                        QuickActionButton(
                            title = "En Vivo",
                            icon = Icons.Default.PlayArrow,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToMatches
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        QuickActionButton(
                            title = "Estadísticas",
                            icon = Icons.Default.BarChart,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToTeams
                        )
                        QuickActionButton(
                            title = "Comunidad",
                            icon = Icons.Default.Forum,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToCommunity
                        )
                    }
                }
            }
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = SportProCardBackground,
            title = {
                Text(
                    text = "Cerrar sesión",
                    color = SportProTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de que deseas cerrar sesión?",
                    color = SportProTextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogoutClick()
                    }
                ) {
                    Text("Cerrar sesión", color = SportProError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar", color = SportProTextMuted)
                }
            }
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, SportProCardBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = SportProCardBackground)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SportProGreen
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                color = SportProTextSecondary
            )
        }
    }
}

@Composable
fun QuickActionButton(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, SportProCardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SportProCardBackground)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SportProGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportProTextPrimary
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = SportProTextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
