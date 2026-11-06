package devs.grupo5.sportpro.presentation.trainer.training

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import devs.grupo5.sportpro.data.model.AttendanceRecord
import devs.grupo5.sportpro.data.model.AttendanceStatus
import devs.grupo5.sportpro.presentation.trainer.theme.*

private val PresentGreen = Color(0xFF006B45)
private val AbsentRed = Color(0xFF5B1717)
private val JustifiedYellow = Color(0xFF725000)

@Composable
fun AttendanceScreen(
    trainingId: String,
    viewModel: TrainingViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    val trainings by viewModel.trainings.collectAsState()
    val players by viewModel.players.collectAsState()

    val training = trainings.find { it.id == trainingId }

    if (training == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Entrenamiento no encontrado")
        }
        return
    }

    val teamPlayers = players.filter {
        it.category == training.category
    }

    val attendance = remember(training.id) {
        mutableStateMapOf<String, AttendanceStatus>().apply {
            teamPlayers.forEach { player ->
                val savedStatus = training.attendanceRecords
                    .find { it.playerId == player.id }
                    ?.status

                this[player.id] = savedStatus
                    ?: if (player.id in training.attendees) {
                        AttendanceStatus.PRESENTE
                    } else {
                        AttendanceStatus.AUSENTE
                    }
            }
        }
    }

    val presentCount = attendance.values.count {
        it == AttendanceStatus.PRESENTE
    }
    val absentCount = attendance.values.count {
        it == AttendanceStatus.AUSENTE
    }
    val justifiedCount = attendance.values.count {
        it == AttendanceStatus.JUSTIFICADO
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SportProDarkBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SportProCardBackground)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }

            Column {
                Text(
                    "Asistencia",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    "${training.date} · ${training.startTime}",
                    color = SportProTextMuted,
                    fontSize = 12.sp
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SportProCardBackground)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                "Presente: $presentCount",
                color = Color(0xFF42D69A),
                fontSize = 12.sp
            )
            Text(
                "Ausente: $absentCount",
                color = Color(0xFFFF8585),
                fontSize = 12.sp
            )
            Text(
                "Justificado: $justifiedCount",
                color = Color(0xFFFFC857),
                fontSize = 12.sp
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(teamPlayers, key = { it.id }) { player ->

                val currentStatus =
                    attendance[player.id] ?: AttendanceStatus.AUSENTE

                val backgroundColor = when (currentStatus) {
                    AttendanceStatus.PRESENTE -> Color(0xFF08271F)
                    AttendanceStatus.AUSENTE -> Color(0xFF210C0C)
                    AttendanceStatus.JUSTIFICADO -> Color(0xFF241C07)
                }

                val statusColor = when (currentStatus) {
                    AttendanceStatus.PRESENTE -> PresentGreen
                    AttendanceStatus.AUSENTE -> AbsentRed
                    AttendanceStatus.JUSTIFICADO -> JustifiedYellow
                }

                val statusLabel = when (currentStatus) {
                    AttendanceStatus.PRESENTE -> "Presente"
                    AttendanceStatus.AUSENTE -> "Ausente"
                    AttendanceStatus.JUSTIFICADO -> "Justificado"
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            backgroundColor,
                            RoundedCornerShape(14.dp)
                        )
                        .border(
                            1.dp,
                            statusColor,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            attendance[player.id] = when (currentStatus) {
                                AttendanceStatus.PRESENTE ->
                                    AttendanceStatus.AUSENTE
                                AttendanceStatus.AUSENTE ->
                                    AttendanceStatus.JUSTIFICADO
                                AttendanceStatus.JUSTIFICADO ->
                                    AttendanceStatus.PRESENTE
                            }
                        }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            player.fullName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            "#${player.jerseyNumber} · ${player.position}",
                            color = SportProTextMuted,
                            fontSize = 12.sp
                        )
                    }

                    Surface(
                        color = statusColor,
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            statusLabel,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 8.dp
                            )
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                val records = teamPlayers.map { player ->
                    AttendanceRecord(
                        playerId = player.id,
                        status = attendance[player.id]
                            ?: AttendanceStatus.AUSENTE
                    )
                }

                viewModel.updateAttendanceRecords(
                    training.id,
                    records
                )

                onBackClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4DD594),
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text(
                "Guardar asistencia",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

