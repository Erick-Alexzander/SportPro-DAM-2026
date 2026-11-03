package devs.grupo5.sportpro.data.repository

import devs.grupo5.sportpro.data.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MockReportRepositoryTest {

    private lateinit var repository: ReportRepository

    @Before
    fun setUp() {
        repository = MockReportRepository.instance.apply {
            clearForTest()
            testAdminUid = "admin_real"
        }
    }

    @Test
    fun `crear un reporte valido devuelve Success`() {
        val report = ContentReport(
            reporterUid = "user_123",
            targetType = ReportTargetType.PUBLICATION,
            targetId = "post_123",
            reason = ReportReason.SPAM
        )
        val result = repository.submitReport(report)
        assertTrue(result is ReportOperationResult.Success<*>)
    }

    @Test
    fun `rechazar un reporte duplicado devuelve Duplicate`() {
        val report = ContentReport(
            reporterUid = "user_456",
            targetType = ReportTargetType.COMMENT,
            targetId = "comment_123",
            reason = ReportReason.HARASSMENT
        )
        
        // Primer intento exitoso
        repository.submitReport(report)
        
        // Segundo intento duplicado exacto
        val result = repository.submitReport(report)
        assertTrue(result is ReportOperationResult.Duplicate)
        
        // Tercer intento: mismo targetId pero diferente targetType (ej: post en vez de comment)
        val postReport = report.copy(targetType = ReportTargetType.PUBLICATION)
        val resultPost = repository.submitReport(postReport)
        assertTrue(resultPost is ReportOperationResult.Success<*>)
    }

    @Test
    fun `consultar reportes pendientes`() {
        val pendingReports = repository.getPendingReports()
        assertTrue(pendingReports.all { it.status == ReportStatus.PENDING })
    }

    @Test
    fun `cambiar el estado de un reporte`() {
        // Creamos uno nuevo para asegurarnos de que exista
        val report = ContentReport(
            reporterUid = "user_789",
            targetType = ReportTargetType.TRYOUT,
            targetId = "tryout_123",
            reason = ReportReason.OTHER
        )
        repository.submitReport(report)
        
        // Buscamos el reporte recién creado
        val createdReport = repository.getPendingReports().find { it.targetId == "tryout_123" }
        assertNotNull(createdReport)
        
        val result = repository.updateReportStatus(
            adminUid = "admin_real", 
            reportId = createdReport!!.id, 
            newStatus = ReportStatus.IN_REVIEW
        )
        
        assertTrue(result is ReportOperationResult.Success<*>)
        
        // Verificamos que se actualizó
        val updatedReport = repository.getReports().find { it.id == createdReport.id }
        assertEquals(ReportStatus.IN_REVIEW, updatedReport?.status)
    }

    @Test
    fun `ocultar un contenido y verificar su estado`() {
        val result = repository.hideContent(
            adminUid = "admin_real",
            targetType = ReportTargetType.PUBLICATION,
            targetId = "post_to_hide",
            reason = "Contenido inapropiado"
        )
        assertTrue(result is ReportOperationResult.Success<*>)
        
        val status = repository.getContentModerationStatus("post_to_hide")
        assertEquals(ContentModerationStatus.HIDDEN, status)
    }

    @Test
    fun `restaurar un contenido y verificar su estado`() {
        repository.hideContent("admin_real", ReportTargetType.PUBLICATION, "post_to_restore", "test")
        
        val restoreResult = repository.restoreContent("admin_real", ReportTargetType.PUBLICATION, "post_to_restore", "restaurado")
        assertTrue(restoreResult is ReportOperationResult.Success<*>)
        
        val status = repository.getContentModerationStatus("post_to_restore")
        assertEquals(ContentModerationStatus.VISIBLE, status)
    }

    @Test
    fun `rechazar una accion de moderacion sin autorizacion explícita`() {
        val result = repository.hideContent(
            adminUid = "any_arbitrary_uid", // Este UID no es el administrador de prueba configurado
            targetType = ReportTargetType.PUBLICATION,
            targetId = "post_target",
            reason = "test"
        )
        
        assertTrue(result is ReportOperationResult.Unauthorized)
    }
}
