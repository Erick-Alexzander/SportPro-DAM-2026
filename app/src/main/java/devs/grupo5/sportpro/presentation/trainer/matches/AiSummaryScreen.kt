package devs.grupo5.sportpro.presentation.trainer.matches

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import devs.grupo5.sportpro.data.model.AiSummary
import devs.grupo5.sportpro.data.model.Match
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
import kotlinx.coroutines.delay

enum class AiSummaryStep {
    START,
    PROCESSING,
    RESULT,
    APPROVED
}

@Composable
fun AiSummaryScreen(
    matchId: String,
    viewModel: MatchViewModel,
    onBackClick: () -> Unit,
    onViewMatchClick: () -> Unit,
    onGoToMatchesClick: () -> Unit,
    onPublishToCommunityClick: (String) -> Unit
) {
    val matches by viewModel.matches.collectAsState()
    val match = matches.find { it.id == matchId } ?: viewModel.getMatchById(matchId)

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

    // PROTECCIÓN DE NAVEGACIÓN: El resumen con IA SOLO está permitido para partidos FINALIZADOS
    val isFinalized = match.status.equals("Finalizado", ignoreCase = true)
    if (!isFinalized) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SportProDarkBackground)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, SportProCardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SportProCardBackground)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = SportProWarning,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Resumen IA no disponible",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportProTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "El resumen narrativo con IA sólo está disponible para partidos con estado Finalizado.\n\nEstado actual del partido: ${match.status}",
                        fontSize = 14.sp,
                        color = SportProTextSecondary,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onBackClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SportProGreen,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Volver al partido", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        return
    }

    var currentStep by remember {
        mutableStateOf(
            if (match.aiSummary?.isApproved == true) AiSummaryStep.APPROVED
            else if (match.aiSummary != null) AiSummaryStep.RESULT
            else AiSummaryStep.START
        )
    }

    var progress by remember { mutableFloatStateOf(0f) }
    var isEditingModalOpen by remember { mutableStateOf(false) }

    // Simulation of progress in PROCESSING step
    LaunchedEffect(currentStep) {
        if (currentStep == AiSummaryStep.PROCESSING) {
            progress = 0f
            while (progress < 1.0f) {
                delay(120)
                progress += 0.08f
            }
            viewModel.generateAiSummary(match.id)
            currentStep = AiSummaryStep.RESULT
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SportProDarkBackground)
    ) {
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "AiSummarySteps"
        ) { step ->
            when (step) {
                AiSummaryStep.START -> AiSummaryStartContent(
                    match = match,
                    onBackClick = onBackClick,
                    onStartGenerateClick = { currentStep = AiSummaryStep.PROCESSING }
                )

                AiSummaryStep.PROCESSING -> AiSummaryProcessingContent(
                    match = match,
                    progress = progress
                )

                AiSummaryStep.RESULT -> {
                    val summary = match.aiSummary ?: viewModel.generateAiSummary(match.id)
                    AiSummaryResultContent(
                        summary = summary,
                        onBackClick = onBackClick,
                        onEditClick = { isEditingModalOpen = true },
                        onApproveClick = {
                            viewModel.approveAiSummary(match.id)
                            currentStep = AiSummaryStep.APPROVED
                        }
                    )
                }

                AiSummaryStep.APPROVED -> AiSummaryApprovedContent(
                    onViewMatchClick = onViewMatchClick,
                    onGoToMatchesClick = onGoToMatchesClick,
                    onPublishClick = { onPublishToCommunityClick(match.id) }
                )
            }
        }

        // Edit Modal Sheet
        val activeSummary = match.aiSummary
        if (isEditingModalOpen && activeSummary != null) {
            EditAiSummaryBottomSheet(
                currentSummary = activeSummary,
                onDismiss = { isEditingModalOpen = false },
                onSave = { updatedSummary ->
                    viewModel.updateAiSummary(match.id, updatedSummary)
                    isEditingModalOpen = false
                }
            )
        }
    }
}

@Composable
fun AiSummaryStartContent(
    match: Match,
    onBackClick: () -> Unit,
    onStartGenerateClick: () -> Unit
) {
    val homeName = if (match.isHome) "SportPro" else match.rival
    val awayName = if (match.isHome) match.rival else "SportPro"
    val hasEvents = match.events.isNotEmpty()

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
                    text = "Resumen con IA",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportProTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Match Info Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, SportProCardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SportProCardBackground)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$homeName vs $awayName",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportProTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Resultado Final: ${match.homeScore ?: 0} - ${match.awayScore ?: 0}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SportProGreen
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Categoría ${match.category} • ${match.events.size} eventos registrados",
                        fontSize = 12.sp,
                        color = SportProTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Disclaimer Note Box or Warning if No Events
        item {
            if (!hasEvents) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, SportProError.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = SportProDarkBackground)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = SportProError,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "No hay eventos registrados en este partido. No es posible generar un resumen narrativo.",
                            fontSize = 13.sp,
                            color = SportProError,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, SportProGreenContainer, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = SportProDarkBackground)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = SportProGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "SportPro IA genera un resumen textual basado únicamente en los eventos registrados durante el partido. No realiza predicciones, no sugiere tácticas ni propone alineaciones automáticas.",
                            fontSize = 12.sp,
                            color = SportProTextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Central AI Action Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        1.dp,
                        if (hasEvents) SportProGreen else SportProCardBorder,
                        RoundedCornerShape(20.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = SportProCardBackground)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(if (hasEvents) SportProGreenContainer else SportProDarkBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = if (hasEvents) SportProGreen else SportProTextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Generar resumen automático",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hasEvents) SportProTextPrimary else SportProTextMuted
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (hasEvents)
                            "El análisis se basará exclusivamente en los eventos registrados en la planilla del partido."
                        else
                            "Para generar un resumen con IA es necesario haber registrado eventos durante el partido.",
                        fontSize = 13.sp,
                        color = SportProTextSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onStartGenerateClick,
                        enabled = hasEvents,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SportProGreen,
                            contentColor = Color.Black,
                            disabledContainerColor = SportProCardBorder,
                            disabledContentColor = SportProTextMuted
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Generar resumen con IA",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AiSummaryProcessingContent(
    match: Match,
    progress: Float
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(SportProGreenContainer),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = SportProGreen,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Analizando eventos del partido...",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SportProTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Procesando ${match.events.size} eventos registrados",
                fontSize = 14.sp,
                color = SportProGreen,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Progress bar
            Column(
                modifier = Modifier.fillMaxWidth(0.85f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = SportProGreen,
                    trackColor = SportProCardBorder
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 13.sp,
                    color = SportProTextMuted,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Esto puede tomar unos segundos...",
                fontSize = 13.sp,
                color = SportProTextMuted
            )
        }
    }
}

@Composable
fun AiSummaryResultContent(
    summary: AiSummary,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onApproveClick: () -> Unit
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = SportProTextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Resumen generado",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportProTextPrimary
                    )
                }

                // Badge: Pendiente de revisión
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SportProWarning.copy(alpha = 0.2f))
                        .border(1.dp, SportProWarning, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Pendiente de revisión",
                        color = SportProWarning,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Summary Main Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, SportProCardBorder, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = SportProCardBackground)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Header inside card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = SportProGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Informe Narrativo SportPro IA",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = SportProGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 1. Introducción
                    Text(
                        text = "1. INTRODUCCIÓN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportProTextMuted,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = summary.introduction,
                        fontSize = 14.sp,
                        color = SportProTextPrimary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Desarrollo
                    Text(
                        text = "2. DESARROLLO DEL ENCUENTRO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportProTextMuted,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = summary.development,
                        fontSize = 14.sp,
                        color = SportProTextPrimary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. Destacados
                    Text(
                        text = "3. DESTACADOS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportProTextMuted,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = summary.highlights,
                        fontSize = 14.sp,
                        color = SportProTextPrimary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Nota del análisis
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SportProDarkBackground)
                            .border(1.dp, SportProCardBorder, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "📌 ${summary.analysisNote}",
                            fontSize = 12.sp,
                            color = SportProTextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Action Buttons: Edit / Approve
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Editar Button
                OutlinedButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = SportProTextPrimary
                    ),
                    border = BorderStroke(1.dp, SportProCardBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = SportProTextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Editar",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Aprobar Button
                Button(
                    onClick = onApproveClick,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SportProGreen,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Aprobar resumen",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AiSummaryApprovedContent(
    onViewMatchClick: () -> Unit,
    onGoToMatchesClick: () -> Unit,
    onPublishClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Checkmark Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(SportProGreenContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = SportProGreen,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SportProGreenContainer)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Aprobado",
                    color = SportProGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Resumen aprobado",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = SportProTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "El resumen del partido ha sido revisado y aprobado por el cuerpo técnico. Quedará disponible en el historial del partido.",
                fontSize = 14.sp,
                color = SportProTextSecondary,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Main Action: Publicar en Comunidad
            Button(
                onClick = onPublishClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SportProGreen,
                    contentColor = Color.Black
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Publicar en Comunidad",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary Buttons: Ver partido & Ir a partidos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onViewMatchClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SportProTextPrimary),
                    border = BorderStroke(1.dp, SportProCardBorder)
                ) {
                    Text("Ver partido", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onGoToMatchesClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SportProTextPrimary),
                    border = BorderStroke(1.dp, SportProCardBorder)
                ) {
                    Text("Ir a partidos", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAiSummaryBottomSheet(
    currentSummary: AiSummary,
    onDismiss: () -> Unit,
    onSave: (AiSummary) -> Unit
) {
    var intro by remember { mutableStateOf(currentSummary.introduction) }
    var dev by remember { mutableStateOf(currentSummary.development) }
    var high by remember { mutableStateOf(currentSummary.highlights) }
    var note by remember { mutableStateOf(currentSummary.analysisNote) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SportProCardBackground,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "✏ Editar Resumen de IA",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportProTextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = SportProTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Field 1: Introducción
            item {
                Text(
                    text = "INTRODUCCIÓN",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportProTextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = intro,
                    onValueChange = { intro = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SportProGreen,
                        unfocusedBorderColor = SportProCardBorder,
                        focusedTextColor = SportProTextPrimary,
                        unfocusedTextColor = SportProTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Field 2: Desarrollo
            item {
                Text(
                    text = "DESARROLLO DEL ENCUENTRO",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportProTextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = dev,
                    onValueChange = { dev = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SportProGreen,
                        unfocusedBorderColor = SportProCardBorder,
                        focusedTextColor = SportProTextPrimary,
                        unfocusedTextColor = SportProTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Field 3: Destacados
            item {
                Text(
                    text = "DESTACADOS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportProTextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = high,
                    onValueChange = { high = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SportProGreen,
                        unfocusedBorderColor = SportProCardBorder,
                        focusedTextColor = SportProTextPrimary,
                        unfocusedTextColor = SportProTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Field 4: Nota del análisis
            item {
                Text(
                    text = "NOTA DEL ANÁLISIS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportProTextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SportProGreen,
                        unfocusedBorderColor = SportProCardBorder,
                        focusedTextColor = SportProTextPrimary,
                        unfocusedTextColor = SportProTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Buttons: Cancelar / Guardar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SportProTextPrimary),
                        border = BorderStroke(1.dp, SportProCardBorder)
                    ) {
                        Text("Cancelar", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            onSave(
                                currentSummary.copy(
                                    introduction = intro,
                                    development = dev,
                                    highlights = high,
                                    analysisNote = note
                                )
                            )
                        },
                        modifier = Modifier
                            .weight(1.2f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SportProGreen,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Guardar cambios", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
