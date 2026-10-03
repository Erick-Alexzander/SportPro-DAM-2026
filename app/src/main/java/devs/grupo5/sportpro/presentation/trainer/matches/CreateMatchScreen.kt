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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.lifecycle.viewmodel.compose.viewModel
import devs.grupo5.sportpro.data.model.Team
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBorder
import devs.grupo5.sportpro.presentation.trainer.theme.SportProDarkBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProGreen
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextMuted
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextPrimary
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextSecondary

@Composable
fun CreateMatchScreen(
    viewModel: MatchViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onMatchRegistered: () -> Unit = {}
) {
    val teams: List<Team> by viewModel.teams.collectAsState()

    var selectedTeamId by remember { mutableStateOf(teams.firstOrNull()?.id ?: "") }
    var selectedCategory by remember { mutableStateOf(teams.firstOrNull()?.category ?: "Primera") }

    var rival by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("Sábado, 15:00") }
    var time by remember { mutableStateOf("15:00") }
    var stadium by remember { mutableStateOf("Estadio Municipal") }
    var isHome by remember { mutableStateOf(true) }

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
                text = "Nuevo partido",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = SportProTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Seleccionar Equipo
            item {
                Column {
                    Text(
                        text = "Seleccionar Equipo",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SportProTextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (teams.isEmpty()) {
                        Text("No hay equipos disponibles", fontSize = 12.sp, color = SportProTextMuted)
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(teams, key = { it.id }) { team ->
                                val isSelected = selectedTeamId == team.id
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) SportProGreen else SportProCardBackground)
                                        .border(
                                            1.dp,
                                            if (isSelected) SportProGreen else SportProCardBorder,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            selectedTeamId = team.id
                                            selectedCategory = team.category
                                        }
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = team.name,
                                            color = if (isSelected) Color.Black else SportProTextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = team.category,
                                            color = if (isSelected) Color.DarkGray else SportProTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = rival,
                    onValueChange = { rival = it },
                    label = { Text("Equipo rival", color = SportProTextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SportProCardBackground,
                        unfocusedContainerColor = SportProCardBackground,
                        focusedBorderColor = SportProGreen,
                        unfocusedBorderColor = SportProCardBorder,
                        focusedTextColor = SportProTextPrimary,
                        unfocusedTextColor = SportProTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Fecha", color = SportProTextSecondary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SportProCardBackground,
                            unfocusedContainerColor = SportProCardBackground,
                            focusedBorderColor = SportProGreen,
                            unfocusedBorderColor = SportProCardBorder,
                            focusedTextColor = SportProTextPrimary,
                            unfocusedTextColor = SportProTextPrimary
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = time,
                        onValueChange = { time = it },
                        label = { Text("Hora", color = SportProTextSecondary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SportProCardBackground,
                            unfocusedContainerColor = SportProCardBackground,
                            focusedBorderColor = SportProGreen,
                            unfocusedBorderColor = SportProCardBorder,
                            focusedTextColor = SportProTextPrimary,
                            unfocusedTextColor = SportProTextPrimary
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = stadium,
                    onValueChange = { stadium = it },
                    label = { Text("Lugar / Estadio", color = SportProTextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SportProCardBackground,
                        unfocusedContainerColor = SportProCardBackground,
                        focusedBorderColor = SportProGreen,
                        unfocusedBorderColor = SportProCardBorder,
                        focusedTextColor = SportProTextPrimary,
                        unfocusedTextColor = SportProTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                Column {
                    Text("Condición", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = SportProTextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isHome) SportProGreen else SportProCardBackground)
                                .border(1.dp, if (isHome) SportProGreen else SportProCardBorder, RoundedCornerShape(12.dp))
                                .clickable { isHome = true }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Local",
                                fontWeight = FontWeight.Bold,
                                color = if (isHome) Color.Black else SportProTextPrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (!isHome) SportProGreen else SportProCardBackground)
                                .border(1.dp, if (!isHome) SportProGreen else SportProCardBorder, RoundedCornerShape(12.dp))
                                .clickable { isHome = false }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Visitante",
                                fontWeight = FontWeight.Bold,
                                color = if (!isHome) Color.Black else SportProTextPrimary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val isFormValid = rival.isNotBlank() && selectedTeamId.isNotBlank()

        Button(
            onClick = {
                if (isFormValid) {
                    viewModel.addMatch(
                        teamId = selectedTeamId,
                        rival = rival,
                        date = date,
                        time = time,
                        stadium = stadium,
                        category = selectedCategory,
                        isHome = isHome
                    )
                    onMatchRegistered()
                }
            },
            enabled = isFormValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SportProGreen,
                contentColor = Color.Black,
                disabledContainerColor = SportProGreen.copy(alpha = 0.3f),
                disabledContentColor = Color.DarkGray
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Registrar partido",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
