package devs.grupo5.sportpro.presentation.player.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import devs.grupo5.sportpro.data.model.UserProfile
import devs.grupo5.sportpro.presentation.auth.AuthViewModel
import devs.grupo5.sportpro.presentation.player.availability.AvailabilityScreen
import devs.grupo5.sportpro.presentation.player.calendar.CreateSessionScreen
import devs.grupo5.sportpro.presentation.player.calendar.SessionDetailScreen
import devs.grupo5.sportpro.presentation.player.calendar.TrainingsCalendarScreen
import devs.grupo5.sportpro.presentation.player.community.CommunityScreen
import devs.grupo5.sportpro.presentation.player.community.PostDetailScreen
import devs.grupo5.sportpro.presentation.player.components.PlayerTab
import devs.grupo5.sportpro.presentation.player.dashboard.PlayerHomeScreen
import devs.grupo5.sportpro.presentation.player.help.HelpSupportScreen
import devs.grupo5.sportpro.presentation.player.library.ExerciseLibraryScreen
import devs.grupo5.sportpro.presentation.player.matches.MatchDetailScreen
import devs.grupo5.sportpro.presentation.player.matches.MatchesScreen
import devs.grupo5.sportpro.presentation.player.notifications.NotificationsScreen
import devs.grupo5.sportpro.presentation.player.privacy.PrivacyScreen
import devs.grupo5.sportpro.presentation.player.profile.ProfileScreen
import devs.grupo5.sportpro.presentation.player.settings.SettingsScreen

object PlayerRoutes {
    const val HOME = "player_home"
    const val CALENDAR = "player_calendar"
    const val SESSION_DETAIL = "player_session_detail/{sessionId}"
    const val CREATE_SESSION = "player_create_session"
    const val EXERCISE_LIBRARY = "player_exercise_library"
    const val AVAILABILITY = "player_availability"
    const val MATCHES = "player_matches"
    const val MATCH_DETAIL = "player_match_detail/{matchId}"
    const val PROFILE = "player_profile"
    const val NOTIFICATIONS = "player_notifications"
    const val PRIVACY = "player_privacy"
    const val SETTINGS = "player_settings"
    const val HELP = "player_help"
    const val COMMUNITY = "player_community"
    const val POST_DETAIL = "player_post_detail/{postId}"

    fun sessionDetail(sessionId: String) = "player_session_detail/$sessionId"
    fun postDetail(postId: String) = "player_post_detail/$postId"
    fun matchDetail(matchId: String) = "player_match_detail/$matchId"
}

@Composable
fun PlayerMainContainerScreen(
    onLogoutSuccess: () -> Unit = {},
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = PlayerRoutes.HOME
    ) {
        composable(PlayerRoutes.HOME) {
            val currentUser by authViewModel.currentUser.collectAsState()

            PlayerHomeScreen(
                playerName = displayNameOf(currentUser).ifBlank { "Jugador" },
                onLogoutClick = onLogoutSuccess,
                onConvocatoriaClick = { navController.navigate(PlayerRoutes.AVAILABILITY) },
                onMatchesClick = { navigateToTab(navController, PlayerTab.MATCHES) },
                onCalendarClick = { navigateToTab(navController, PlayerTab.CALENDAR) },
                onProfileClick = { navigateToTab(navController, PlayerTab.PROFILE) },
                onCommunityClick = { navigateToTab(navController, PlayerTab.COMMUNITY) },
                onTabSelected = { tab -> navigateToTab(navController, tab) }
            )
        }

        composable(PlayerRoutes.AVAILABILITY) {
            AvailabilityScreen(
                onBackClick = { navController.popBackStack() },
                onConfirmClick = { navController.popBackStack() },
                onTabSelected = { tab -> navigateToTab(navController, tab) }
            )
        }

        composable(PlayerRoutes.MATCHES) {
            MatchesScreen(
                onBackClick = { navController.popBackStack() },
                onMatchClick = { matchId ->
                    navController.navigate(PlayerRoutes.matchDetail(matchId))
                },
                onTabSelected = { tab -> navigateToTab(navController, tab) }
            )
        }

        composable(
            route = PlayerRoutes.MATCH_DETAIL,
            arguments = listOf(navArgument("matchId") { type = NavType.StringType })
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            MatchDetailScreen(
                matchId = matchId,
                onBackClick = { navController.popBackStack() },
                onTabSelected = { tab -> navigateToTab(navController, tab) }
            )
        }

        composable(PlayerRoutes.CALENDAR) {
            TrainingsCalendarScreen(
                onBackClick = { navController.popBackStack() },
                onAddClick = { navController.navigate(PlayerRoutes.CREATE_SESSION) },
                onTrainingClick = { sessionId ->
                    navController.navigate(PlayerRoutes.sessionDetail(sessionId))
                },
                onLibraryClick = {
                    navController.navigate(PlayerRoutes.EXERCISE_LIBRARY)
                },
                onTabSelected = { tab -> navigateToTab(navController, tab) }
            )
        }

        composable(PlayerRoutes.CREATE_SESSION) {
            CreateSessionScreen(
                onBackClick = { navController.popBackStack() },
                onNewExerciseClick = { navController.navigate(PlayerRoutes.EXERCISE_LIBRARY) },
                onCreateSessionClick = { navController.popBackStack() },
                onTabSelected = { tab -> navigateToTab(navController, tab) }
            )
        }

        composable(PlayerRoutes.EXERCISE_LIBRARY) {
            ExerciseLibraryScreen(
                onBackClick = { navController.popBackStack() },
                onTabSelected = { tab -> navigateToTab(navController, tab) }
            )
        }

        composable(
            route = PlayerRoutes.SESSION_DETAIL,
            arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
            SessionDetailScreen(
                sessionId = sessionId,
                onBackClick = { navController.popBackStack() },
                onTabSelected = { tab -> navigateToTab(navController, tab) }
            )
        }

        composable(PlayerRoutes.PROFILE) {
            val currentUser by authViewModel.currentUser.collectAsState()
            val registeredName = displayNameOf(currentUser)
            val registeredEmail = currentUser?.email ?: ""
            val roleLabel = when (currentUser?.role?.name) {
                "PLAYER" -> "Jugador"
                "TRAINER" -> "Entrenador"
                "PARENT" -> "Apoderado"
                "ADMIN" -> "Administrador"
                else -> "Jugador"
            }
            val computedInitials = initialsOf(currentUser)

            ProfileScreen(
                fullName = registeredName,
                email = registeredEmail,
                role = roleLabel,
                birthDate = currentUser?.birthDate,
                initials = computedInitials,
                onBackClick = { navController.popBackStack() },
                onNotificationsClick = { navController.navigate(PlayerRoutes.NOTIFICATIONS) },
                onPrivacyClick = { navController.navigate(PlayerRoutes.PRIVACY) },
                onSettingsClick = { navController.navigate(PlayerRoutes.SETTINGS) },
                onHelpClick = { navController.navigate(PlayerRoutes.HELP) },
                onLogoutClick = onLogoutSuccess,
                onTabSelected = { tab -> navigateToTab(navController, tab) }
            )
        }

        composable(PlayerRoutes.NOTIFICATIONS) {
            NotificationsScreen(
                onBackClick = { navController.popBackStack() },
                onTabSelected = { tab -> navigateToTab(navController, tab) }
            )
        }

        composable(PlayerRoutes.PRIVACY) {
            PrivacyScreen(
                onBackClick = { navController.popBackStack() },
                onTabSelected = { tab -> navigateToTab(navController, tab) }
            )
        }

        composable(PlayerRoutes.SETTINGS) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onTabSelected = { tab -> navigateToTab(navController, tab) }
            )
        }

        composable(PlayerRoutes.HELP) {
            HelpSupportScreen(
                onBackClick = { navController.popBackStack() },
                onTabSelected = { tab -> navigateToTab(navController, tab) }
            )
        }

        composable(PlayerRoutes.COMMUNITY) {
            CommunityScreen(
                onPostClick = { postId -> navController.navigate(PlayerRoutes.postDetail(postId)) },
                onTabSelected = { tab -> navigateToTab(navController, tab) }
            )
        }

        composable(
            route = PlayerRoutes.POST_DETAIL,
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            val currentUser by authViewModel.currentUser.collectAsState()

            PostDetailScreen(
                postId = postId,
                currentUserInitials = initialsOf(currentUser),
                onBackClick = { navController.popBackStack() },
                onTabSelected = { tab -> navigateToTab(navController, tab) }
            )
        }
    }
}

private fun displayNameOf(user: UserProfile?): String =
    listOfNotNull(
        user?.firstName?.trim()?.takeIf { it.isNotEmpty() },
        user?.lastName?.trim()?.takeIf { it.isNotEmpty() }
    ).joinToString(" ").ifBlank { user?.email?.substringBefore("@") ?: "" }

private fun initialsOf(user: UserProfile?): String =
    displayNameOf(user).split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .map { it.first().uppercase() }
        .joinToString("")
        .ifBlank { "?" }

private fun navigateToTab(navController: NavHostController, tab: PlayerTab) {
    val route = when (tab) {
        PlayerTab.HOME -> PlayerRoutes.HOME
        PlayerTab.CALENDAR -> PlayerRoutes.CALENDAR
        PlayerTab.MATCHES -> PlayerRoutes.MATCHES
        PlayerTab.COMMUNITY -> PlayerRoutes.COMMUNITY
        PlayerTab.PROFILE -> PlayerRoutes.PROFILE
    }

    navController.navigate(route) {
        popUpTo(PlayerRoutes.HOME) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
