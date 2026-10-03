package devs.grupo5.sportpro.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import devs.grupo5.sportpro.data.model.UserRole
import devs.grupo5.sportpro.presentation.trainer.navigation.TrainerMainContainerScreen
import devs.grupo5.sportpro.ui.screens.RegisterScreen
import devs.grupo5.sportpro.ui.theme.BackgroundDark
import devs.grupo5.sportpro.ui.theme.NeonGreen

object AuthRoutes {
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTER_PERSONAL = "register_personal"
    const val REGISTER_ROLE = "register_role"

    const val TRAINER_MODULE = "trainer_module"
    const val PLAYER_MODULE = "player_module"
    const val PARENT_MODULE = "parent_module"
}

@Composable
fun AuthNavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel()
) {
    val isSessionChecked by authViewModel.isSessionChecked.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    if (!isSessionChecked) {
        // Initial Loading Splash Screen while checking Firebase session
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "SportPro",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonGreen
                )
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(
                    color = NeonGreen,
                    strokeWidth = 3.dp
                )
            }
        }
        return
    }

    val startDestination = when (currentUser?.role) {
        UserRole.TRAINER -> AuthRoutes.TRAINER_MODULE
        UserRole.PLAYER -> AuthRoutes.PLAYER_MODULE
        UserRole.PARENT -> AuthRoutes.PARENT_MODULE
        UserRole.ADMIN -> AuthRoutes.PLAYER_MODULE
        null -> AuthRoutes.WELCOME
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Welcome Screen
        composable(AuthRoutes.WELCOME) {
            WelcomeScreen(
                onCreateAccountClick = {
                    navController.navigate(AuthRoutes.REGISTER_PERSONAL)
                },
                onLoginClick = {
                    navController.navigate(AuthRoutes.LOGIN)
                }
            )
        }

        // Login Screen
        composable(AuthRoutes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onBack = {
                    navController.popBackStack()
                },
                onCreateAccountClick = {
                    navController.navigate(AuthRoutes.REGISTER_PERSONAL)
                },
                onLoginSuccess = { role ->
                    navigateByRole(navController, role)
                }
            )
        }

        // Register Step 1: Personal Data
        composable(AuthRoutes.REGISTER_PERSONAL) {
            RegisterScreen(
                onBack = {
                    navController.popBackStack()
                },
                onLogin = {
                    navController.navigate(AuthRoutes.LOGIN)
                },
                onContinue = { formData ->
                    authViewModel.saveStep1Data(formData)
                    navController.navigate(AuthRoutes.REGISTER_ROLE)
                },
                correoYaRegistrado = { email ->
                    authViewModel.isEmailRegistered(email)
                }
            )
        }

        // Register Step 2: Role Selection
        composable(AuthRoutes.REGISTER_ROLE) {
            RegisterRoleScreen(
                viewModel = authViewModel,
                onBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = { role ->
                    navigateByRole(navController, role)
                }
            )
        }

        // Trainer Module
        composable(AuthRoutes.TRAINER_MODULE) {
            TrainerMainContainerScreen(
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(AuthRoutes.WELCOME) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Player Placeholder
        composable(AuthRoutes.PLAYER_MODULE) {
            PlayerPlaceholderScreen(
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(AuthRoutes.WELCOME) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Parent Placeholder
        composable(AuthRoutes.PARENT_MODULE) {
            ParentPlaceholderScreen(
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(AuthRoutes.WELCOME) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

private fun navigateByRole(navController: NavHostController, role: UserRole) {
    val destination = when (role) {
        UserRole.TRAINER -> AuthRoutes.TRAINER_MODULE
        UserRole.PLAYER -> AuthRoutes.PLAYER_MODULE
        UserRole.PARENT -> AuthRoutes.PARENT_MODULE
        UserRole.ADMIN -> AuthRoutes.PLAYER_MODULE
    }

    navController.navigate(destination) {
        popUpTo(AuthRoutes.WELCOME) {
            inclusive = true
        }
        launchSingleTop = true
    }
}
