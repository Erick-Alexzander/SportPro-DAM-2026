package devs.grupo5.sportpro.presentation.trainer.training

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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import devs.grupo5.sportpro.presentation.trainer.teams.CategoryChip
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBorder
import devs.grupo5.sportpro.presentation.trainer.theme.SportProDarkBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProGreen
import devs.grupo5.sportpro.presentation.trainer.theme.SportProGreenContainer
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextMuted
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextPrimary
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextSecondary

@Composable
fun TrainingDetailScreen(
    trainingId: String,
    viewModel: TrainingViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    val trainings by viewModel.trainings.collectAsState()
    val allPlayers by viewModel.players.collectAsState()

    val training = trainings.find { it.id == trainingId } ?: viewModel.getTrainingById(trainingId)

    if (training == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SportProDarkBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("Entrenamiento no encontrado.", color = SportProTextMuted)
        }
        return
    }

    val categoryPlayers = allPlayers.filter { it.category == training.category || training.category.isEmpty() }
    val attendeeIds = remember { mutableStateListOf<String>().apply { addAll(training.attendees) } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SportProDarkBackground)
            .padding(16.dp)
    ) {
        // Top Bar
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
                text = training.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SportProTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Overview card
            item {
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
                            CategoryChip(category = training.category)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = SportProGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${training.date} • ${training.startTime} (${training.durationMinutes} min)",
                                    fontSize = 12.sp,
                                    color = SportProGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Objetivo de la sesión:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportProTextPrimary
                        )
                        Text(
                            text = training.objective,
                            fontSize = 13.sp,
                            color = SportProTextSecondary
                        )
                    }
                }
            }

            // Exercises List
            item {
                Text(
                    text = "Ejercicios programados (${training.exercises.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportProTextPrimary
                )
            }

            items(training.exercises) { exercise ->
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
                                .background(SportProGreenContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = SportProGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = exercise.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = SportProTextPrimary
                            )
                            Text(
                                text = "${exercise.durationMinutes} minutos",
                                fontSize = 12.sp,
                                color = SportProTextSecondary
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SportProGreen.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = exercise.type,
                            color = SportProGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Attendance Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Control de Asistencia",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportProTextPrimary
                    )
                    Text(
                        text = "${attendeeIds.size} / ${categoryPlayers.size} Presentes",
                        fontSize = 13.sp,
                        color = SportProGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(categoryPlayers) { player ->
                val isPresent = attendeeIds.contains(player.id)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SportProCardBackground)
                        .border(1.dp, SportProCardBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            if (isPresent) attendeeIds.remove(player.id)
                            else attendeeIds.add(player.id)
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = player.fullName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SportProTextPrimary
                        )
                        Text(
                            text = "${player.position} • Camiseta #${player.jerseyNumber}",
                            fontSize = 12.sp,
                            color = SportProTextSecondary
                        )
                    }

                    Checkbox(
                        checked = isPresent,
                        onCheckedChange = { checked ->
                            if (checked) attendeeIds.add(player.id)
                            else attendeeIds.remove(player.id)
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = SportProGreen,
                            checkmarkColor = Color.Black,
                            uncheckedColor = SportProTextMuted
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.updateAttendance(training.id, attendeeIds.toList())
                onBackClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SportProGreen,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Registrar asistencia",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
