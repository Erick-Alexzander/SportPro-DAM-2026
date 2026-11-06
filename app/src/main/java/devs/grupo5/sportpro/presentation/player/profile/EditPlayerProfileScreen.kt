package devs.grupo5.sportpro.presentation.player.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import devs.grupo5.sportpro.data.model.PlayerAttributes
import devs.grupo5.sportpro.data.model.PlayerProfile
import devs.grupo5.sportpro.data.model.PlayerSeasonStats
import devs.grupo5.sportpro.presentation.player.theme.PlayerBackground
import devs.grupo5.sportpro.presentation.player.theme.PlayerBorder
import devs.grupo5.sportpro.presentation.player.theme.PlayerError
import devs.grupo5.sportpro.presentation.player.theme.PlayerGreenContainer
import devs.grupo5.sportpro.presentation.player.theme.PlayerNeonGreen
import devs.grupo5.sportpro.presentation.player.theme.PlayerSurface
import devs.grupo5.sportpro.presentation.player.theme.PlayerSurfaceVariant
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextPrimary
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextSecondary
import devs.grupo5.sportpro.presentation.player.theme.SportProPlayerTheme

@Composable
fun EditPlayerProfileScreen(
    viewModel: PlayerProfileViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onSaved: () -> Unit = {}
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val user = currentUser
    val profile = user?.playerProfile ?: PlayerProfile()

    var firstName by remember(user?.uid, user?.firstName) {
        mutableStateOf(user?.firstName.orEmpty())
    }
    var lastName by remember(user?.uid, user?.lastName) {
        mutableStateOf(user?.lastName.orEmpty())
    }
    var birthDate by remember(user?.uid, user?.birthDate) {
        mutableStateOf(user?.birthDate.orEmpty())
    }

    var position by remember(user?.uid, profile.position) {
        mutableStateOf(profile.position.ifBlank { "MED" })
    }
    var category by remember(user?.uid, profile.category) {
        mutableStateOf(profile.category.ifBlank { "Primera" })
    }
    var dominantFoot by remember(user?.uid, profile.dominantFoot) {
        mutableStateOf(profile.dominantFoot.ifBlank { "Derecho" })
    }
    var status by remember(user?.uid, profile.status) {
        mutableStateOf(profile.status.ifBlank { "Activo" })
    }
    var jerseyNumber by remember(user?.uid, profile.jerseyNumber) {
        mutableStateOf(if (profile.jerseyNumber > 0) profile.jerseyNumber.toString() else "")
    }
    var teamName by remember(user?.uid, profile.teamName) {
        mutableStateOf(profile.teamName)
    }
    var heightCm by remember(user?.uid, profile.heightCm) {
        mutableStateOf(if (profile.heightCm > 0) profile.heightCm.toString() else "")
    }
    var weightKg by remember(user?.uid, profile.weightKg) {
        mutableStateOf(if (profile.weightKg > 0.0) profile.weightKg.toString() else "")
    }
    var size by remember(user?.uid, profile.size) {
        mutableStateOf(profile.size)
    }

    val stats = profile.stats
    var matchesPlayed by remember(user?.uid, stats.matchesPlayed) {
        mutableStateOf(stats.matchesPlayed.toString())
    }
    var goals by remember(user?.uid, stats.goals) {
        mutableStateOf(stats.goals.toString())
    }
    var assists by remember(user?.uid, stats.assists) {
        mutableStateOf(stats.assists.toString())
    }
    var yellowCards by remember(user?.uid, stats.yellowCards) {
        mutableStateOf(stats.yellowCards.toString())
    }
    var redCards by remember(user?.uid, stats.redCards) {
        mutableStateOf(stats.redCards.toString())
    }

    val attributes = profile.attributes
    var speed by remember(user?.uid, attributes.speed) {
        mutableStateOf(attributes.speed)
    }
    var shooting by remember(user?.uid, attributes.shooting) {
        mutableStateOf(attributes.shooting)
    }
    var passing by remember(user?.uid, attributes.passing) {
        mutableStateOf(attributes.passing)
    }
    var defense by remember(user?.uid, attributes.defense) {
        mutableStateOf(attributes.defense)
    }
    var physical by remember(user?.uid, attributes.physical) {
        mutableStateOf(attributes.physical)
    }

    SportProPlayerTheme {
        Scaffold(
            containerColor = PlayerBackground,
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
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
                        text = "Editar perfil",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlayerTextPrimary
                    )
                }
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
                    Text(
                        text = "Datos personales",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlayerTextPrimary
                    )
                }

                item {
                    ProfileTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = "Nombre"
                    )
                }

                item {
                    ProfileTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = "Apellidos"
                    )
                }

                item {
                    ProfileTextField(
                        value = birthDate,
                        onValueChange = { birthDate = it },
                        label = "Fecha de nacimiento (dd/mm/aaaa)"
                    )
                }

                item {
                    SectionCard {
                        Text(
                            text = "Datos deportivos",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PlayerTextPrimary
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        FieldLabel("Categoría")
                        ChipGroup(
                            options = listOf("Primera", "Sub-15", "Sub-10"),
                            selected = category,
                            onSelect = { category = it }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        FieldLabel("Posición")
                        ChipGroup(
                            options = listOf("POR", "DEF", "MED", "DEL"),
                            selected = position,
                            onSelect = { position = it }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        FieldLabel("Pie dominante")
                        ChipGroup(
                            options = listOf("Derecho", "Izquierdo", "Ambidiestro"),
                            selected = dominantFoot,
                            onSelect = { dominantFoot = it }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        FieldLabel("Estado")
                        ChipGroup(
                            options = listOf("Activo", "Lesionado", "Suspendido"),
                            selected = status,
                            onSelect = { status = it }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            ProfileTextField(
                                value = jerseyNumber,
                                onValueChange = { value ->
                                    jerseyNumber = value.filter { it.isDigit() }.take(2)
                                },
                                label = "Dorsal",
                                keyboardType = KeyboardType.Number,
                                modifier = Modifier.weight(1f)
                            )
                            ProfileTextField(
                                value = teamName,
                                onValueChange = { teamName = it },
                                label = "Equipo / academia",
                                modifier = Modifier.weight(1.6f)
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Datos físicos",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlayerTextPrimary
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ProfileTextField(
                            value = heightCm,
                            onValueChange = { value ->
                                heightCm = value.filter { it.isDigit() }.take(3)
                            },
                            label = "Altura (cm)",
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                        ProfileTextField(
                            value = weightKg,
                            onValueChange = { value ->
                                weightKg = value.filter { it.isDigit() || it == '.' || it == ',' }
                            },
                            label = "Peso (kg)",
                            keyboardType = KeyboardType.Decimal,
                            modifier = Modifier.weight(1f)
                        )
                        ProfileTextField(
                            value = size,
                            onValueChange = { size = it.uppercase().take(3) },
                            label = "Talla",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    SectionCard {
                        Text(
                            text = "Estadísticas de temporada",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PlayerTextPrimary
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            NumberField(
                                value = matchesPlayed,
                                onValueChange = { matchesPlayed = it },
                                label = "Partidos",
                                modifier = Modifier.weight(1f)
                            )
                            NumberField(
                                value = goals,
                                onValueChange = { goals = it },
                                label = "Goles",
                                modifier = Modifier.weight(1f)
                            )
                            NumberField(
                                value = assists,
                                onValueChange = { assists = it },
                                label = "Asist.",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            NumberField(
                                value = yellowCards,
                                onValueChange = { yellowCards = it },
                                label = "Amarillas",
                                modifier = Modifier.weight(1f)
                            )
                            NumberField(
                                value = redCards,
                                onValueChange = { redCards = it },
                                label = "Rojas",
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                item {
                    SectionCard {
                        Text(
                            text = "Atributos",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PlayerTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Arrastra para definir cada atributo (0 - 100).",
                            fontSize = 12.sp,
                            color = PlayerTextSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        AttributeSlider("Velocidad", speed) { speed = it }
                        AttributeSlider("Tiro", shooting) { shooting = it }
                        AttributeSlider("Pase", passing) { passing = it }
                        AttributeSlider("Defensa", defense) { defense = it }
                        AttributeSlider("Físico", physical) { physical = it }
                    }
                }

                if (errorMessage != null) {
                    item {
                        Text(
                            text = errorMessage ?: "",
                            fontSize = 13.sp,
                            color = PlayerError,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                item {
                    Button(
                        onClick = {
                            val updatedProfile = PlayerProfile(
                                position = position,
                                jerseyNumber = jerseyNumber.toIntOrNull() ?: 0,
                                category = category,
                                dominantFoot = dominantFoot,
                                status = status,
                                teamName = teamName.trim(),
                                heightCm = heightCm.toIntOrNull() ?: 0,
                                weightKg = weightKg.replace(",", ".").toDoubleOrNull() ?: 0.0,
                                size = size.trim(),
                                stats = PlayerSeasonStats(
                                    matchesPlayed = matchesPlayed.toIntOrNull() ?: 0,
                                    goals = goals.toIntOrNull() ?: 0,
                                    assists = assists.toIntOrNull() ?: 0,
                                    yellowCards = yellowCards.toIntOrNull() ?: 0,
                                    redCards = redCards.toIntOrNull() ?: 0
                                ),
                                attributes = PlayerAttributes(
                                    speed = speed,
                                    shooting = shooting,
                                    passing = passing,
                                    defense = defense,
                                    physical = physical
                                ),
                                privacySettings = profile.privacySettings
                            )
                            viewModel.saveProfile(
                                firstName = firstName,
                                lastName = lastName,
                                birthDate = birthDate,
                                playerProfile = updatedProfile,
                                onSuccess = onSaved
                            )
                        },
                        enabled = !isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PlayerNeonGreen,
                            contentColor = Color(0xFF00210B)
                        )
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFF00210B),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                        Text(
                            text = if (isSaving) "Guardando..." else "Guardar cambios",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun SectionCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, PlayerBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = PlayerSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = PlayerTextSecondary
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun ChipGroup(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) PlayerNeonGreen else PlayerSurfaceVariant)
                    .border(
                        1.dp,
                        if (isSelected) PlayerNeonGreen else PlayerBorder,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onSelect(option) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = option,
                    color = if (isSelected) Color(0xFF00210B) else PlayerTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label, color = PlayerTextSecondary) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Next
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = PlayerSurface,
            unfocusedContainerColor = PlayerSurface,
            focusedBorderColor = PlayerNeonGreen,
            unfocusedBorderColor = PlayerBorder,
            focusedTextColor = PlayerTextPrimary,
            unfocusedTextColor = PlayerTextPrimary,
            cursorColor = PlayerNeonGreen
        ),
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun NumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    ProfileTextField(
        value = value,
        onValueChange = { newValue -> onValueChange(newValue.filter { it.isDigit() }.take(5)) },
        label = label,
        keyboardType = KeyboardType.Number,
        modifier = modifier
    )
}

@Composable
private fun AttributeSlider(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 13.sp, color = PlayerTextSecondary)
            Text(
                text = "$value",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PlayerNeonGreen
            )
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                thumbColor = PlayerNeonGreen,
                activeTrackColor = PlayerNeonGreen,
                inactiveTrackColor = PlayerSurfaceVariant
            )
        )
    }
}
