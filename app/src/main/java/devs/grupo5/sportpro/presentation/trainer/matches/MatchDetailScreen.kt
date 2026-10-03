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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import devs.grupo5.sportpro.data.model.MatchEvent
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBorder
import devs.grupo5.sportpro.presentation.trainer.theme.SportProDarkBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProGreen
import devs.grupo5.sportpro.presentation.trainer.theme.SportProGreenContainer
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextMuted
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextPrimary
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    matchId: String,
    viewModel: MatchViewModel,
    onBackClick: () -> Unit,
    onAiSummaryClick: (String) -> Unit
) {
    val matches by viewModel.matches.collectAsState()
    val match = matches.find { it.id == matchId } ?: viewModel.getMatchById(matchId)

    var selectedSectionDialog by remember { mutableStateOf<String?>(null) }
    var showTimelineSheet by remember { mutableStateOf(false) }

    if (match == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SportProDarkBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("Partido no encontrado", color = SportProTextPrimary)
        }
        return
    }

    val homeTeamName = if (match.isHome) "SportPro" else match.rival
    val awayTeamName = if (match.isHome) match.rival else "SportPro"
    val isFinalized = match.status.equals("Finalizado", ignoreCase = true)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SportProDarkBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = SportProTextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Detalle del Partido",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportProTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Scoreboard Header Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, SportProCardBorder, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = SportProCardBackground)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Category & Status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SportProDarkBackground)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = match.category,
                                    color = SportProGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            MatchStatusChip(status = match.status)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Match Score Display
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = homeTeamName,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SportProTextPrimary
                                )
                                Text(
                                    text = if (match.isHome) "Local" else "Visitante",
                                    fontSize = 11.sp,
                                    color = SportProTextSecondary
                                )
                            }

                            if (match.homeScore != null && match.awayScore != null) {
                                Text(
                                    text = "${match.homeScore} - ${match.awayScore}",
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SportProGreen
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SportProDarkBackground)
                                        .padding(horizontal = 16.dp, vertical = 6.dp)
                                ) {
                                    Text("VS", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SportProGreen)
                                }
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = awayTeamName,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SportProTextPrimary
                                )
                                Text(
                                    text = if (match.isHome) "Visitante" else "Local",
                                    fontSize = 11.sp,
                                    color = SportProTextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stadium & Date
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
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${match.date} • ${match.time}",
                                    color = SportProTextSecondary,
                                    fontSize = 12.sp
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = SportProTextMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = match.stadium,
                                    color = SportProTextMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Quick Actions: "Convocatoria", "Alineación", y ÚNICAMENTE "Resumen IA" SI status == "Finalizado"
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Convocatoria
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SportProCardBackground)
                            .border(1.dp, SportProCardBorder, RoundedCornerShape(12.dp))
                            .clickable { selectedSectionDialog = "Convocatoria" }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ListAlt,
                                contentDescription = null,
                                tint = SportProTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Convocatoria",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SportProTextPrimary
                            )
                        }
                    }

                    // Alineación
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SportProCardBackground)
                            .border(1.dp, SportProCardBorder, RoundedCornerShape(12.dp))
                            .clickable { selectedSectionDialog = "Alineación" }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Groups,
                                contentDescription = null,
                                tint = SportProTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Alineación",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SportProTextPrimary
                            )
                        }
                    }

                    // Resumen IA: ÚNICAMENTE visible cuando el partido está FINALIZADO
                    if (isFinalized) {
                        Box(
                            modifier = Modifier
                                .weight(1.2f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SportProGreenContainer)
                                .border(1.dp, SportProGreen, RoundedCornerShape(12.dp))
                                .clickable { onAiSummaryClick(match.id) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = SportProGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Resumen IA",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SportProGreen
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Section "Eventos del partido"
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Eventos del partido",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportProTextPrimary
                    )

                    Text(
                        text = "Ver cronología",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportProGreen,
                        modifier = Modifier.clickable { showTimelineSheet = true }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (match.events.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SportProCardBackground)
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isFinalized) "No se registraron eventos en este partido." else "Aún no hay eventos para este partido.",
                            color = SportProTextMuted,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    match.events.forEach { ev ->
                        DetailedMatchEventItem(event = ev)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Primary AI Summary Trigger Banner (ÚNICAMENTE si status == FINALIZADO)
            if (isFinalized) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, SportProGreen, RoundedCornerShape(16.dp))
                            .clickable { onAiSummaryClick(match.id) },
                        colors = CardDefaults.cardColors(containerColor = SportProGreenContainer)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = SportProGreen,
                                modifier = Modifier.size(32.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Generar Resumen con IA",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SportProGreen
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Crea un informe narrativo detallado para la plantilla y la comunidad.",
                                    fontSize = 12.sp,
                                    color = SportProTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Section Dialog for Convocatoria / Alineación
        selectedSectionDialog?.let { title ->
            ModalBottomSheet(
                onDismissRequest = { selectedSectionDialog = null },
                containerColor = SportProCardBackground,
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportProTextPrimary
                        )
                        IconButton(onClick = { selectedSectionDialog = null }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = SportProTextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (title == "Convocatoria")
                            "Jugadores convocados para el encuentro:\n\n• 1. Matías Rojas (POR)\n• 4. Sebastián Torres (DEF)\n• 8. Felipe Muñoz (MED)\n• 9. Carlos Mendoza (DEL)\n• 10. Diego Herrera (MED)\n• 11. Nicolás Vega (DEL)"
                        else
                            "Esquema Táctico: 4-3-3\n\nTitulares:\n• POR: Matías Rojas\n• DEF: S. Torres, C. Rivas, M. Silva, J. Pérez\n• MED: D. Herrera, F. Muñoz, A. Castro\n• DEL: C. Mendoza, N. Vega, E. Morales",
                        fontSize = 14.sp,
                        color = SportProTextSecondary,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { selectedSectionDialog = null },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SportProGreen,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Aceptar", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Timeline Modal Sheet
        if (showTimelineSheet) {
            ModalBottomSheet(
                onDismissRequest = { showTimelineSheet = false },
                containerColor = SportProCardBackground,
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cronología del Partido",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportProTextPrimary
                        )
                        IconButton(onClick = { showTimelineSheet = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = SportProTextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (match.events.isEmpty()) {
                        Text("Aún no hay eventos en la cronología.", color = SportProTextMuted, fontSize = 13.sp)
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(match.events, key = { it.id }) { ev ->
                                DetailedMatchEventItem(event = ev)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun DetailedMatchEventItem(event: MatchEvent) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, SportProCardBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = SportProCardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${event.minute}'",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SportProGreen
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = event.icon,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = if (event.player.isNotEmpty()) event.player else event.eventType,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportProTextPrimary
                    )
                    Text(
                        text = "${event.eventType} • ${event.team}",
                        fontSize = 12.sp,
                        color = SportProTextSecondary
                    )
                }
            }

            // Status tag (e.g. Local, Visitante, Corregido, Anulado)
            EventStatusChip(status = event.status)
        }
    }
}
