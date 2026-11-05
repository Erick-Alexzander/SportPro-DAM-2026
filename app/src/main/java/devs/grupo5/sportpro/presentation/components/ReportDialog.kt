package devs.grupo5.sportpro.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import devs.grupo5.sportpro.data.model.ContentReport
import devs.grupo5.sportpro.data.model.ReportOperationResult
import devs.grupo5.sportpro.data.model.ReportReason
import devs.grupo5.sportpro.data.model.ReportTargetType
import devs.grupo5.sportpro.data.repository.MockReportRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ReportDialog(
    targetType: ReportTargetType,
    targetId: String,
    parentPostId: String? = null,
    reporterUid: String?,
    onDismiss: () -> Unit
) {
    var selectedReason by remember { mutableStateOf<ReportReason?>(null) }
    var description by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val reportRepository = MockReportRepository.instance

    val title = when (targetType) {
        ReportTargetType.PUBLICATION -> "Reportar publicación"
        ReportTargetType.COMMENT -> "Reportar comentario"
        ReportTargetType.TRYOUT -> "Reportar convocatoria"
    }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        containerColor = Color(0xFF1B231F),
        titleContentColor = Color.White,
        textContentColor = Color.LightGray,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        },
        text = {
            if (resultMessage != null) {
                Text(
                    text = resultMessage!!,
                    color = if (isSuccess) Color(0xFF00E676) else Color(0xFFFF9100),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            } else if (reporterUid.isNullOrBlank()) {
                Text(
                    text = "Debes iniciar sesión para reportar contenido.",
                    color = Color(0xFFFF9100),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("¿Por qué deseas reportar este contenido?", fontWeight = FontWeight.Bold, color = Color.White)
                    
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ReportReason.entries.forEach { reason ->
                            val isSelected = selectedReason == reason
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0xFF00E676) else Color(0xFF2E3B34))
                                    .clickable { selectedReason = reason }
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = reason.displayName,
                                    color = if (isSelected) Color.Black else Color.White,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { if (it.length <= 250) description = it },
                        label = { Text("Describe brevemente lo ocurrido (opcional)", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00E676),
                            unfocusedBorderColor = Color(0xFF2E3B34),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        maxLines = 3
                    )
                    Text(
                        text = "${description.length}/250",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.End
                    )
                }
            }
        },
        confirmButton = {
            if (resultMessage != null) {
                if (isSuccess) {
                    Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))) {
                        Text("Entendido", color = Color.Black)
                    }
                } else {
                    Button(onClick = { resultMessage = null }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E3B34))) {
                        Text("Volver", color = Color.White)
                    }
                }
            } else if (!reporterUid.isNullOrBlank()) {
                Button(
                    onClick = {
                        if (selectedReason == null) return@Button
                        
                        isLoading = true
                        scope.launch {
                            // Simular pequeño retardo de red para la UX de loading
                            delay(500)
                            
                            val report = ContentReport(
                                reporterUid = reporterUid,
                                targetType = targetType,
                                targetId = targetId,
                                parentPostId = parentPostId,
                                reason = selectedReason!!,
                                description = description.trim()
                            )

                            val result = reportRepository.submitReport(report)
                            
                            when (result) {
                                is ReportOperationResult.Success -> {
                                    isSuccess = true
                                    resultMessage = "Reporte enviado correctamente."
                                }
                                is ReportOperationResult.Duplicate -> {
                                    isSuccess = false
                                    resultMessage = "Ya reportaste este contenido."
                                }
                                is ReportOperationResult.InvalidContent -> {
                                    isSuccess = false
                                    resultMessage = "No se pudo identificar el contenido."
                                }
                                is ReportOperationResult.Unauthorized -> {
                                    isSuccess = false
                                    resultMessage = "No tienes permisos para realizar esta acción."
                                }
                                is ReportOperationResult.StorageError -> {
                                    isSuccess = false
                                    resultMessage = "No se pudo enviar el reporte. Inténtalo nuevamente."
                                }
                            }
                            isLoading = false
                        }
                    },
                    enabled = !isLoading && selectedReason != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00E676),
                        disabledContainerColor = Color(0xFF2E3B34)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black, strokeWidth = 2.dp)
                    } else {
                        Text("Enviar reporte", color = if (selectedReason != null) Color.Black else Color.Gray)
                    }
                }
            }
        },
        dismissButton = {
            if (!isLoading && !isSuccess) {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        }
    )
}
