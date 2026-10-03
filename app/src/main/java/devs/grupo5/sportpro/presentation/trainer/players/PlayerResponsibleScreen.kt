package devs.grupo5.sportpro.presentation.trainer.players

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import devs.grupo5.sportpro.presentation.trainer.theme.SportProGreenContainer
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextPrimary
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextSecondary

@Composable
fun PlayerResponsibleScreen(
    viewModel: PlayerViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onPlayerRegistered: () -> Unit = {}
) {
    var parentName by remember { mutableStateOf(viewModel.registrationDraft.parentName) }
    var parentRelation by remember { mutableStateOf(viewModel.registrationDraft.parentRelation) }
    var parentPhone by remember { mutableStateOf(viewModel.registrationDraft.parentPhone) }
    var emergencyPhone by remember { mutableStateOf(viewModel.registrationDraft.emergencyPhone) }

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
                    text = "Paso 3 de 3",
                    fontSize = 12.sp,
                    color = SportProGreen,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Responsable y Contacto",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportProTextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = parentName,
            onValueChange = { parentName = it },
            label = { Text("Padre / Apoderado responsable", color = SportProTextSecondary) },
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

        OutlinedTextField(
            value = parentRelation,
            onValueChange = { parentRelation = it },
            label = { Text("Relación con el menor (ej. Padre, Madre)", color = SportProTextSecondary) },
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

        OutlinedTextField(
            value = parentPhone,
            onValueChange = { parentPhone = it },
            label = { Text("Teléfono de contacto", color = SportProTextSecondary) },
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

        OutlinedTextField(
            value = emergencyPhone,
            onValueChange = { emergencyPhone = it },
            label = { Text("Teléfono de emergencia", color = SportProTextSecondary) },
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

        Spacer(modifier = Modifier.height(20.dp))

        // Privacy card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, SportProGreen.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = SportProGreenContainer)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Privacidad",
                    tint = SportProGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Protección de datos: Los datos de contacto y de menores de edad están protegidos de acuerdo con las políticas de privacidad de SportPro.",
                    fontSize = 12.sp,
                    color = SportProTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                viewModel.updateResponsibleData(
                    parentPhone = parentPhone,
                    parentName = parentName,
                    parentRelation = parentRelation,
                    emergencyPhone = emergencyPhone
                )
                viewModel.registerPlayer()
                onPlayerRegistered()
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
                text = "Registrar jugador",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
