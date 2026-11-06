
package devs.grupo5.sportpro.presentation.trainer.training

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import devs.grupo5.sportpro.data.model.AttendanceStatus
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
    onBackClick: () -> Unit = {},
    onAttendanceClick: () -> Unit = {}
) {
    val trainings by viewModel.trainings.collectAsState()
    val allPlayers by viewModel.players.collectAsState()

    val training = trainings.find { it.id == trainingId }

    if (training == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SportProDarkBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Entrenamiento no encontrado.",
                color = SportProTextMuted
            )
        }
        return
    }

    val categoryPlayers = allPlayers.filter {
        it.category == training.category ||
                training.category.isEmpty()
    }

    val presentCount = if (training.attendanceRecords.isNotEmpty()) {
        training.attendanceRecords.count {
            it.status == AttendanceStatus.PRESENTE &&
                    categoryPlayers.any { player ->
                        player.id == it.playerId
                    }
        }
    } else {
        categoryPlayers.count { player ->
            player.id in training.attendees
        }
    }

    val absentCount = training.attendanceRecords.count {
        it.status == AttendanceStatus.AUSENTE &&
                categoryPlayers.any { player ->
                    player.id == it.playerId
                }
    }

    val justifiedCount = training.attendanceRecords.count {
        it.status == AttendanceStatus.JUSTIFICADO &&
                categoryPlayers.any { player ->
                    player.id == it.playerId
                }
    }

    val hasAttendanceRecords =
        training.attendanceRecords.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SportProDarkBackground)
            .padding(16.dp)
    ) {

        // CABECERA
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

            // DETALLE DEL ENTRENAMIENTO
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            1.dp,
                            SportProCardBorder,
                            RoundedCornerShape(16.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = SportProCardBackground
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween,
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {
                            CategoryChip(
                                category = training.category
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = SportProGreen,
                                    modifier = Modifier.size(16.dp)
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                Text(
                                    text = "${training.date} • ${training.startTime}",
                                    fontSize = 12.sp,
                                    color = SportProGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Duración: ${training.durationMinutes} minutos",
                            fontSize = 13.sp,
                            color = SportProTextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Objetivo de la sesión:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportProTextPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = training.objective,
                            fontSize = 13.sp,
                            color = SportProTextSecondary
                        )
                    }
                }
            }

            // EJERCICIOS
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
                        .border(
                            1.dp,
                            SportProCardBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SportProGreenContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector =
                                    Icons.Default.FitnessCenter,
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

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                SportProGreen.copy(alpha = 0.15f)
                            )
                            .padding(
                                horizontal = 10.dp,
                                vertical = 4.dp
                            )
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

            // HU-009: RESUMEN DE ASISTENCIA
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            SportProCardBorder,
                            RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SportProCardBackground
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "Control de Asistencia",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportProTextPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${categoryPlayers.size} jugadores en la categoría",
                            fontSize = 13.sp,
                            color = SportProTextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceEvenly
                        ) {

                            AttendanceCounter(
                                count = presentCount,
                                label = "Presentes",
                                color = SportProGreen
                            )

                            AttendanceCounter(
                                count = absentCount,
                                label = "Ausentes",
                                color = Color(0xFFFF8585)
                            )

                            AttendanceCounter(
                                count = justifiedCount,
                                label = "Justificados",
                                color = Color(0xFFFFC857)
                            )
                        }

                        if (!hasAttendanceRecords) {
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Asistencia completa pendiente de registrar.",
                                fontSize = 12.sp,
                                color = SportProTextMuted
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // HU-009: ABRIR PANTALLA DE ASISTENCIA
        Button(
            onClick = onAttendanceClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SportProGreen,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Registrar asistencia",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// COMPONENTE REUTILIZABLE PARA LOS CONTADORES
@Composable
private fun AttendanceCounter(
    count: Int,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            color = SportProTextSecondary
        )
    }
}
