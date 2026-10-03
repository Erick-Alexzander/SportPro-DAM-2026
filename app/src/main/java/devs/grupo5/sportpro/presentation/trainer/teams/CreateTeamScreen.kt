package devs.grupo5.sportpro.presentation.trainer.teams

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import devs.grupo5.sportpro.data.model.Player
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBorder
import devs.grupo5.sportpro.presentation.trainer.theme.SportProDarkBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProGreen
import devs.grupo5.sportpro.presentation.trainer.theme.SportProGreenContainer
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextMuted
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextPrimary
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextSecondary

@Composable
fun CreateTeamScreen(
    viewModel: TeamViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onTeamCreated: () -> Unit = {}
) {
    val availablePlayers by viewModel.availablePlayers.collectAsState()

    var name by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Primera") }
    var description by remember { mutableStateOf("") }
    val selectedPlayerIds = remember { mutableStateListOf<String>() }

    val categories = listOf("Primera", "Sub-15", "Sub-10")

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
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = SportProTextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Nuevo equipo",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = SportProTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del equipo", color = SportProTextSecondary) },
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
                    Text(
                        text = "Categoría",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SportProTextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { category ->
                            val isSelected = selectedCategory == category
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSelected) SportProGreen else SportProCardBackground)
                                    .border(
                                        1.dp,
                                        if (isSelected) SportProGreen else SportProCardBorder,
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable { selectedCategory = category }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = category,
                                    color = if (isSelected) Color.Black else SportProTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción del equipo", color = SportProTextSecondary) },
                    minLines = 3,
                    maxLines = 4,
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Seleccionar Jugadores",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportProTextPrimary
                    )
                    Text(
                        text = "${selectedPlayerIds.size} seleccionados",
                        fontSize = 13.sp,
                        color = SportProGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Filter players by selected category if desired, or show all with checkbox
            val filteredPlayers = availablePlayers.filter { it.category == selectedCategory || selectedCategory.isEmpty() }

            items(filteredPlayers) { player ->
                val isSelected = selectedPlayerIds.contains(player.id)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SportProCardBackground)
                        .border(1.dp, SportProCardBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            if (isSelected) selectedPlayerIds.remove(player.id)
                            else selectedPlayerIds.add(player.id)
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = player.fullName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = SportProTextPrimary
                        )
                        Text(
                            text = "${player.position} • Camiseta #${player.jerseyNumber} • ${player.category}",
                            fontSize = 12.sp,
                            color = SportProTextSecondary
                        )
                    }

                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { checked ->
                            if (checked) selectedPlayerIds.add(player.id)
                            else selectedPlayerIds.remove(player.id)
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
                if (name.isNotBlank()) {
                    viewModel.createTeam(
                        name = name,
                        category = selectedCategory,
                        description = description,
                        selectedPlayerIds = selectedPlayerIds.toList()
                    )
                    onTeamCreated()
                }
            },
            enabled = name.isNotBlank(),
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
                text = "Crear equipo",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
