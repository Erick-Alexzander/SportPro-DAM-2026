package devs.grupo5.sportpro.presentation.player.library

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import devs.grupo5.sportpro.presentation.player.components.PlayerBottomNavigationBar
import devs.grupo5.sportpro.presentation.player.components.PlayerTab
import devs.grupo5.sportpro.presentation.player.theme.PlayerBackground
import devs.grupo5.sportpro.presentation.player.theme.PlayerBorder
import devs.grupo5.sportpro.presentation.player.theme.PlayerGreenContainer
import devs.grupo5.sportpro.presentation.player.theme.PlayerNeonGreen
import devs.grupo5.sportpro.presentation.player.theme.PlayerSurface
import devs.grupo5.sportpro.presentation.player.theme.PlayerSurfaceVariant
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextMuted
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextPrimary
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextSecondary
import devs.grupo5.sportpro.presentation.player.theme.SportProPlayerTheme

private data class LibraryExercise(
    val emoji: String,
    val title: String,
    val category: String,
    val description: String,
    val durationMinutes: Int
)

private val exerciseFilters = listOf("Todos", "Posesión", "Táctica", "Técnica", "Físico")

private val libraryExercises = listOf(
    LibraryExercise(
        emoji = "🔵",
        title = "Rondo 5v2",
        category = "Posesión",
        description = "Cuadrado de 10×10m, 5 vs 2. Máximo 2 toques.",
        durationMinutes = 15
    ),
    LibraryExercise(
        emoji = "♟️",
        title = "Juego de posición",
        category = "Táctica",
        description = "Partidos reducidos con zonas y comodines.",
        durationMinutes = 20
    ),
    LibraryExercise(
        emoji = "⚽",
        title = "Conducción en slalom",
        category = "Técnica",
        description = "Conducir el balón en zigzag por conos separados 1m.",
        durationMinutes = 10
    ),
    LibraryExercise(
        emoji = "💪",
        title = "Sprints de 20m",
        category = "Físico",
        description = "Series de aceleración: 6×20m con 45s de pausa.",
        durationMinutes = 12
    ),
    LibraryExercise(
        emoji = "⚽",
        title = "Pared y continuación",
        category = "Técnica",
        description = "Trabajo en parejas: pase, devolución y continuación.",
        durationMinutes = 10
    ),
    LibraryExercise(
        emoji = "🏆",
        title = "1v1 en espacios reducidos",
        category = "Competitivo",
        description = "Duelo uno contra uno en zona de 6×10m.",
        durationMinutes = 15
    ),
    LibraryExercise(
        emoji = "💪",
        title = "Velocidad de reacción",
        category = "Físico",
        description = "Reacción a señal visual/auditiva y sprint corto.",
        durationMinutes = 12
    ),
    LibraryExercise(
        emoji = "🏆",
        title = "Partido mini 5v5",
        category = "Competitivo",
        description = "Partido condicionado en campo reducido.",
        durationMinutes = 20
    )
)

@Composable
fun ExerciseLibraryScreen(
    onBackClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onExerciseClick: (String) -> Unit = {},
    onTabSelected: (PlayerTab) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("Todos") }

    val filteredExercises = if (selectedFilter == "Todos") {
        libraryExercises
    } else {
        libraryExercises.filter { it.category == selectedFilter }
    }

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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // TopAppBar / Barra superior
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = PlayerTextPrimary
                        )
                    }
                    Text(
                        text = "Biblioteca de ejercicios",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlayerTextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onAddClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PlayerNeonGreen)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar ejercicio",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Filtros por tipo de ejercicio
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(exerciseFilters) { filter ->
                        LibraryFilterChip(
                            label = filter,
                            isSelected = selectedFilter == filter,
                            onClick = { selectedFilter = filter }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de ejercicios
                if (filteredExercises.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay ejercicios para este tipo.",
                            color = PlayerTextMuted,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredExercises, key = { it.title }) { exercise ->
                            ExerciseLibraryCard(
                                exercise = exercise,
                                onClick = { onExerciseClick(exercise.title) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(8.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun LibraryFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) PlayerNeonGreen else PlayerSurface)
            .border(
                1.dp,
                if (isSelected) PlayerNeonGreen else PlayerBorder,
                RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.Black else PlayerTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ExerciseLibraryCard(
    exercise: LibraryExercise,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, PlayerBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = PlayerSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono/Emoji en fondo circular oscuro
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(PlayerSurfaceVariant)
                    .border(1.dp, PlayerBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = exercise.emoji,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = exercise.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlayerTextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    CategoryChip(category = exercise.category)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = exercise.description,
                    fontSize = 13.sp,
                    color = PlayerTextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = PlayerNeonGreen,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "${exercise.durationMinutes} min",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PlayerNeonGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(category: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(PlayerGreenContainer)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = category,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = PlayerNeonGreen
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF101413)
@Composable
private fun ExerciseLibraryScreenPreview() {
    ExerciseLibraryScreen()
}
