package devs.grupo5.sportpro.presentation.parent.privacy

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight

import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import devs.grupo5.sportpro.data.model.LinkedChild
import devs.grupo5.sportpro.presentation.parent.theme.ParentBackground
import devs.grupo5.sportpro.presentation.parent.theme.ParentBorder
import devs.grupo5.sportpro.presentation.parent.theme.ParentWarning
import devs.grupo5.sportpro.presentation.parent.theme.ParentSurface
import devs.grupo5.sportpro.presentation.parent.theme.ParentSurfaceVariant
import devs.grupo5.sportpro.presentation.parent.theme.ParentTextMuted
import devs.grupo5.sportpro.presentation.parent.theme.ParentTextPrimary
import devs.grupo5.sportpro.presentation.parent.theme.ParentTextSecondary
import devs.grupo5.sportpro.presentation.parent.theme.SportProParentTheme

@Composable
fun ChildrenPrivacyListScreen(
    children: List<LinkedChild>,
    onBackClick: () -> Unit,
    onChildSelected: (String) -> Unit
) {
    SportProParentTheme {
        Scaffold(
            containerColor = ParentBackground
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = ParentTextPrimary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Privacidad de mis hijos",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ParentTextPrimary
                        )
                    }
                }
                
                item {
                    Text(
                        text = "Seleccione un menor vinculado para gestionar sus ajustes de privacidad y datos de contacto.",
                        color = ParentTextSecondary,
                        fontSize = 14.sp
                    )
                }

                items(children) { child ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onChildSelected(child.id) }
                            .border(1.dp, ParentBorder, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = ParentSurface)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(40.dp).clip(CircleShape).background(ParentSurfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = ParentWarning)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = child.fullName, fontWeight = FontWeight.Bold, color = ParentTextPrimary, fontSize = 16.sp)
                                    Text(text = "ID: ${child.id}", color = ParentTextMuted, fontSize = 12.sp)
                                }
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = "Ver", tint = ParentTextSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChildPrivacyDetailScreen(
    child: LinkedChild,
    onBackClick: () -> Unit,
    onSaveClick: (LinkedChild) -> Unit
) {
    var emergencyPhone by remember { mutableStateOf(child.emergencyPhone) }
    var statsVis by remember { mutableStateOf(child.privacySettings.statsVisibility) }
    var photoVis by remember { mutableStateOf(child.privacySettings.profilePhoto) }
    var nameVis by remember { mutableStateOf(child.privacySettings.fullNameVisibility) }
    var showSuccess by remember { mutableStateOf(false) }
    
    val visibilityOptions = listOf("Público", "Restringido", "Solo autorizados")

    SportProParentTheme {
        Scaffold(
            containerColor = ParentBackground
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = ParentTextPrimary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Privacidad: ${child.firstName}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ParentTextPrimary
                        )
                    }
                }
                
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).border(1.dp, ParentBorder, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = ParentSurface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = ParentWarning)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Ajustes protegidos", color = ParentTextPrimary, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Como apoderado, usted administra los datos sensibles de su pupilo. Estos datos no podrán ser hechos públicos por el menor.",
                                fontSize = 13.sp, color = ParentTextSecondary
                            )
                        }
                    }
                }
                
                item {
                    Text("CONTACTO DE EMERGENCIA", color = ParentTextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = emergencyPhone,
                        onValueChange = { emergencyPhone = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ParentWarning,
                            unfocusedBorderColor = ParentBorder,
                            focusedTextColor = ParentTextPrimary,
                            unfocusedTextColor = ParentTextPrimary
                        ),
                        singleLine = true
                    )
                    Text("Solo visible para entrenadores autorizados.", color = ParentTextSecondary, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                }

                item {
                    Text("VISIBILIDAD", color = ParentTextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                
                item {
                    ParentVisibilityPicker(label = "Foto de Perfil", options = visibilityOptions, selected = photoVis, onSelect = { photoVis = it })
                }
                item {
                    ParentVisibilityPicker(label = "Nombre Completo", options = visibilityOptions, selected = nameVis, onSelect = { nameVis = it })
                }
                item {
                    ParentVisibilityPicker(label = "Estadísticas", options = visibilityOptions, selected = statsVis, onSelect = { statsVis = it })
                }
                
                item {
                    Button(
                        onClick = {
                            val updatedChild = child.copy(
                                emergencyPhone = emergencyPhone,
                                privacySettings = child.privacySettings.copy(
                                    profilePhoto = photoVis,
                                    fullNameVisibility = nameVis,
                                    statsVisibility = statsVis
                                )
                            )
                            onSaveClick(updatedChild)
                            showSuccess = true
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ParentWarning, contentColor = Color.White)
                    ) {
                        Text("Guardar Cambios", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
                
                if (showSuccess) {
                    item {
                        Text("Configuración de privacidad guardada exitosamente.", color = ParentWarning, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ParentVisibilityPicker(label: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).border(1.dp, ParentBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = ParentSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(label, fontWeight = FontWeight.Bold, color = ParentTextPrimary, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                options.forEach { option ->
                    val isSelected = option == selected
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) ParentWarning else ParentSurfaceVariant)
                            .clickable { onSelect(option) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = option,
                            color = if (isSelected) Color.White else ParentTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
