package devs.grupo5.sportpro.presentation.auth

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import devs.grupo5.sportpro.data.model.UserRole
import devs.grupo5.sportpro.presentation.trainer.navigation.TrainerMainContainerScreen
import devs.grupo5.sportpro.ui.screens.RegisterScreen

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
    NavHost(
        navController = navController,
        startDestination = AuthRoutes.WELCOME
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

        // Trainer Module (hosts existing TrainerMainContainerScreen directly)
        composable(AuthRoutes.TRAINER_MODULE) {
            TrainerMainContainerScreen(
                authViewModel = authViewModel,
                onLogoutSuccess = {
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
