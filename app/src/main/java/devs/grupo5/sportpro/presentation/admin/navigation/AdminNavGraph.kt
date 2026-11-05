package devs.grupo5.sportpro.presentation.admin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import devs.grupo5.sportpro.data.model.UserRole
import devs.grupo5.sportpro.presentation.auth.AuthViewModel
import devs.grupo5.sportpro.presentation.admin.moderation.ModerationDashboardScreen

object AdminRoutes {
    const val MODERATION_PANEL = "moderation_panel"
}

@Composable
fun AdminMainContainerScreen(
    authViewModel: AuthViewModel = viewModel(),
    onLogoutSuccess: () -> Unit = {},
    navController: NavHostController = rememberNavController()
) {
    val currentUser by authViewModel.currentUser.collectAsState()

    // Auth guard for Admin
    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            onLogoutSuccess()
        } else if (currentUser?.role != UserRole.ADMIN) {
            // Un usuario normal no puede estar aquí
            onLogoutSuccess()
        }
    }

    NavHost(
        navController = navController,
        startDestination = AdminRoutes.MODERATION_PANEL
    ) {
        composable(AdminRoutes.MODERATION_PANEL) {
            ModerationDashboardScreen(
                onLogoutClick = onLogoutSuccess
            )
        }
    }
}
