package devs.grupo5.sportpro.presentation.trainer.matches

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import devs.grupo5.sportpro.data.model.Match
import devs.grupo5.sportpro.data.model.MatchEvent
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

data class QuickEventOption(
    val type: String,
    val icon: String
)

val eventOptions = listOf(
    QuickEventOption("Gol", "⚽"),
    QuickEventOption("T. Amarilla", "🟨"),
    QuickEventOption("T. Roja", "🟥"),
    QuickEventOption("Cambio", "🔄"),
    QuickEventOption("Córner", "🚩"),
    QuickEventOption("Falta", "⚠️"),
    QuickEventOption("Penal", "🎯"),
    QuickEventOption("Fuera de juego", "📌")
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LiveMatchScreen(
    matchId: String,
    viewModel: MatchViewModel,
    onBackClick: () -> Unit,
    onFinishMatchClick: (String) -> Unit
) {
    val matches by viewModel.matches.collectAsState()
    val match = matches.find { it.id == matchId } ?: viewModel.getMatchById(matchId)
    val toastMessage by viewModel.eventConfirmationToast.collectAsState()

    var activeModalEvent by remember { mutableStateOf<QuickEventOption?>(null) }
    var showFullTimelineSheet by remember { mutableStateOf(false) }

    // Auto toast clearing
    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            delay(3000)
            viewModel.clearConfirmationToast()
        }
    }

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
            // Header: Arrow back + Title + Live badge
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
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
                            text = "Partido en Vivo",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportProTextPrimary
                        )
                    }

                    // Badge EN VIVO
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SportProWarning.copy(alpha = 0.2f))
                            .border(1.dp, SportProWarning, RoundedCornerShape(12.dp))
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
                                text = "EN VIVO",
                                color = SportProWarning,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Toast feedback
            item {
                AnimatedVisibility(
                    visible = toastMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SportProGreenContainer)
                            .border(1.dp, SportProGreen, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SportProGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = toastMessage ?: "",
                                color = SportProGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Scoreboard Card
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
                        // Category & Stadium info
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

                        Spacer(modifier = Modifier.height(16.dp))

                        // Score & Minute / Timer Controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Home Team
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (match.isHome) "SportPro" else match.rival,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SportProTextPrimary
                                )
                                Text(
                                    text = "Local",
                                    fontSize = 12.sp,
                                    color = SportProTextSecondary
                                )
                            }

                            // Big Scoreboard & Timer
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${match.homeScore ?: 0} - ${match.awayScore ?: 0}",
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SportProGreen
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(SportProDarkBackground)
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${match.currentMinute}'",
                                        color = SportProWarning,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clip(CircleShape)
                                            .background(SportProGreen)
                                            .clickable { viewModel.toggleMatchPause(match.id) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (match.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                            contentDescription = if (match.isPaused) "Reanudar" else "Pausar",
                                            tint = Color.Black,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            // Away Team
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (match.isHome) match.rival else "SportPro",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SportProTextPrimary
                                )
                                Text(
                                    text = "Visitante",
                                    fontSize = 12.sp,
                                    color = SportProTextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Section "REGISTRAR EVENTO"
            item {
                Text(
                    text = "REGISTRAR EVENTO",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportProTextMuted,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    maxItemsInEachRow = 4
                ) {
                    eventOptions.forEach { opt ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SportProCardBackground)
                                .border(1.dp, SportProCardBorder, RoundedCornerShape(12.dp))
                                .clickable { activeModalEvent = opt }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = opt.icon, fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = opt.type,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SportProTextPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Section "Cronología reciente"
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cronología reciente",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportProTextPrimary
                    )

                    Text(
                        text = "Ver todo",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportProGreen,
                        modifier = Modifier.clickable { showFullTimelineSheet = true }
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
                            text = "Aún no hay eventos registrados.",
                            color = SportProTextMuted,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    val recentEvents = match.events.takeLast(4).reversed()
                    recentEvents.forEach { ev ->
                        EventRowItem(event = ev)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Finish Match Button
            item {
                Button(
                    onClick = {
                        val finished = viewModel.finishMatch(match.id)
                        if (finished != null) {
                            onFinishMatchClick(finished.id)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SportProError,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Finalizar partido",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Modal BottomSheet for Register Event
        activeModalEvent?.let { opt ->
            RegisterEventBottomSheet(
                eventOption = opt,
                match = match,
                onDismiss = { activeModalEvent = null },
                onConfirm = { minute, team, player, status, detail ->
                    viewModel.addMatchEvent(
                        matchId = match.id,
                        minute = minute,
                        eventType = opt.type,
                        icon = opt.icon,
                        player = player,
                        team = team,
                        status = status,
                        detail = detail
                    )
                    activeModalEvent = null
                }
            )
        }

        // Full Timeline Modal Sheet
        if (showFullTimelineSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFullTimelineSheet = false },
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
                            text = "Cronología Completa",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportProTextPrimary
                        )
                        IconButton(onClick = { showFullTimelineSheet = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = SportProTextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (match.events.isEmpty()) {
                        Text("No hay eventos.", color = SportProTextMuted)
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(match.events.reversed(), key = { it.id }) { ev ->
                                EventRowItem(event = ev)
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
fun EventRowItem(event: MatchEvent) {
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
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SportProDarkBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = event.icon, fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${event.minute}' ${event.eventType}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SportProTextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(${event.team})",
                            fontSize = 11.sp,
                            color = SportProTextSecondary
                        )
                    }

                    if (event.player.isNotEmpty()) {
                        Text(
                            text = event.player,
                            fontSize = 12.sp,
                            color = SportProGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (event.detail.isNotEmpty()) {
                        Text(
                            text = event.detail,
                            fontSize = 11.sp,
                            color = SportProTextMuted
                        )
                    }
                }
            }

            // Status Badge (Local, Visitante, Corregido, Anulado, Pendiente)
            EventStatusChip(status = event.status)
        }
    }
}

@Composable
fun EventStatusChip(status: String) {
    val (bgColor, textColor) = when (status.lowercase()) {
        "corregido" -> Pair(SportProWarning.copy(alpha = 0.2f), SportProWarning)
        "anulado" -> Pair(SportProError.copy(alpha = 0.2f), SportProError)
        "pendiente" -> Pair(SportProCardBorder, SportProTextMuted)
        "visitante" -> Pair(SportProGreenContainer, SportProGreen)
        else -> Pair(SportProGreenContainer, SportProGreen)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = status,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterEventBottomSheet(
    eventOption: QuickEventOption,
    match: Match,
    onDismiss: () -> Unit,
    onConfirm: (minute: Int, team: String, player: String, status: String, detail: String) -> Unit
) {
    var minute by remember { mutableIntStateOf(match.currentMinute) }
    
    val localTeamName = if (match.isHome) "SportPro (Local)" else "${match.rival} (Local)"
    val awayTeamName = if (match.isHome) "${match.rival} (Vis.)" else "SportPro (Vis.)"
    
    var selectedTeam by remember { mutableStateOf(localTeamName) }

    val defaultPlayers = listOf(
        "Carlos Mendoza",
        "Diego Herrera",
        "Sebastián Torres",
        "Felipe Muñoz",
        "Rival #10",
        "Rival #7"
    )
    var selectedPlayer by remember { mutableStateOf(defaultPlayers.first()) }
    var customPlayerText by remember { mutableStateOf("") }
    var eventStatusChoice by remember { mutableStateOf("Local") } // "Local", "Visitante", "Corregido", "Anulado"
    var additionalNotes by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SportProCardBackground,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = eventOption.icon, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Registrar ${eventOption.type}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportProTextPrimary
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = SportProTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // MINUTO Selector (- / +)
            Text(
                text = "MINUTO",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SportProTextMuted
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = { if (minute > 1) minute-- },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SportProDarkBackground)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Disminuir minuto",
                        tint = SportProTextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(SportProDarkBackground)
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "$minute'",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SportProGreen
                    )
                }

                IconButton(
                    onClick = { if (minute < 120) minute++ },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SportProDarkBackground)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Aumentar minuto",
                        tint = SportProTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // EQUIPO Selector
            Text(
                text = "EQUIPO",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SportProTextMuted
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(localTeamName, awayTeamName).forEach { t ->
                    val isSelected = selectedTeam == t
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) SportProGreen else SportProDarkBackground)
                            .border(1.dp, if (isSelected) SportProGreen else SportProCardBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                selectedTeam = t
                                eventStatusChoice = if (t == localTeamName) "Local" else "Visitante"
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = t,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.Black else SportProTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // JUGADOR Selector
            Text(
                text = "JUGADOR",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SportProTextMuted
            )
            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                defaultPlayers.forEach { p ->
                    val isSelected = selectedPlayer == p && customPlayerText.isEmpty()
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) SportProGreen.copy(alpha = 0.2f) else SportProDarkBackground)
                            .border(1.dp, if (isSelected) SportProGreen else SportProCardBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                selectedPlayer = p
                                customPlayerText = ""
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = p,
                            fontSize = 12.sp,
                            color = if (isSelected) SportProGreen else SportProTextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = customPlayerText,
                onValueChange = { customPlayerText = it },
                label = { Text("Otro jugador (opcional)", color = SportProTextMuted, fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SportProGreen,
                    unfocusedBorderColor = SportProCardBorder,
                    focusedTextColor = SportProTextPrimary,
                    unfocusedTextColor = SportProTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ESTADO DE INCIDENCIA Choice
            Text(
                text = "ESTADO INCIDENCIA",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SportProTextMuted
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("Local", "Visitante", "Corregido", "Anulado").forEach { st ->
                    val isSel = eventStatusChoice.equals(st, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) SportProGreen else SportProDarkBackground)
                            .border(1.dp, if (isSel) SportProGreen else SportProCardBorder, RoundedCornerShape(8.dp))
                            .clickable { eventStatusChoice = st }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = st,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) Color.Black else SportProTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Additional Notes / Details
            OutlinedTextField(
                value = additionalNotes,
                onValueChange = { additionalNotes = it },
                label = { Text("Detalle adicional (ej: tiro libre, VAR, reemplazo)", color = SportProTextMuted, fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SportProGreen,
                    unfocusedBorderColor = SportProCardBorder,
                    focusedTextColor = SportProTextPrimary,
                    unfocusedTextColor = SportProTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Confirm Button
            Button(
                onClick = {
                    val finalPlayer = if (customPlayerText.isNotBlank()) customPlayerText.trim() else selectedPlayer
                    val shortTeamName = if (selectedTeam.contains("Local")) "Local" else "Visitante"
                    onConfirm(minute, shortTeamName, finalPlayer, eventStatusChoice, additionalNotes.trim())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SportProGreen,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "Confirmar evento",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
