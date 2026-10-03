package devs.grupo5.sportpro.presentation.trainer.players

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
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBorder
import devs.grupo5.sportpro.presentation.trainer.theme.SportProDarkBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProGreen
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextPrimary
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextSecondary

@Composable
fun PlayerSportDataScreen(
    viewModel: PlayerViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onContinueClick: () -> Unit = {}
) {
    var selectedPosition by remember { mutableStateOf(if (viewModel.registrationDraft.position.isEmpty()) "MED" else viewModel.registrationDraft.position) }
    var selectedFoot by remember { mutableStateOf(viewModel.registrationDraft.dominantFoot) }
    var jerseyNumberText by remember { mutableStateOf(if (viewModel.registrationDraft.jerseyNumber > 0) viewModel.registrationDraft.jerseyNumber.toString() else "10") }
    var sizeText by remember { mutableStateOf(viewModel.registrationDraft.size) }
    var weightText by remember { mutableStateOf(if (viewModel.registrationDraft.weight > 0) viewModel.registrationDraft.weight.toString() else "65.0") }

    val positions = listOf("POR", "DEF", "MED", "DEL")
    val feet = listOf("Derecho", "Izquierdo", "Ambidiestro")

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
            Column {
                Text(
                    text = "Paso 2 de 3",
                    fontSize = 12.sp,
                    color = SportProGreen,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Información Deportiva",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportProTextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Posicion
        Text("Posición", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = SportProTextSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            positions.forEach { pos ->
                val isSelected = selectedPosition == pos
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) SportProGreen else SportProCardBackground)
                        .border(1.dp, if (isSelected) SportProGreen else SportProCardBorder, RoundedCornerShape(20.dp))
                        .clickable { selectedPosition = pos }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = pos,
                        color = if (isSelected) Color.Black else SportProTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pie dominante
        Text("Pie dominante", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = SportProTextSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            feet.forEach { foot ->
                val isSelected = selectedFoot == foot
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) SportProGreen else SportProCardBackground)
                        .border(1.dp, if (isSelected) SportProGreen else SportProCardBorder, RoundedCornerShape(20.dp))
                        .clickable { selectedFoot = foot }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = foot,
                        color = if (isSelected) Color.Black else SportProTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = jerseyNumberText,
            onValueChange = { jerseyNumberText = it },
            label = { Text("Número de camiseta", color = SportProTextSecondary) },
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

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = sizeText,
                onValueChange = { sizeText = it },
                label = { Text("Talla (ej. M)", color = SportProTextSecondary) },
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
                value = weightText,
                onValueChange = { weightText = it },
                label = { Text("Peso (kg)", color = SportProTextSecondary) },
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

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val jerseyNum = jerseyNumberText.toIntOrNull() ?: 0
                val weightVal = weightText.toDoubleOrNull() ?: 0.0
                viewModel.updateSportData(
                    position = selectedPosition,
                    dominantFoot = selectedFoot,
                    jerseyNumber = jerseyNum,
                    size = sizeText,
                    weight = weightVal
                )
                onContinueClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SportProGreen,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Continuar",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
