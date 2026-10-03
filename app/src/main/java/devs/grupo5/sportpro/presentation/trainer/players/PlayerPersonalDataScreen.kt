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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Person
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
import devs.grupo5.sportpro.presentation.trainer.theme.SportProGreenContainer
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextMuted
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextPrimary
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextSecondary
import devs.grupo5.sportpro.presentation.trainer.theme.SportProWarning

@Composable
fun PlayerPersonalDataScreen(
    viewModel: PlayerViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onContinueClick: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf(viewModel.registrationDraft.fullName) }
    var dateOfBirth by remember { mutableStateOf(viewModel.registrationDraft.dateOfBirth) }
    var contactEmail by remember { mutableStateOf(viewModel.registrationDraft.contactEmail) }
    var selectedCategory by remember { mutableStateOf(if (viewModel.registrationDraft.category.isEmpty()) "Primera" else viewModel.registrationDraft.category) }

    val categories = listOf("Primera", "Sub-15", "Sub-10")

    val calculatedAge = viewModel.calculateAge(dateOfBirth)
    val isMinor = calculatedAge < 18
    val isValid = fullName.isNotBlank() && dateOfBirth.isNotBlank()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SportProDarkBackground)
            .padding(16.dp)
    ) {
        // Top Bar & Progress
        item {
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
                        text = "Paso 1 de 3",
                        fontSize = 12.sp,
                        color = SportProGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Datos Personales",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportProTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Photo Avatar Placeholder
        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(90.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(SportProGreenContainer)
                            .border(2.dp, SportProGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = SportProGreen,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(SportProDarkBackground)
                            .border(1.dp, SportProCardBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = "Agregar foto",
                            tint = SportProGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Nombre Completo
        item {
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Nombre completo *", color = SportProTextSecondary) },
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

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Fecha de Nacimiento
        item {
            OutlinedTextField(
                value = dateOfBirth,
                onValueChange = { dateOfBirth = it },
                label = { Text("Fecha de nacimiento * (DD/MM/AAAA)", color = SportProTextSecondary) },
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

            // Dynamic Age Calculation Chip
            if (dateOfBirth.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isMinor) SportProWarning.copy(alpha = 0.2f) else SportProGreenContainer)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isMinor)
                            "Edad calculada: $calculatedAge años (Menor de edad — Requiere Apoderado)"
                        else
                            "Edad calculada: $calculatedAge años (Mayor de edad — Apoderado no exigido)",
                        color = if (isMinor) SportProWarning else SportProGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Correo / contacto
        item {
            OutlinedTextField(
                value = contactEmail,
                onValueChange = { contactEmail = it },
                label = { Text("Correo o teléfono de contacto", color = SportProTextSecondary) },
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
        }

        // Categoría
        item {
            Text(
                text = "Categoría *",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = SportProTextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.forEach { category ->
                    val isSelected = selectedCategory == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) SportProGreen else SportProCardBackground)
                            .border(1.dp, if (isSelected) SportProGreen else SportProCardBorder, RoundedCornerShape(20.dp))
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = category,
                            color = if (isSelected) Color.Black else SportProTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Button Continuar
        item {
            Button(
                onClick = {
                    if (isValid) {
                        viewModel.updatePersonalData(
                            fullName = fullName,
                            dateOfBirth = dateOfBirth,
                            contactEmail = contactEmail,
                            category = selectedCategory
                        )
                        onContinueClick()
                    }
                },
                enabled = isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SportProGreen,
                    contentColor = Color.Black,
                    disabledContainerColor = SportProCardBorder,
                    disabledContentColor = SportProTextMuted
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Continuar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
