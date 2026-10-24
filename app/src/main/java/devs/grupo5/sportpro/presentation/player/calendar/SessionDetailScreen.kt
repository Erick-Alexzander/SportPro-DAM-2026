package devs.grupo5.sportpro.presentation.player.calendar

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextMuted
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextPrimary
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextSecondary
import devs.grupo5.sportpro.presentation.player.theme.SportProPlayerTheme

@Composable
fun SessionDetailScreen(
    sessionId: String = "",
    onBackClick: () -> Unit = {},
    onTabSelected: (PlayerTab) -> Unit = {}
) {
    val session = findSessionById(sessionId)

    SportProPlayerTheme {
        Scaffold(
            containerColor = PlayerBackground,
            bottomBar = {
                PlayerBottomNavigationBar(
                    currentTab = PlayerTab.CALENDAR,
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
                    SessionDetailTopBar(onBackClick = onBackClick)
                }

                item {
                    SessionInfoCard(session = session)
                }

                item {
                    ObjectiveCard(objective = session.objective)
                }

                item {
                    Text(
                        text = "Ejercicios de la sesión",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlayerTextPrimary
                    )
                }

                itemsIndexed(
                    items = session.exercises,
                    key = { index, _ -> "${session.id}_$index" }
                ) { index, exercise ->
                    ExerciseCard(number = index + 1, exercise = exercise)
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun SessionDetailTopBar(onBackClick: () -> Unit) {
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
            text = "Detalle de sesión",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = PlayerTextPrimary
        )
    }
}

@Composable
private fun SessionInfoCard(session: PlayerTrainingSession) {
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            InfoRow(
                icon = Icons.Default.CalendarMonth,
                label = "Fecha",
                value = session.date
            )
            InfoRow(
                icon = Icons.Default.AccessTime,
                label = "Hora",
                value = session.time
            )
            InfoRow(
                icon = Icons.Default.Timer,
                label = "Duración",
                value = "${session.durationMinutes} min"
            )
            InfoRow(
                icon = Icons.AutoMirrored.Filled.Label,
                label = "Categoría",
                value = session.category
            )
            InfoRow(
                icon = Icons.Default.Groups,
                label = "Asistencia",
                value = "${session.attendeesCount} jugadores"
            )
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PlayerNeonGreen,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            color = PlayerTextSecondary
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = PlayerTextPrimary
        )
    }
}

@Composable
private fun ObjectiveCard(objective: String) {
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
                text = "Objetivo",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PlayerTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = objective,
                fontSize = 14.sp,
                color = PlayerTextSecondary,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun ExerciseCard(
    number: Int,
    exercise: TrainingExercise
) {
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
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(PlayerNeonGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF00210B)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlayerTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${exercise.type} · ${exercise.durationMinutes} min",
                    fontSize = 12.sp,
                    color = PlayerTextMuted
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF101413)
@Composable
private fun SessionDetailScreenPreview() {
    SessionDetailScreen(sessionId = "t1")
}
