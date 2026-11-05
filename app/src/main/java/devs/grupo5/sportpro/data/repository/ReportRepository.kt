package devs.grupo5.sportpro.data.repository

import devs.grupo5.sportpro.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

interface ReportRepository {
    val reports: StateFlow<List<ContentReport>>
    val moderationActions: StateFlow<List<ModerationAction>>

    fun submitReport(report: ContentReport): ReportOperationResult<Unit>
    fun getReports(): List<ContentReport>
    fun getPendingReports(): List<ContentReport>
    
    fun updateReportStatus(adminUid: String, reportId: String, newStatus: ReportStatus): ReportOperationResult<Unit>
    
    fun hideContent(adminUid: String, targetType: ReportTargetType, targetId: String, reason: String): ReportOperationResult<Unit>
    fun blockContent(adminUid: String, targetType: ReportTargetType, targetId: String, reason: String): ReportOperationResult<Unit>
    fun restoreContent(adminUid: String, targetType: ReportTargetType, targetId: String, reason: String): ReportOperationResult<Unit>
    
    fun getContentModerationStatus(targetId: String): ContentModerationStatus
}

class MockReportRepository private constructor() : ReportRepository {
    private val _reports = MutableStateFlow<List<ContentReport>>(emptyList())
    override val reports = _reports.asStateFlow()

    private val _moderationActions = MutableStateFlow<List<ModerationAction>>(emptyList())
    override val moderationActions = _moderationActions.asStateFlow()

    // Map para seguimiento local de qué contenidos están bloqueados/ocultos (ID -> Status)
    private val _contentStatuses = MutableStateFlow<Map<String, ContentModerationStatus>>(emptyMap())

    override fun submitReport(report: ContentReport): ReportOperationResult<Unit> {
        if (report.targetId.isBlank() || report.reporterUid.isBlank()) {
            return ReportOperationResult.InvalidContent
        }
        
        val currentReports = _reports.value
        // Evitar duplicados del mismo usuario contra el mismo contenido exacto
        if (currentReports.any { 
            it.targetId == report.targetId && 
            it.targetType == report.targetType &&
            it.parentPostId == report.parentPostId &&
            it.reporterUid == report.reporterUid 
        }) {
            return ReportOperationResult.Duplicate
        }
        
        // Prioridad especial para exposición de menores
        val minorFlag = report.reason == ReportReason.PRIVATE_INFO_EXPOSURE || 
                        report.reason == ReportReason.MINOR_INAPPROPRIATE
        
        val newReport = report.copy(
            id = UUID.randomUUID().toString(), 
            createdAt = System.currentTimeMillis(),
            minorProtectionFlag = minorFlag
        )
        
        _reports.value = currentReports + newReport
        return ReportOperationResult.Success(Unit)
    }

    override fun getReports(): List<ContentReport> = _reports.value

    override fun getPendingReports(): List<ContentReport> = _reports.value.filter { it.status == ReportStatus.PENDING }

    /**
     * UID configurado exclusivamente para pruebas unitarias.
     * NUNCA se debe utilizar un mecanismo así en producción.
     */
    var testAdminUid: String? = null

    /**
     * NOTA DE SEGURIDAD (MOCK): 
     * Esta validación de administrador es simulada localmente exigiendo una identidad de prueba explícita.
     * 
     * LIMITACIONES: En producción, NUNCA se debe confiar en el UID proporcionado libremente desde el cliente.
     * La protección real requiere Firebase Custom Claims (rol ADMIN) validado mediante 
     * Reglas de Seguridad en Firestore y validación de token en Cloud Functions.
     */
    private fun verifyAdminMock(adminUid: String): ReportOperationResult<Unit>? {
        if (testAdminUid == null || adminUid != testAdminUid) {
            return ReportOperationResult.Unauthorized
        }
        return null
    }

    fun clearForTest() {
        _reports.value = emptyList()
        _moderationActions.value = emptyList()
        _contentStatuses.value = emptyMap()
        testAdminUid = null
    }

    override fun updateReportStatus(adminUid: String, reportId: String, newStatus: ReportStatus): ReportOperationResult<Unit> {
        verifyAdminMock(adminUid)?.let { return it }

        val currentReports = _reports.value
        val reportExists = currentReports.any { it.id == reportId }
        if (!reportExists) return ReportOperationResult.InvalidContent

        _reports.value = currentReports.map { if (it.id == reportId) it.copy(status = newStatus) else it }
        return ReportOperationResult.Success(Unit)
    }

    override fun hideContent(adminUid: String, targetType: ReportTargetType, targetId: String, reason: String): ReportOperationResult<Unit> {
        verifyAdminMock(adminUid)?.let { return it }
        if (targetId.isBlank()) return ReportOperationResult.InvalidContent

        applyAction(adminUid, targetType, targetId, ContentModerationStatus.HIDDEN, reason)
        return ReportOperationResult.Success(Unit)
    }

    override fun blockContent(adminUid: String, targetType: ReportTargetType, targetId: String, reason: String): ReportOperationResult<Unit> {
        verifyAdminMock(adminUid)?.let { return it }
        if (targetId.isBlank()) return ReportOperationResult.InvalidContent

        applyAction(adminUid, targetType, targetId, ContentModerationStatus.BLOCKED, reason)
        return ReportOperationResult.Success(Unit)
    }

    override fun restoreContent(adminUid: String, targetType: ReportTargetType, targetId: String, reason: String): ReportOperationResult<Unit> {
        verifyAdminMock(adminUid)?.let { return it }
        if (targetId.isBlank()) return ReportOperationResult.InvalidContent

        applyAction(adminUid, targetType, targetId, ContentModerationStatus.VISIBLE, reason)
        return ReportOperationResult.Success(Unit)
    }

    private fun applyAction(adminUid: String, targetType: ReportTargetType, targetId: String, newStatus: ContentModerationStatus, reason: String) {
        val action = ModerationAction(
            id = UUID.randomUUID().toString(),
            actionType = newStatus,
            targetType = targetType,
            targetId = targetId,
            adminUid = adminUid,
            date = System.currentTimeMillis(),
            reason = reason
        )
        _moderationActions.value = _moderationActions.value + action
        
        val updatedStatuses = _contentStatuses.value.toMutableMap()
        if (newStatus == ContentModerationStatus.VISIBLE) {
            updatedStatuses.remove(targetId)
        } else {
            updatedStatuses[targetId] = newStatus
        }
        _contentStatuses.value = updatedStatuses
        
        // Autoresolver los reportes asociados si el contenido es bloqueado/ocultado
        val associatedReports = _reports.value.filter { it.targetId == targetId && it.status == ReportStatus.PENDING }
        if (associatedReports.isNotEmpty() && newStatus != ContentModerationStatus.VISIBLE) {
            _reports.value = _reports.value.map {
                if (it.targetId == targetId && it.status == ReportStatus.PENDING) it.copy(status = ReportStatus.RESOLVED) else it
            }
        }
    }

    override fun getContentModerationStatus(targetId: String): ContentModerationStatus {
        return _contentStatuses.value[targetId] ?: ContentModerationStatus.VISIBLE
    }

    companion object {
        val instance: MockReportRepository by lazy { MockReportRepository() }
    }
}
