package devs.grupo5.sportpro.presentation.trainer.players.link

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import devs.grupo5.sportpro.data.model.Player
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProCardBorder
import devs.grupo5.sportpro.presentation.trainer.theme.SportProDarkBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProError
import devs.grupo5.sportpro.presentation.trainer.theme.SportProGreen
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextMuted
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextPrimary
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingClaimsDialog(
    teamPlayers: List<Player>,
    onDismiss: () -> Unit,
    viewModel: PendingClaimsViewModel = viewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMsg by viewModel.errorMsg.collectAsState()
    val successMsg by viewModel.successMsg.collectAsState()
    val claims by viewModel.pendingClaims.collectAsState()

    var claimToApprove by remember { mutableStateOf<PendingClaimItem?>(null) }

    LaunchedEffect(teamPlayers) {
        viewModel.loadPendingClaims(teamPlayers)
    }

    ModalBottomSheet(
        onDismissRequest = {
            viewModel.clearMessages()
            onDismiss()
        },
        containerColor = SportProCardBackground,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Solicitudes pendientes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SportProTextPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.loadPendingClaims(teamPlayers) }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Actualizar", tint = SportProGreen)
                    }
                    IconButton(onClick = {
                        viewModel.clearMessages()
                        onDismiss()
                    }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar", tint = SportProTextMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (errorMsg != null) {
                Text(
                    text = errorMsg ?: "",
                    color = SportProError,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (successMsg != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SportProGreen.copy(alpha = 0.2f))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = successMsg ?: "",
                        color = SportProGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (isLoading && claims.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SportProGreen)
                }
            } else if (claims.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay solicitudes de vinculación pendientes",
                        color = SportProTextMuted,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(claims, key = { it.claim.userUid + it.inviteId }) { item ->
                        androidx.compose.material3.Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, SportProCardBorder, RoundedCornerShape(12.dp)),
                            colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = SportProDarkBackground)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.playerName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SportProTextPrimary
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(SportProGreen.copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = item.claim.status,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SportProGreen
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Cuenta ID: ${item.claim.userUid.take(8)}...",
                                    fontSize = 12.sp,
                                    color = SportProTextSecondary
                                )

                                val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(item.claim.createdAt))
                                Text(
                                    text = "Fecha: $dateStr",
                                    fontSize = 11.sp,
                                    color = SportProTextMuted
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = { claimToApprove = item },
                                    enabled = !isLoading,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(38.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SportProGreen,
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Aprobar vinculación", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Confirmation Dialog before Approving
    if (claimToApprove != null) {
        val item = claimToApprove!!
        AlertDialog(
            onDismissRequest = { claimToApprove = null },
            containerColor = SportProCardBackground,
            title = {
                Text(
                    text = "Confirmar vinculación",
                    color = SportProTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "¿Confirmas que esta cuenta pertenece al jugador '${item.playerName}'? Una vez aprobada, su ficha deportiva quedará vinculada a esa cuenta.",
                    color = SportProTextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        claimToApprove = null
                        viewModel.approveClaim(item.inviteId, item.claim.userUid, teamPlayers)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SportProGreen, contentColor = Color.Black)
                ) {
                    Text("Sí, aprobar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { claimToApprove = null }) {
                    Text("Cancelar", color = SportProTextSecondary)
                }
            }
        )
    }
}
