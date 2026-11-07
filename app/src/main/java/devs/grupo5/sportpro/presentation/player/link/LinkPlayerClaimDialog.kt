package devs.grupo5.sportpro.presentation.player.link

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import devs.grupo5.sportpro.presentation.player.theme.PlayerBackground
import devs.grupo5.sportpro.presentation.player.theme.PlayerBorder
import devs.grupo5.sportpro.presentation.player.theme.PlayerNeonGreen
import devs.grupo5.sportpro.presentation.player.theme.PlayerSurface
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextMuted
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextPrimary
import devs.grupo5.sportpro.presentation.player.theme.PlayerTextSecondary
import devs.grupo5.sportpro.presentation.trainer.theme.SportProError

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkPlayerClaimDialog(
    onDismiss: () -> Unit,
    viewModel: PlayerClaimViewModel = viewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMsg by viewModel.errorMsg.collectAsState()
    val claimSuccess by viewModel.claimSuccess.collectAsState()
    val hasExistingLink by viewModel.hasExistingLink.collectAsState()

    var inviteCodeInput by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = {
            viewModel.clearState()
            onDismiss()
        },
        containerColor = PlayerSurface,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Vincular ficha deportiva",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlayerTextPrimary
                )
                IconButton(onClick = {
                    viewModel.clearState()
                    onDismiss()
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = PlayerTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (hasExistingLink) {
                // El jugador ya está vinculado, no se le permite otra vinculación
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(PlayerNeonGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = PlayerNeonGreen, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Ya estás vinculado",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlayerNeonGreen
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tu cuenta ya posee una ficha oficial de equipo en SportPro.",
                    fontSize = 13.sp,
                    color = PlayerTextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PlayerNeonGreen, contentColor = Color(0xFF00210B))
                ) {
                    Text("Cerrar", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))

            } else if (claimSuccess) {
                // Solicitud encolada con éxito
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(PlayerNeonGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = PlayerNeonGreen, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Solicitud enviada",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlayerNeonGreen
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tu solicitud fue enviada. El Entrenador debe aprobarla para completar la vinculación.",
                    fontSize = 13.sp,
                    color = PlayerTextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        viewModel.clearState()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PlayerNeonGreen, contentColor = Color(0xFF00210B))
                ) {
                    Text("Entendido", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))

            } else {
                // Estado normal - Ingreso de PIN
                Text(
                    text = "Para poder ver tus estadísticas, partidos y entrenamientos, tu entrenador debe generarte un código de acceso único.",
                    fontSize = 13.sp,
                    color = PlayerTextSecondary
                )
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = inviteCodeInput,
                    onValueChange = { inviteCodeInput = it },
                    label = { Text("Código de invitación", color = PlayerTextSecondary) },
                    placeholder = { Text("Ej: X8jLq... (Pegar código completo)", color = PlayerTextMuted, fontSize = 13.sp) },
                    singleLine = true,
                    leadingIcon = { Icon(imageVector = Icons.Default.VpnKey, contentDescription = null, tint = PlayerNeonGreen) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PlayerBackground,
                        unfocusedContainerColor = PlayerBackground,
                        focusedBorderColor = PlayerNeonGreen,
                        unfocusedBorderColor = PlayerBorder,
                        focusedTextColor = PlayerTextPrimary,
                        unfocusedTextColor = PlayerTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (errorMsg != null) {
                    Text(
                        text = errorMsg ?: "",
                        color = SportProError,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                Button(
                    onClick = { viewModel.submitInviteCode(inviteCodeInput) },
                    enabled = !isLoading && inviteCodeInput.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PlayerNeonGreen,
                        contentColor = Color(0xFF00210B),
                        disabledContainerColor = PlayerNeonGreen.copy(alpha = 0.3f),
                        disabledContentColor = Color.DarkGray
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color(0xFF00210B),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Enviar solicitud",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
