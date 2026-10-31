package devs.grupo5.sportpro.presentation.player.privacy

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import devs.grupo5.sportpro.data.model.Player
import devs.grupo5.sportpro.data.model.PrivacySettings
import devs.grupo5.sportpro.data.repository.MockPlayerRepository
import devs.grupo5.sportpro.data.repository.PlayerRepository
import devs.grupo5.sportpro.presentation.player.components.PlayerBottomNavigationBar
import devs.grupo5.sportpro.presentation.player.components.PlayerTab
import devs.grupo5.sportpro.presentation.player.theme.PlayerBackground
import devs.grupo5.sportpro.presentation.player.theme.PlayerBlue
import devs.grupo5.sportpro.presentation.player.theme.PlayerBlueLight
import devs.grupo5.sportpro.presentation.player.theme.PlayerBorder
import devs.grupo5.sportpro.presentation.player.theme.PlayerNeonGreen
import devs.grupo5.sportpro.presentation.player.theme.PlayerSurface
import devs.grupo5.sportpro.presentation.player.theme.PlayerSurfaceVariant
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextPrimary
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextSecondary
import devs.grupo5.sportpro.presentation.player.theme.PlayerWarning
import devs.grupo5.sportpro.presentation.player.theme.SportProPlayerTheme

private val visibilityOptions = listOf("Público", "Restringido", "Solo autorizados")



private fun optionColor(option: String): Color = when (option) {
    "Público" -> PlayerNeonGreen
    "Restringido" -> PlayerWarning
    else -> PlayerBlueLight
}

class PrivacyViewModel(
    private val playerRepository: PlayerRepository = MockPlayerRepository.instance
) : ViewModel() {
    fun getPlayer(playerId: String): Player? = playerRepository.getPlayerById(playerId)
    
    fun calculateAge(birthDateStr: String): Int {
        if (birthDateStr.isBlank()) return -1
        return try {
            val parts = birthDateStr.split("/", "-", ".").mapNotNull { it.trim().toIntOrNull() }
            if (parts.size == 3) {
                val (day, month, year) = if (parts[0] > 1000) Triple(parts[2], parts[1], parts[0]) else Triple(parts[0], parts[1], parts[2])
                val today = java.time.LocalDate.now()
                val birthDate = java.time.LocalDate.of(year, month, day)
                var age = today.year - birthDate.year
                if (today.monthValue < birthDate.monthValue || (today.monthValue == birthDate.monthValue && today.dayOfMonth < birthDate.dayOfMonth)) {
                    age--
                }
                age.coerceAtLeast(0)
            } else -1
        } catch (_: Exception) {
            -1
        }
    }
    
    fun savePrivacySettings(playerId: String, settings: PrivacySettings) {
        val player = getPlayer(playerId) ?: return
        playerRepository.updatePlayer(player.copy(privacySettings = settings))
    }
}

@Composable
fun PrivacyScreen(
    playerId: String = "p1", // Fallback to mock ID if not provided. In a real app this comes from Auth.
    viewModel: PrivacyViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onTabSelected: (PlayerTab) -> Unit = {}
) {
    val player = viewModel.getPlayer(playerId)
    val age = player?.let { viewModel.calculateAge(it.dateOfBirth) } ?: -1
    val isMinor = age < 18

    // For minors, everything sensitive is "Solo autorizados", and others may be what they are. 
    // Wait, the requirement says minor cannot change sensitive data, and even for non-sensitive data, guardian manages it?
    // "Para menores, las preferencias deben ser administradas por el responsable vinculado y nunca pueden superar las restricciones obligatorias del sistema... Mostrar un candado."
    
    // We will initialize state from player's actual settings:
    val initialSettings = player?.privacySettings ?: PrivacySettings()
    
    val profilePhotoOpt = remember(initialSettings) { mutableStateOf(if (isMinor) "Solo autorizados" else initialSettings.profilePhoto) }
    val fullNameOpt = remember(initialSettings) { mutableStateOf(if (isMinor) "Solo autorizados" else initialSettings.fullNameVisibility) }
    val statsOpt = remember(initialSettings) { mutableStateOf(if (isMinor) "Solo autorizados" else initialSettings.statsVisibility) }
    
    var showSuccess by remember { mutableStateOf(false) }

    SportProPlayerTheme {
        Scaffold(
            containerColor = PlayerBackground,
            bottomBar = {
                PlayerBottomNavigationBar(
                    currentTab = PlayerTab.PROFILE,
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
                    PrivacyTopBar(onBackClick = onBackClick)
                }

                item {
                    DataProtectionCard(isMinor = isMinor)
                }
                
                // Sensitive fields: Contacts and BirthDate are ALWAYS restricted, show as locked cards.
                item {
                    LockedFieldCard(label = "Contacto y Emergencia", value = "Solo autorizados (Protegido)")
                }
                item {
                    LockedFieldCard(label = "Fecha de Nacimiento", value = "Solo autorizados (Protegido)")
                }

                if (isMinor) {
                    // Minors cannot edit anything
                    item { LockedFieldCard(label = "Foto de Perfil", value = "Gestionado por tu apoderado") }
                    item { LockedFieldCard(label = "Nombre Completo", value = "Gestionado por tu apoderado") }
                    item { LockedFieldCard(label = "Estadísticas", value = "Gestionado por tu apoderado") }
                } else {
                    item {
                        VisibilityFieldCard(
                            label = "Foto De Perfil",
                            selectedOption = profilePhotoOpt.value,
                            onOptionSelected = { profilePhotoOpt.value = it }
                        )
                    }
                    item {
                        VisibilityFieldCard(
                            label = "Nombre Completo",
                            selectedOption = fullNameOpt.value,
                            onOptionSelected = { fullNameOpt.value = it }
                        )
                    }
                    item {
                        VisibilityFieldCard(
                            label = "Estadísticas",
                            selectedOption = statsOpt.value,
                            onOptionSelected = { statsOpt.value = it }
                        )
                    }
                }

                if (!isMinor) {
                    item {
                        Button(
                            onClick = {
                                viewModel.savePrivacySettings(
                                    playerId = playerId, 
                                    settings = PrivacySettings(
                                        profilePhoto = profilePhotoOpt.value,
                                        fullNameVisibility = fullNameOpt.value,
                                        statsVisibility = statsOpt.value
                                    )
                                )
                                showSuccess = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 8.dp)
                                .height(54.dp),
                            shape = RoundedCornerShape(percent = 50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PlayerNeonGreen,
                                contentColor = Color(0xFF00210B)
                            )
                        ) {
                            Text(
                                text = "Guardar configuración",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00210B)
                            )
                        }
                    }
                }
                
                if (showSuccess && !isMinor) {
                    item {
                        Text("Configuración guardada exitosamente.", color = PlayerNeonGreen, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PrivacyTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
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
        Column {
            Text(
                text = "Privacidad",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PlayerTextPrimary
            )
            Text(
                text = "Configuración de datos",
                fontSize = 12.sp,
                color = PlayerTextSecondary
            )
        }
    }
}

@Composable
private fun DataProtectionCard(isMinor: Boolean) {
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(PlayerBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = PlayerBlueLight,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Protección de datos",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlayerNeonGreen
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isMinor) {
                Text(
                    text = "Tu cuenta está protegida por políticas para menores de edad. " +
                        "Tus datos sensibles están restringidos automáticamente. " +
                        "Tu padre o apoderado administra la privacidad de tus datos personales y de emergencia. " +
                        "Si necesitas actualizar esta información, comunícate con tu responsable.",
                    fontSize = 13.sp,
                    color = PlayerTextSecondary,
                    lineHeight = 19.sp
                )
            } else {
                Text(
                    text = "Los datos de contacto y emergencia están siempre protegidos. " +
                        "Puedes gestionar la visibilidad de tu foto, nombre y estadísticas deportivas.",
                    fontSize = 13.sp,
                    color = PlayerTextSecondary,
                    lineHeight = 19.sp
                )
            }
        }
    }
}

@Composable
private fun LockedFieldCard(label: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, PlayerBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = PlayerSurfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = label, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PlayerTextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = value, fontSize = 12.sp, color = PlayerWarning)
            }
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Bloqueado",
                tint = PlayerTextSecondary
            )
        }
    }
}

@Composable
private fun VisibilityFieldCard(
    label: String,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, PlayerBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = PlayerSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = PlayerTextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                visibilityOptions.forEach { option ->
                    val isSelected = option == selectedOption
                    val accent = optionColor(option)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) accent else PlayerSurfaceVariant)
                            .border(
                                1.dp,
                                if (isSelected) accent else PlayerBorder,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { onOptionSelected(option) }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = option,
                            color = if (isSelected) Color(0xFF00210B) else PlayerTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF101413)
@Composable
private fun PrivacyScreenPreview() {
    PrivacyScreen()
}
