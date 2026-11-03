package devs.grupo5.sportpro.presentation.admin.moderation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import devs.grupo5.sportpro.data.model.ContentReport
import devs.grupo5.sportpro.data.model.ReportReason
import devs.grupo5.sportpro.data.model.ReportStatus
import devs.grupo5.sportpro.data.model.ReportTargetType
import devs.grupo5.sportpro.data.repository.MockReportRepository
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID

class ModerationDashboardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockRepository: MockReportRepository
    private val adminUid = "test_admin"

    @Before
    fun setUp() {
        mockRepository = MockReportRepository.instance
        mockRepository.clearForTest()
        mockRepository.testAdminUid = adminUid
    }

    @After
    fun tearDown() {
        mockRepository.clearForTest()
    }

    @Test
    fun verifyDashboardShowsPendingReports() {
        // Arrange
        val reportInput = ContentReport(
            reporterUid = "usr1",
            targetType = ReportTargetType.PUBLICATION,
            targetId = "post1",
            reason = ReportReason.SPAM,
            description = "Test Spam"
        )
        mockRepository.submitReport(reportInput)
        
        val savedReport = mockRepository.getReports().first()

        // Act
        composeTestRule.setContent {
            ModerationDashboardScreen(
                onLogoutClick = {},
                reportRepository = mockRepository,
                adminUid = adminUid
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Panel de Moderación").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Tab_PENDING").assertIsSelected()
        composeTestRule.onNodeWithTag("ReportCard_${savedReport.id}").assertIsDisplayed()
        composeTestRule.onNodeWithText(ReportReason.SPAM.displayName).assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Spam").assertIsDisplayed()
    }

    @Test
    fun markReportInReview() {
        val report = ContentReport(
            id = UUID.randomUUID().toString(),
            reporterUid = "usr1",
            targetType = ReportTargetType.PUBLICATION,
            targetId = "post1",
            reason = ReportReason.SPAM
        )
        mockRepository.submitReport(report)

        composeTestRule.setContent {
            ModerationDashboardScreen(
                onLogoutClick = {},
                reportRepository = mockRepository,
                adminUid = adminUid
            )
        }

        composeTestRule.onNodeWithText("Revisar →").performClick()
        composeTestRule.onNodeWithText("Detalle del Reporte").assertIsDisplayed()
        composeTestRule.onNodeWithText("Marcar en revisión").performClick()

        assert(mockRepository.getReports().first().status == ReportStatus.IN_REVIEW)
    }

    @Test
    fun dismissReport() {
        val report = ContentReport(
            id = UUID.randomUUID().toString(),
            reporterUid = "usr1",
            targetType = ReportTargetType.PUBLICATION,
            targetId = "post1",
            reason = ReportReason.SPAM
        )
        mockRepository.submitReport(report)

        composeTestRule.setContent {
            ModerationDashboardScreen(
                onLogoutClick = {},
                reportRepository = mockRepository,
                adminUid = adminUid
            )
        }

        composeTestRule.onNodeWithText("Revisar →").performClick()
        composeTestRule.onNodeWithText("Descartar reporte").performClick()
        
        // Modal de confirmación de descarte
        composeTestRule.onNodeWithText("Descartar").performClick()

        assert(mockRepository.getReports().first().status == ReportStatus.DISMISSED)
    }

    @Test
    fun blockContent() {
        val report = ContentReport(
            id = UUID.randomUUID().toString(),
            reporterUid = "usr1",
            targetType = ReportTargetType.PUBLICATION,
            targetId = "post1",
            reason = ReportReason.MINOR_INAPPROPRIATE,
            minorProtectionFlag = true
        )
        mockRepository.submitReport(report)

        composeTestRule.setContent {
            ModerationDashboardScreen(
                onLogoutClick = {},
                reportRepository = mockRepository,
                adminUid = adminUid
            )
        }

        composeTestRule.onNodeWithText("Revisar →").performClick()
        composeTestRule.onNodeWithText("Bloquear contenido").performClick()
        
        // Modal de confirmación de bloqueo
        composeTestRule.onNodeWithText("Confirmar").performClick()

        assert(mockRepository.getReports().first().status == ReportStatus.RESOLVED)
        assert(mockRepository.getContentModerationStatus("post1") == devs.grupo5.sportpro.data.model.ContentModerationStatus.BLOCKED)
    }
}
