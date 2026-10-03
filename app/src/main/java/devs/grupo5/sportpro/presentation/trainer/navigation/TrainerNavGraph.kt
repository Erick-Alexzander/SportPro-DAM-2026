package devs.grupo5.sportpro.presentation.trainer.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import devs.grupo5.sportpro.presentation.trainer.community.CommunityScreen
import devs.grupo5.sportpro.presentation.trainer.community.CommunityViewModel
import devs.grupo5.sportpro.presentation.trainer.community.CreatePostScreen
import devs.grupo5.sportpro.presentation.trainer.components.TrainerBottomNavigationBar
import devs.grupo5.sportpro.presentation.trainer.components.TrainerTab
import devs.grupo5.sportpro.presentation.trainer.dashboard.TrainerDashboardScreen
import devs.grupo5.sportpro.presentation.trainer.dashboard.TrainerDashboardViewModel
import devs.grupo5.sportpro.presentation.trainer.matches.CreateMatchScreen
import devs.grupo5.sportpro.presentation.trainer.matches.MatchViewModel
import devs.grupo5.sportpro.presentation.trainer.matches.MatchesScreen
import devs.grupo5.sportpro.presentation.trainer.players.PlayerPersonalDataScreen
import devs.grupo5.sportpro.presentation.trainer.players.PlayerResponsibleScreen
import devs.grupo5.sportpro.presentation.trainer.players.PlayerSportDataScreen
import devs.grupo5.sportpro.presentation.trainer.players.PlayerViewModel
import devs.grupo5.sportpro.presentation.trainer.players.PlayersScreen
import devs.grupo5.sportpro.presentation.trainer.profile.ProfileScreen
import devs.grupo5.sportpro.presentation.trainer.teams.CreateTeamScreen
import devs.grupo5.sportpro.presentation.trainer.teams.TeamDetailScreen
import devs.grupo5.sportpro.presentation.trainer.teams.TeamViewModel
import devs.grupo5.sportpro.presentation.trainer.teams.TeamsScreen
import devs.grupo5.sportpro.presentation.trainer.theme.SportProDarkBackground
import devs.grupo5.sportpro.presentation.trainer.theme.SportProTrainerTheme
import devs.grupo5.sportpro.presentation.trainer.training.CreateTrainingScreen
import devs.grupo5.sportpro.presentation.trainer.training.TrainingDetailScreen
import devs.grupo5.sportpro.presentation.trainer.training.TrainingViewModel
import devs.grupo5.sportpro.presentation.trainer.training.TrainingsScreen

object TrainerRoutes {
    const val DASHBOARD = "trainer_dashboard"
    const val TEAMS = "trainer_teams"
    const val CREATE_TEAM = "create_team"
    const val TEAM_DETAIL = "team_detail/{teamId}"

    const val PLAYERS = "trainer_players"
    const val PLAYER_PERSONAL = "player_personal"
    const val PLAYER_SPORT = "player_sport"
    const val PLAYER_RESPONSIBLE = "player_responsible"

    const val TRAINING = "trainer_training"
    const val CREATE_TRAINING = "create_training"
    const val TRAINING_DETAIL = "training_detail/{trainingId}"

    const val MATCHES = "trainer_matches"
    const val CREATE_MATCH = "create_match"

    const val COMMUNITY = "trainer_community"
    const val CREATE_POST = "create_post"

    const val PROFILE = "trainer_profile"
}

@Composable
fun TrainerMainContainerScreen(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: TrainerRoutes.DASHBOARD

    // Determine current tab
    val currentTab = when {
        currentRoute.startsWith(TrainerRoutes.TEAMS) || currentRoute.startsWith("team_detail") || currentRoute == TrainerRoutes.CREATE_TEAM -> TrainerTab.TEAMS
        currentRoute.startsWith(TrainerRoutes.PLAYERS) || currentRoute.startsWith("player_") -> TrainerTab.TEAMS
        currentRoute.startsWith(TrainerRoutes.TRAINING) || currentRoute.startsWith("training_") || currentRoute == TrainerRoutes.CREATE_TRAINING -> TrainerTab.TRAINING
        currentRoute.startsWith(TrainerRoutes.MATCHES) || currentRoute == TrainerRoutes.CREATE_MATCH -> TrainerTab.MATCHES
        currentRoute.startsWith(TrainerRoutes.COMMUNITY) || currentRoute == TrainerRoutes.CREATE_POST -> TrainerTab.COMMUNITY
        currentRoute.startsWith(TrainerRoutes.PROFILE) -> TrainerTab.PROFILE
        else -> TrainerTab.DASHBOARD
    }

    // ViewModels shared in navigation context
    val teamViewModel: TeamViewModel = viewModel()
    val playerViewModel: PlayerViewModel = viewModel()
    val trainingViewModel: TrainingViewModel = viewModel()
    val matchViewModel: MatchViewModel = viewModel()
    val communityViewModel: CommunityViewModel = viewModel()
    val dashboardViewModel: TrainerDashboardViewModel = viewModel()

    SportProTrainerTheme {
        Scaffold(
            containerColor = SportProDarkBackground,
            bottomBar = {
                TrainerBottomNavigationBar(
                    currentTab = currentTab,
                    onTabSelected = { tab ->
                        navController.navigate(tab.route) {
                            popUpTo(TrainerRoutes.DASHBOARD) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = TrainerRoutes.DASHBOARD,
                modifier = Modifier.padding(innerPadding)
            ) {
                // Dashboard
                composable(TrainerRoutes.DASHBOARD) {
                    TrainerDashboardScreen(
                        viewModel = dashboardViewModel,
                        onNavigateToTrainings = { navController.navigate(TrainerRoutes.TRAINING) },
                        onNavigateToMatches = { navController.navigate(TrainerRoutes.MATCHES) },
                        onNavigateToTeams = { navController.navigate(TrainerRoutes.TEAMS) },
                        onNavigateToCommunity = { navController.navigate(TrainerRoutes.COMMUNITY) }
                    )
                }

                // Teams
                composable(TrainerRoutes.TEAMS) {
                    TeamsScreen(
                        viewModel = teamViewModel,
                        onCreateTeamClick = { navController.navigate(TrainerRoutes.CREATE_TEAM) },
                        onTeamClick = { teamId -> navController.navigate("team_detail/$teamId") }
                    )
                }

                composable(TrainerRoutes.CREATE_TEAM) {
                    CreateTeamScreen(
                        viewModel = teamViewModel,
                        onBackClick = { navController.popBackStack() },
                        onTeamCreated = { navController.popBackStack() }
                    )
                }

                composable(
                    route = TrainerRoutes.TEAM_DETAIL,
                    arguments = listOf(navArgument("teamId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val teamId = backStackEntry.arguments?.getString("teamId") ?: ""
                    TeamDetailScreen(
                        teamId = teamId,
                        viewModel = teamViewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                }

                // Players
                composable(TrainerRoutes.PLAYERS) {
                    PlayersScreen(
                        viewModel = playerViewModel,
                        onAddPlayerClick = { navController.navigate(TrainerRoutes.PLAYER_PERSONAL) }
                    )
                }

                composable(TrainerRoutes.PLAYER_PERSONAL) {
                    PlayerPersonalDataScreen(
                        viewModel = playerViewModel,
                        onBackClick = { navController.popBackStack() },
                        onContinueClick = { navController.navigate(TrainerRoutes.PLAYER_SPORT) }
                    )
                }

                composable(TrainerRoutes.PLAYER_SPORT) {
                    PlayerSportDataScreen(
                        viewModel = playerViewModel,
                        onBackClick = { navController.popBackStack() },
                        onContinueClick = { navController.navigate(TrainerRoutes.PLAYER_RESPONSIBLE) }
                    )
                }

                composable(TrainerRoutes.PLAYER_RESPONSIBLE) {
                    PlayerResponsibleScreen(
                        viewModel = playerViewModel,
                        onBackClick = { navController.popBackStack() },
                        onPlayerRegistered = {
                            navController.popBackStack(TrainerRoutes.PLAYERS, inclusive = false)
                        }
                    )
                }

                // Trainings
                composable(TrainerRoutes.TRAINING) {
                    TrainingsScreen(
                        viewModel = trainingViewModel,
                        onCreateTrainingClick = { navController.navigate(TrainerRoutes.CREATE_TRAINING) },
                        onTrainingClick = { trainingId -> navController.navigate("training_detail/$trainingId") }
                    )
                }

                composable(TrainerRoutes.CREATE_TRAINING) {
                    CreateTrainingScreen(
                        viewModel = trainingViewModel,
                        onBackClick = { navController.popBackStack() },
                        onSessionCreated = { navController.popBackStack() }
                    )
                }

                composable(
                    route = TrainerRoutes.TRAINING_DETAIL,
                    arguments = listOf(navArgument("trainingId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val trainingId = backStackEntry.arguments?.getString("trainingId") ?: ""
                    TrainingDetailScreen(
                        trainingId = trainingId,
                        viewModel = trainingViewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                }

                // Matches
                composable(TrainerRoutes.MATCHES) {
                    MatchesScreen(
                        viewModel = matchViewModel,
                        onCreateMatchClick = { navController.navigate(TrainerRoutes.CREATE_MATCH) }
                    )
                }

                composable(TrainerRoutes.CREATE_MATCH) {
                    CreateMatchScreen(
                        viewModel = matchViewModel,
                        onBackClick = { navController.popBackStack() },
                        onMatchRegistered = { navController.popBackStack() }
                    )
                }

                // Community
                composable(TrainerRoutes.COMMUNITY) {
                    CommunityScreen(
                        viewModel = communityViewModel,
                        onCreatePostClick = { navController.navigate(TrainerRoutes.CREATE_POST) }
                    )
                }

                composable(TrainerRoutes.CREATE_POST) {
                    CreatePostScreen(
                        viewModel = communityViewModel,
                        onBackClick = { navController.popBackStack() },
                        onPostCreated = { navController.popBackStack() }
                    )
                }

                // Mi Perfil (HU-005)
                composable(TrainerRoutes.PROFILE) {
                    ProfileScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
