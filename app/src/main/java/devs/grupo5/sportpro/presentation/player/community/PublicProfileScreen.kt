package devs.grupo5.sportpro.presentation.player.community

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import devs.grupo5.sportpro.presentation.player.theme.*
import devs.grupo5.sportpro.data.repository.MockPlayerRepository

@Composable
fun PublicProfileScreen(
    uid: String,
    onBackClick: () -> Unit
) {
    // Simulamos la obtención del perfil
    // En integración real, este UID buscará el Player en Firestore
    // Aquí filtramos el MockPlayerRepository
    val player = MockPlayerRepository.instance.players.collectAsState().value.find { it.id == uid }

    SportProPlayerTheme {
        Scaffold(
            containerColor = PlayerBackground,
            topBar = {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = PlayerTextPrimary)
                    }
                    Text("Perfil", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PlayerTextPrimary)
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                if (player == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Perfil no disponible", color = PlayerTextMuted, fontSize = 16.sp)
                    }
                } else {
                    val settings = player.privacySettings
                    val isMinor = player.age < 18
                    
                    // Header
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(PlayerSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            if (settings.profilePhoto == "Público") {
                                // Aquí iría la carga de imagen real, usamos ícono de fallback
                                Icon(Icons.Default.Person, contentDescription = null, tint = PlayerNeonGreen, modifier = Modifier.size(45.dp))
                            } else {
                                Icon(Icons.Default.Person, contentDescription = null, tint = PlayerTextMuted, modifier = Modifier.size(45.dp))
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        if (settings.fullNameVisibility == "Público") {
                            Text(player.fullName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PlayerTextPrimary)
                        } else {
                            Text(player.fullName.split(" ").firstOrNull() ?: "Deportista", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PlayerTextPrimary)
                        }
                        
                        if (isMinor) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PlayerSurfaceVariant)
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = PlayerNeonGreen, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Cuenta Protegida", color = PlayerNeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Sport Data
                    Card(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).border(1.dp, PlayerBorder, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = PlayerSurface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Información Deportiva", fontWeight = FontWeight.Bold, color = PlayerTextPrimary, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Equipo", color = PlayerTextSecondary, fontSize = 12.sp)
                                    Text(player.category, color = PlayerTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Column {
                                    Text("Posición", color = PlayerTextSecondary, fontSize = 12.sp)
                                    Text(player.position, color = PlayerTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Column {
                                    Text("Dorsal", color = PlayerTextSecondary, fontSize = 12.sp)
                                    Text("#${player.jerseyNumber}", color = PlayerNeonGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Stats Data
                    if (settings.statsVisibility == "Público") {
                        Card(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).border(1.dp, PlayerBorder, RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = PlayerSurface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Estadísticas de Temporada", fontWeight = FontWeight.Bold, color = PlayerTextPrimary, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    StatItem("Partidos", "14")
                                    StatItem("Goles", "8")
                                    StatItem("Asist.", "3")
                                }
                            }
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).border(1.dp, PlayerBorder, RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = PlayerSurfaceVariant)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = PlayerTextMuted, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("El jugador mantiene sus estadísticas privadas.", color = PlayerTextMuted, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = PlayerNeonGreen, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        Text(label, color = PlayerTextSecondary, fontSize = 12.sp)
    }
}
