package devs.grupo5.sportpro.presentation.player.calendar

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextMuted
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextPrimary
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextSecondary
import devs.grupo5.sportpro.presentation.player.theme.SportProPlayerTheme

private data class SelectableExercise(
    val id: String,
    val title: String,
    val durationMinutes: Int,
    val category: String
)

private val selectableExercises = listOf(
    SelectableExercise(id = "ex1", title = "Rondo 5v2", durationMinutes = 15, category = "Posesión"),
    SelectableExercise(id = "ex2", title = "Juego de posición", durationMinutes = 20, category = "Táctica"),
    SelectableExercise(id = "ex3", title = "Conducción en slalom", durationMinutes = 10, category = "Técnica"),
    SelectableExercise(id = "ex4", title = "Sprints de 20m", durationMinutes = 12, category = "Físico"),
    SelectableExercise(id = "ex5", title = "Pared y continuación", durationMinutes = 10, category = "Técnica"),
    SelectableExercise(id = "ex6", title = "1v1 en espacios reducidos", durationMinutes = 12, category = "Técnica"),
    SelectableExercise(id = "ex7", title = "Velocidad de reacción", durationMinutes = 10, category = "Físico"),
    SelectableExercise(id = "ex8", title = "Partido mini 5v5", durationMinutes = 20, category = "General")
)

private val sessionCategories = listOf("Primera", "Sub-15", "Sub-10")

@Composable
fun CreateSessionScreen(
    onBackClick: () -> Unit = {},
    onNewExerciseClick: () -> Unit = {},
    onCreateSessionClick: () -> Unit = {},
    onTabSelected: (PlayerTab) -> Unit = {}
) {
    var date by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("90") }
    var selectedCategory by remember { mutableStateOf("Primera") }
    var objective by remember { mutableStateOf("") }
    var selectedExerciseIds by remember { mutableStateOf(setOf("ex1", "ex4")) }

    val selectedCount = selectedExerciseIds.size
    val summaryLabel = if (selectedCount == 1) {
        "1 ejercicio seleccionado"
    } else {
        "$selectedCount ejercicios seleccionados"
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = PlayerTextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Nueva sesión",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PlayerTextPrimary
                        )
                    }
                }

                item {
                    FormTextField(
                        label = "FECHA",
                        value = date,
                        onValueChange = { date = it },
                        placeholder = "dd/mm/aaaa",
                        leadingIcon = Icons.Default.CalendarMonth
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FormTextField(
                            label = "HORA INICIO",
                            value = startTime,
                            onValueChange = { startTime = it },
                            placeholder = "--:--",
                            leadingIcon = Icons.Default.Schedule,
                            modifier = Modifier.weight(1f)
                        )
                        FormTextField(
                            label = "DURACIÓN (MIN)",
                            value = duration,
                            onValueChange = { duration = it.filter { char -> char.isDigit() } },
                            placeholder = "90",
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        FormLabel(text = "CATEGORÍA / EQUIPO")
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            sessionCategories.forEach { category ->
                                CategoryChip(
                                    label = category,
                                    isSelected = selectedCategory == category,
                                    onClick = { selectedCategory = category }
                                )
                            }
                        }
                    }
                }

                item {
                    FormTextField(
                        label = "OBJETIVO DE LA SESIÓN",
                        value = objective,
                        onValueChange = { objective = it },
                        placeholder = "Describe el objetivo principal...",
                        minLines = 3,
                        singleLine = false
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ejercicios",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PlayerTextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "+ Nuevo",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PlayerNeonGreen,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onNewExerciseClick() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                items(selectableExercises, key = { it.id }) { exercise ->
                    val isSelected = exercise.id in selectedExerciseIds
                    ExerciseCheckCard(
                        exercise = exercise,
                        isSelected = isSelected,
                        onToggle = {
                            selectedExerciseIds = if (isSelected) {
                                selectedExerciseIds - exercise.id
                            } else {
                                selectedExerciseIds + exercise.id
                            }
                        }
                    )
                }

                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = summaryLabel,
                            fontSize = 13.sp,
                            color = PlayerTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onCreateSessionClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(percent = 50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PlayerNeonGreen,
                                contentColor = Color(0xFF00210B)
                            )
                        ) {
                            Text(
                                text = "Crear sesión",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun FormLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = PlayerNeonGreen
    )
}

@Composable
private fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Column(modifier = modifier) {
        FormLabel(text = label)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    color = PlayerTextMuted,
                    fontSize = 14.sp
                )
            },
            leadingIcon = leadingIcon?.let { icon ->
                {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = PlayerTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            singleLine = singleLine,
            minLines = minLines,
            shape = RoundedCornerShape(14.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = PlayerSurface,
                unfocusedContainerColor = PlayerSurface,
                focusedBorderColor = PlayerNeonGreen,
                unfocusedBorderColor = PlayerBorder,
                focusedTextColor = PlayerTextPrimary,
                unfocusedTextColor = PlayerTextPrimary,
                cursorColor = PlayerNeonGreen
            )
        )
    }
}

@Composable
private fun CategoryChip(
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
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.Black else PlayerTextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ExerciseCheckCard(
    exercise: SelectableExercise,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) PlayerNeonGreen else PlayerBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PlayerGreenContainer else PlayerSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = PlayerNeonGreen,
                    checkmarkColor = Color(0xFF00210B),
                    uncheckedColor = PlayerTextMuted
                )
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "${exercise.title} ${exercise.durationMinutes} min",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = PlayerTextPrimary,
                modifier = Modifier.weight(1f),
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(PlayerGreenContainer)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = exercise.category,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlayerNeonGreen
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF101413)
@Composable
private fun CreateSessionScreenPreview() {
    CreateSessionScreen()
}
