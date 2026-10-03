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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import devs.grupo5.sportpro.data.model.Match
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBorder
import devs.grupo5.sportpro.presentation.trainer.theme.SportProDarkBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProGreen
import devs.grupo5.sportpro.presentation.trainer.theme.SportProGreenContainer
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextMuted
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextPrimary
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextSecondary

enum class PublishStep {
    PREVIEW,
    SUCCESS
}

@Composable
fun PublishCommunityScreen(
    matchId: String,
    viewModel: MatchViewModel,
    onBackClick: () -> Unit,
    onNavigateToCommunity: () -> Unit
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

    var currentStep by remember { mutableStateOf(PublishStep.PREVIEW) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SportProDarkBackground)
    ) {
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "PublishSteps"
        ) { step ->
            when (step) {
                PublishStep.PREVIEW -> PublishPreviewContent(
                    match = match,
                    onBackClick = onBackClick,
                    onPublishClick = {
                        viewModel.publishSummaryToCommunity(match.id)
                        currentStep = PublishStep.SUCCESS
                    }
                )

                PublishStep.SUCCESS -> PublishSuccessContent(
                    onGoToCommunityClick = onNavigateToCommunity
                )
            }
        }
    }
}

@Composable
fun PublishPreviewContent(
    match: Match,
    onBackClick: () -> Unit,
    onPublishClick: () -> Unit
) {
    val summary = match.aiSummary
    val homeName = if (match.isHome) "SportPro" else match.rival
    val awayName = if (match.isHome) match.rival else "SportPro"
    val score = "${match.homeScore ?: 0} - ${match.awayScore ?: 0}"

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
                    text = "Publicar en Comunidad",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportProTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Info Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SportProGreenContainer)
                    .border(1.dp, SportProGreen, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = "📢 Como Entrenador del equipo, estás publicando el resumen oficial aprobado del partido en el muro de la Comunidad SportPro.",
                    fontSize = 13.sp,
                    color = SportProGreen,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Visibility Selector
        item {
            Text(
                text = "VISIBILIDAD",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SportProTextMuted
            )
            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SportProCardBackground)
                    .border(1.dp, SportProCardBorder, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = SportProGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Comunidad pública",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SportProTextPrimary
                            )
                            Text(
                                text = "Visible para deportistas, apoderados y técnicos de SportPro",
                                fontSize = 11.sp,
                                color = SportProTextSecondary
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(SportProGreenContainer)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Pública",
                            color = SportProGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Post Preview Label
        item {
            Text(
                text = "VISTA PREVIA DE LA PUBLICACIÓN",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SportProTextMuted
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Post Card Preview
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, SportProGreen.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = SportProCardBackground)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Match Score Banner inside Post
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SportProDarkBackground)
                            .padding(12.dp)
                    ) {
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
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "$homeName vs $awayName",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SportProTextPrimary
                                    )
                                    Text(
                                        text = "Categoría ${match.category} • ${match.date}",
                                        fontSize = 11.sp,
                                        color = SportProTextSecondary
                                    )
                                }
                            }

                            Text(
                                text = score,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SportProGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (summary != null) {
                        Text(
                            text = summary.introduction,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SportProTextPrimary,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = summary.development,
                            fontSize = 13.sp,
                            color = SportProTextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }

        // Bottom Action Buttons: Cancelar / Publicar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SportProTextPrimary),
                    border = BorderStroke(1.dp, SportProCardBorder)
                ) {
                    Text("Cancelar", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onPublishClick,
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
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Publicar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PublishSuccessContent(
    onGoToCommunityClick: () -> Unit
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
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(SportProGreenContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = SportProGreen,
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Publicado en Comunidad",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = SportProTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "El resumen aprobado del partido ya está disponible para toda la comunidad de SportPro.",
                fontSize = 14.sp,
                color = SportProTextSecondary,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = onGoToCommunityClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SportProGreen,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "Ver publicación en Comunidad",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onGoToCommunityClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SportProTextPrimary),
                border = BorderStroke(1.dp, SportProCardBorder)
            ) {
                Text("Volver a Comunidad", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
