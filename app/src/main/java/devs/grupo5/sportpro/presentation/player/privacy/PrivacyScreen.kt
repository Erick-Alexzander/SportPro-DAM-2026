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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
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

private data class PrivacyField(
    val label: String,
    val defaultOption: String
)

private val privacyFields = listOf(
    PrivacyField(label = "Foto De Perfil", defaultOption = "Público"),
    PrivacyField(label = "Nombre Completo", defaultOption = "Público"),
    PrivacyField(label = "Estadísticas", defaultOption = "Restringido"),
    PrivacyField(label = "Contacto", defaultOption = "Solo autorizados"),
    PrivacyField(label = "Fecha Nac.", defaultOption = "Solo autorizados")
)

private fun optionColor(option: String): Color = when (option) {
    "Público" -> PlayerNeonGreen
    "Restringido" -> PlayerWarning
    else -> PlayerBlueLight
}

@Composable
fun PrivacyScreen(
    onBackClick: () -> Unit = {},
    onSaveClick: (Map<String, String>) -> Unit = {},
    onTabSelected: (PlayerTab) -> Unit = {}
) {
    val selections = remember {
        mutableStateMapOf<String, String>().apply {
            privacyFields.forEach { field -> put(field.label, field.defaultOption) }
        }
    }

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
                    DataProtectionCard()
                }

                items(privacyFields.size) { index ->
                    val field = privacyFields[index]
                    VisibilityFieldCard(
                        field = field,
                        selectedOption = selections[field.label] ?: field.defaultOption,
                        onOptionSelected = { option -> selections[field.label] = option }
                    )
                }

                item {
                    Button(
                        onClick = { onSaveClick(selections.toMap()) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 24.dp)
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
private fun DataProtectionCard() {
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

            Text(
                text = "Los datos de menores de edad tienen protección especial. Los campos marcados como " +
                    "'Solo autorizados' solo pueden ser vistos por el administrador y el apoderado registrado.",
                fontSize = 13.sp,
                color = PlayerTextSecondary,
                lineHeight = 19.sp
            )
        }
    }
}

@Composable
private fun VisibilityFieldCard(
    field: PrivacyField,
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
                text = field.label,
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
