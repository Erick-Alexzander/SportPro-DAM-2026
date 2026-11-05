package devs.grupo5.sportpro.presentation.admin.moderation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import devs.grupo5.sportpro.data.model.ContentReport
import devs.grupo5.sportpro.data.model.ContentModerationStatus
import devs.grupo5.sportpro.data.model.ReportStatus
import devs.grupo5.sportpro.data.repository.MockReportRepository

val DarkBackground = Color(0xFF101413)
val SurfaceColor = Color(0xFF1B231F)
val BorderColor = Color(0xFF2E3B34)
val NeonGreen = Color(0xFF00E676)
val WarningColor = Color(0xFFFF9100)
val DangerColor = Color(0xFFD50000)

@Composable
fun ModerationDashboardScreen(
    onLogoutClick: () -> Unit,
    reportRepository: devs.grupo5.sportpro.data.repository.ReportRepository = MockReportRepository.instance,
    adminUid: String = "admin_uid"
) {
    val reports by reportRepository.reports.collectAsState()

    var selectedTab by remember { mutableStateOf(ReportStatus.PENDING) }
    var selectedReport by remember { mutableStateOf<ContentReport?>(null) }

    val filteredReports = reports.filter { it.status == selectedTab }

    // Counts for UI
    val counts = ReportStatus.entries.associateWith { status ->
        reports.count { it.status == status }
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SportPro Admin",
                            fontSize = 12.sp,
                            color = NeonGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Panel de Moderación",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Salir", tint = Color.Gray)
                    }
                }

                androidx.compose.material3.ScrollableTabRow(
                    selectedTabIndex = ReportStatus.entries.indexOf(selectedTab),
                    containerColor = DarkBackground,
                    contentColor = NeonGreen,
                    edgePadding = 16.dp
                ) {
                    ReportStatus.entries.forEach { status ->
                        Tab(
                            selected = selectedTab == status,
                            onClick = { selectedTab = status },
                            modifier = Modifier.testTag("Tab_${status.name}"),
                            text = { 
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(status.name, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(if (selectedTab == status) NeonGreen else BorderColor)
                                            .padding(horizontal = 6.dp, vertical = 2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${counts[status] ?: 0}",
                                            color = if (selectedTab == status) Color.Black else Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            },
                            selectedContentColor = NeonGreen,
                            unselectedContentColor = Color.Gray
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (filteredReports.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay reportes en este estado", color = Color.Gray, fontSize = 14.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredReports, key = { it.id }) { report ->
                        ReportCard(report = report, onClick = { selectedReport = report })
                    }
                }
            }
        }
    }

    selectedReport?.let { report ->
        ModerationDetailDialog(
            report = report,
            onDismiss = { selectedReport = null },
            onAction = { actionType, reason ->
                when (actionType) {
                    ContentModerationStatus.HIDDEN -> reportRepository.hideContent(adminUid, report.targetType, report.targetId, reason)
                    ContentModerationStatus.BLOCKED -> reportRepository.blockContent(adminUid, report.targetType, report.targetId, reason)
                    ContentModerationStatus.VISIBLE -> reportRepository.restoreContent(adminUid, report.targetType, report.targetId, reason)
                }
                selectedReport = null
            },
            onMarkReview = {
                reportRepository.updateReportStatus(adminUid, report.id, ReportStatus.IN_REVIEW)
                selectedReport = null
            },
            onDismissReport = {
                reportRepository.updateReportStatus(adminUid, report.id, ReportStatus.DISMISSED)
                selectedReport = null
            }
        )
    }
}

@Composable
fun ReportCard(report: ContentReport, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("ReportCard_${report.id}"),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (report.minorProtectionFlag) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = DangerColor, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = report.reason.displayName,
                            color = if (report.minorProtectionFlag) DangerColor else NeonGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Contenido: ${report.targetType.name}",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BorderColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = report.status.name,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            if (report.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = report.description,
                    color = Color.White,
                    fontSize = 13.sp,
                    maxLines = 2,
                    lineHeight = 18.sp,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(text = "Revisar →", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun ModerationDetailDialog(
    report: ContentReport,
    onDismiss: () -> Unit,
    onAction: (ContentModerationStatus, String) -> Unit,
    onMarkReview: () -> Unit,
    onDismissReport: () -> Unit
) {
    var confirmAction by remember { mutableStateOf<ContentModerationStatus?>(null) }
    var confirmDismiss by remember { mutableStateOf(false) }
    
    if (confirmAction != null) {
        AlertDialog(
            onDismissRequest = { confirmAction = null },
            containerColor = SurfaceColor,
            titleContentColor = Color.White,
            textContentColor = Color.LightGray,
            title = { Text("Confirmar acción") },
            text = { 
                Text(
                    text = when (confirmAction) {
                        ContentModerationStatus.BLOCKED -> "¿Seguro que deseas BLOQUEAR este contenido? Desaparecerá inmediatamente del feed de todos los usuarios."
                        ContentModerationStatus.HIDDEN -> "¿Deseas ocultar este contenido mientras lo revisas?"
                        ContentModerationStatus.VISIBLE -> "¿Restaurar contenido? Volverá a ser visible para la comunidad."
                        else -> "¿Continuar?"
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = { 
                        onAction(confirmAction!!, "Acción confirmada por el admin")
                        confirmAction = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (confirmAction == ContentModerationStatus.BLOCKED) DangerColor else NeonGreen)
                ) {
                    Text("Confirmar", color = if (confirmAction == ContentModerationStatus.BLOCKED) Color.White else Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmAction = null }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    } else if (confirmDismiss) {
        AlertDialog(
            onDismissRequest = { confirmDismiss = false },
            containerColor = SurfaceColor,
            titleContentColor = Color.White,
            textContentColor = Color.LightGray,
            title = { Text("Descartar reporte") },
            text = { Text("¿Estás seguro de descartar este reporte sin tomar acción sobre el contenido?") },
            confirmButton = {
                Button(
                    onClick = { 
                        onDismissReport()
                        confirmDismiss = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WarningColor)
                ) {
                    Text("Descartar", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDismiss = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    } else {

        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = SurfaceColor,
            titleContentColor = Color.White,
            textContentColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            title = { Text("Detalle del Reporte", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Motivo", color = Color.Gray, fontSize = 12.sp)
                    Text(report.reason.displayName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = if (report.minorProtectionFlag) DangerColor else Color.White)
                    
                    if (report.minorProtectionFlag) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("⚠️ Prioridad Alta: Protección de menores", color = DangerColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Tipo de Contenido", color = Color.Gray, fontSize = 12.sp)
                    Text("${report.targetType}", fontSize = 14.sp)
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("ID Objetivo", color = Color.Gray, fontSize = 12.sp)
                    Text(report.targetId, fontSize = 13.sp, color = NeonGreen)
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Descripción del denunciante", color = Color.Gray, fontSize = 12.sp)
                    Text(report.description.ifBlank { "Sin descripción adicional" }, fontSize = 14.sp)
                }
            },
            confirmButton = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (report.status == ReportStatus.PENDING) {
                        Button(onClick = { onMarkReview() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = WarningColor)) {
                            Text("Marcar en revisión", color = Color.Black)
                        }
                    }
                    if (report.status != ReportStatus.DISMISSED && report.status != ReportStatus.RESOLVED) {
                        Button(onClick = { confirmAction = ContentModerationStatus.HIDDEN }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = WarningColor)) {
                            Text("Ocultar contenido", color = Color.Black)
                        }
                        Button(onClick = { confirmAction = ContentModerationStatus.BLOCKED }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = DangerColor)) {
                            Text("Bloquear contenido", color = Color.White)
                        }
                        OutlinedButton(
                            onClick = { confirmDismiss = true }, 
                            modifier = Modifier.fillMaxWidth(),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
                        ) {
                            Text("Descartar reporte", color = Color.LightGray)
                        }
                    }
                    if (report.status == ReportStatus.RESOLVED) {
                        Button(onClick = { confirmAction = ContentModerationStatus.VISIBLE }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)) {
                            Text("Restaurar contenido", color = Color.Black)
                        }
                    }
                    TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                        Text("Cerrar panel", color = Color.Gray)
                    }
                }
            }
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun ModerationDashboardScreenPreview() {
    // Configurar el entorno mock para la previsualización sin alterar producción.
    val previewRepo = MockReportRepository.instance.apply {
        testAdminUid = "preview_admin"
        // Inyectamos algunos reportes mock para visualizar
        submitReport(ContentReport(
            id = "mock1", 
            reporterUid = "usr1", 
            targetType = devs.grupo5.sportpro.data.model.ReportTargetType.PUBLICATION,
            targetId = "p1",
            reason = devs.grupo5.sportpro.data.model.ReportReason.SPAM,
            description = "Contenido repetido"
        ))
        submitReport(ContentReport(
            id = "mock2", 
            reporterUid = "usr2", 
            targetType = devs.grupo5.sportpro.data.model.ReportTargetType.COMMENT,
            targetId = "c1",
            reason = devs.grupo5.sportpro.data.model.ReportReason.MINOR_INAPPROPRIATE,
            minorProtectionFlag = true
        ))
    }
    
    ModerationDashboardScreen(
        onLogoutClick = {},
        reportRepository = previewRepo,
        adminUid = "preview_admin"
    )
}
