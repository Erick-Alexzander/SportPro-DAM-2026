package devs.grupo5.sportpro.presentation.trainer.training

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import devs.grupo5.sportpro.data.model.Exercise
import devs.grupo5.sportpro.data.model.Team
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBorder
import devs.grupo5.sportpro.presentation.trainer.theme.SportProDarkBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProError
import devs.grupo5.sportpro.presentation.trainer.theme.SportProGreen
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextMuted
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextPrimary
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextSecondary

@Composable
fun CreateTrainingScreen(
    viewModel: TrainingViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onSessionCreated: () -> Unit = {}
) {
    val teams: List<Team> by viewModel.teams.collectAsState()

    var selectedTeamId by remember { mutableStateOf(teams.firstOrNull()?.id ?: "") }
    var selectedCategory by remember { mutableStateOf(teams.firstOrNull()?.category ?: "Primera") }

    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("Hoy") }
    var startTime by remember { mutableStateOf("18:30") }
    var durationText by remember { mutableStateOf("90") }
    var objective by remember { mutableStateOf("") }

    // Exercises
    var newExerciseName by remember { mutableStateOf("") }
    var newExerciseDuration by remember { mutableStateOf("20") }
    var newExerciseType by remember { mutableStateOf("Táctico") }

    val exercisesList = remember {
        mutableStateListOf(
            Exercise("e1", "Rondo de calentamiento", 15, "Técnico")
        )
    }

    val exerciseTypes = listOf("Táctico", "Físico", "Técnico")

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
                text = "Nueva Sesión de Entrenamiento",
                fontSize = 20.sp,
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
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título de la sesión", color = SportProTextSecondary) },
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
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Hora inicio", color = SportProTextSecondary) },
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
                    value = durationText,
                    onValueChange = { durationText = it },
                    label = { Text("Duración total (minutos)", color = SportProTextSecondary) },
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
                OutlinedTextField(
                    value = objective,
                    onValueChange = { objective = it },
                    label = { Text("Objetivo de la sesión", color = SportProTextSecondary) },
                    minLines = 2,
                    maxLines = 3,
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

            // Ejercicios section
            item {
                Text(
                    text = "Ejercicios de la Sesión",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportProTextPrimary
                )
            }

            items(exercisesList) { exercise ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SportProCardBackground)
                        .border(1.dp, SportProCardBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(exercise.name, fontWeight = FontWeight.Bold, color = SportProTextPrimary)
                        Text("${exercise.durationMinutes} min • Tipo: ${exercise.type}", fontSize = 12.sp, color = SportProTextSecondary)
                    }
                    IconButton(onClick = { exercisesList.remove(exercise) }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = SportProError)
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SportProCardBackground)
                        .border(1.dp, SportProCardBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text("Agregar Ejercicio", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SportProGreen)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newExerciseName,
                        onValueChange = { newExerciseName = it },
                        label = { Text("Nombre del ejercicio", color = SportProTextSecondary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newExerciseDuration,
                            onValueChange = { newExerciseDuration = it },
                            label = { Text("Minutos", color = SportProTextSecondary) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Row(modifier = Modifier.weight(2f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            exerciseTypes.forEach { type ->
                                val isSelected = newExerciseType == type
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSelected) SportProGreen else SportProDarkBackground)
                                        .clickable { newExerciseType = type }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(type, fontSize = 11.sp, color = if (isSelected) Color.Black else SportProTextPrimary)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (newExerciseName.isNotBlank()) {
                                exercisesList.add(
                                    Exercise(
                                        id = "ex_${System.currentTimeMillis()}",
                                        name = newExerciseName,
                                        durationMinutes = newExerciseDuration.toIntOrNull() ?: 15,
                                        type = newExerciseType
                                    )
                                )
                                newExerciseName = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SportProGreen, contentColor = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Añadir ejercicio")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val isFormValid = title.isNotBlank() && selectedTeamId.isNotBlank()

        Button(
            onClick = {
                if (isFormValid) {
                    viewModel.createTraining(
                        teamId = selectedTeamId,
                        title = title,
                        date = date,
                        startTime = startTime,
                        durationMinutes = durationText.toIntOrNull() ?: 90,
                        category = selectedCategory,
                        objective = objective,
                        exercises = exercisesList.toList()
                    )
                    onSessionCreated()
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
                text = "Crear entrenamiento",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
