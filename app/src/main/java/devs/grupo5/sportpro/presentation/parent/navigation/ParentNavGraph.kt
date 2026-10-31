package devs.grupo5.sportpro.presentation.parent.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import devs.grupo5.sportpro.data.model.LinkedChild
import devs.grupo5.sportpro.data.repository.MockParentRepository
import devs.grupo5.sportpro.data.repository.ParentRepository
import devs.grupo5.sportpro.presentation.auth.AuthViewModel
import devs.grupo5.sportpro.presentation.parent.announcements.ParentAnnouncementsScreen
import devs.grupo5.sportpro.presentation.parent.calendar.ParentCalendarScreen
import devs.grupo5.sportpro.presentation.parent.children.ChildScreen
import devs.grupo5.sportpro.presentation.parent.components.ParentBottomNavigationBar
import devs.grupo5.sportpro.presentation.parent.components.ParentStatsFab
import devs.grupo5.sportpro.presentation.parent.components.ParentTab
import devs.grupo5.sportpro.presentation.parent.dashboard.ParentHomeScreen
import devs.grupo5.sportpro.presentation.parent.dialogs.ChildContactDialog
import devs.grupo5.sportpro.presentation.parent.dialogs.ChildPhysicalDataDialog
import devs.grupo5.sportpro.presentation.parent.dialogs.ChildStatsDialog
import devs.grupo5.sportpro.presentation.parent.dialogs.EditProfileDialog
import devs.grupo5.sportpro.presentation.parent.dialogs.NotificationsDialog
import devs.grupo5.sportpro.presentation.parent.dialogs.PaymentStatusDialog
import devs.grupo5.sportpro.presentation.parent.dialogs.RegisterChildDialog
import devs.grupo5.sportpro.presentation.parent.dialogs.SimpleInfoDialog
import devs.grupo5.sportpro.presentation.parent.profile.ParentProfileScreen
import devs.grupo5.sportpro.presentation.parent.theme.ParentBackground
import devs.grupo5.sportpro.presentation.parent.theme.SportProParentTheme

object ParentRoutes {
    const val HOME = "parent_home"
    const val CHILD = "parent_child"
    const val CALENDAR = "parent_calendar"
    const val ANNOUNCEMENTS = "parent_announcements"
    const val PROFILE = "parent_profile"
    const val CHILDREN_PRIVACY_LIST = "parent_children_privacy"
    const val CHILDREN_PRIVACY_DETAIL = "parent_children_privacy_detail/{childId}"
    
    fun childrenPrivacyDetail(childId: String) = "parent_children_privacy_detail/$childId"
}

@Composable
fun ParentMainContainerScreen(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    onLogoutSuccess: () -> Unit = {}
) {
    val appContext = LocalContext.current.applicationContext
    val parentRepository: ParentRepository = remember(appContext) {
        MockParentRepository.getInstance(appContext)
    }

    val currentUser by authViewModel.currentUser.collectAsState()
    val guardian by parentRepository.guardian.collectAsState()
    val linkedChildren by parentRepository.linkedChildren.collectAsState()
    val selectedChildId by parentRepository.selectedChildId.collectAsState()
    val selectedChild = linkedChildren.firstOrNull { it.id == selectedChildId }
        ?: linkedChildren.firstOrNull()
    val payment by parentRepository.payment.collectAsState()
    val notifications by parentRepository.notifications.collectAsState()

    var showChildDialog by rememberSaveable { mutableStateOf(false) }
    var childDialogTarget by remember { mutableStateOf<LinkedChild?>(null) }
    var showEditProfileDialog by rememberSaveable { mutableStateOf(false) }
    var showNotificationsDialog by rememberSaveable { mutableStateOf(false) }
    var showPaymentDialog by rememberSaveable { mutableStateOf(false) }
    var showStatsDialog by rememberSaveable { mutableStateOf(false) }
    var physicalDialogTarget by remember { mutableStateOf<LinkedChild?>(null) }
    var contactDialogTarget by remember { mutableStateOf<LinkedChild?>(null) }
    var createAccessTarget by remember { mutableStateOf<LinkedChild?>(null) }
    var eventMessage by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(currentUser) {
        currentUser?.let { parentRepository.initFrom(it) }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ParentRoutes.HOME
    val currentTab = when {
        currentRoute.startsWith(ParentRoutes.CHILD) && !currentRoute.startsWith("parent_children_privacy") -> ParentTab.CHILD
        currentRoute.startsWith(ParentRoutes.CALENDAR) -> ParentTab.CALENDAR
        currentRoute.startsWith(ParentRoutes.ANNOUNCEMENTS) -> ParentTab.ANNOUNCEMENTS
        currentRoute.startsWith(ParentRoutes.PROFILE) || currentRoute.startsWith("parent_children_privacy") -> ParentTab.PROFILE
        else -> ParentTab.HOME
    }

    SportProParentTheme {
        Scaffold(
            containerColor = ParentBackground,
            bottomBar = {
                ParentBottomNavigationBar(
                    currentTab = currentTab,
                    onTabSelected = { tab ->
                        navController.navigate(tab.route) {
                            popUpTo(ParentRoutes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            },
            floatingActionButton = {
                ParentStatsFab(onClick = { showStatsDialog = true })
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = ParentRoutes.HOME,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(ParentRoutes.HOME) {
                    ParentHomeScreen(
                        guardian = guardian,
                        child = selectedChild,
                        children = linkedChildren,
                        payment = payment,
                        unreadNotifications = notifications.count { !it.isRead },
                        onNotificationsClick = { showNotificationsDialog = true },
                        onRegisterChildClick = {
                            childDialogTarget = null
                            showChildDialog = true
                        },
                        onSelectChild = { parentRepository.selectChild(it) },
                        onPaymentClick = { showPaymentDialog = true },
                        onChildTabClick = { navController.navigate(ParentRoutes.CHILD) },
                        onCalendarTabClick = { navController.navigate(ParentRoutes.CALENDAR) },
                        onAnnouncementsTabClick = { navController.navigate(ParentRoutes.ANNOUNCEMENTS) }
                    )
                }

                composable(ParentRoutes.CHILD) {
                    ChildScreen(
                        children = linkedChildren,
                        selectedChild = selectedChild,
                        guardian = guardian,
                        onSelectChild = { parentRepository.selectChild(it) },
                        onRegisterChildClick = {
                            childDialogTarget = null
                            showChildDialog = true
                        },
                        onEditChildClick = { child ->
                            childDialogTarget = child
                            showChildDialog = true
                        },
                        onUnlinkChildClick = { child -> parentRepository.unlinkChild(child.id) },
                        onEditPhysicalDataClick = { child -> physicalDialogTarget = child },
                        onEditContactClick = { child -> contactDialogTarget = child },
                        onCreateAccessClick = { child -> createAccessTarget = child }
                    )
                }

                composable(ParentRoutes.CALENDAR) {
                    ParentCalendarScreen(
                        childCategory = selectedChild?.category.orEmpty(),
                        onEventClick = { event ->
                            eventMessage = "${event.type.label} · ${event.day} ${event.month} ${event.time}\n" +
                                "${event.title}\n${event.subtitle} · ${event.place}"
                        }
                    )
                }

                composable(ParentRoutes.ANNOUNCEMENTS) {
                    ParentAnnouncementsScreen(
                        onAnnouncementClick = { announcement ->
                            eventMessage = "${announcement.title}\n\n${announcement.body}"
                        }
                    )
                }

                composable(ParentRoutes.PROFILE) {
                    ParentProfileScreen(
                        guardian = guardian,
                        children = linkedChildren,
                        payment = payment,
                        unreadNotifications = notifications.count { !it.isRead },
                        onEditProfileClick = { showEditProfileDialog = true },
                        onNotificationsClick = { showNotificationsDialog = true },
                        onPrivacyClick = { navController.navigate(ParentRoutes.CHILDREN_PRIVACY_LIST) },
                        onPaymentClick = { showPaymentDialog = true },
                        onLogoutClick = onLogoutSuccess
                    )
                }
                
                composable(ParentRoutes.CHILDREN_PRIVACY_LIST) {
                    devs.grupo5.sportpro.presentation.parent.privacy.ChildrenPrivacyListScreen(
                        children = linkedChildren,
                        onBackClick = { navController.popBackStack() },
                        onChildSelected = { childId ->
                            navController.navigate(ParentRoutes.childrenPrivacyDetail(childId))
                        }
                    )
                }
                
                composable(
                    route = ParentRoutes.CHILDREN_PRIVACY_DETAIL,
                    arguments = listOf(androidx.navigation.navArgument("childId") { type = androidx.navigation.NavType.StringType })
                ) { backStackEntry ->
                    val childId = backStackEntry.arguments?.getString("childId") ?: ""
                    val selectedForPrivacy = linkedChildren.find { it.id == childId }
                    if (selectedForPrivacy != null) {
                        devs.grupo5.sportpro.presentation.parent.privacy.ChildPrivacyDetailScreen(
                            child = selectedForPrivacy,
                            onBackClick = { navController.popBackStack() },
                            onSaveClick = { updatedChild ->
                                parentRepository.updateChild(updatedChild)
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }

    if (showChildDialog) {
        RegisterChildDialog(
            initialChild = childDialogTarget,
            onDismiss = { showChildDialog = false },
            onSave = { child ->
                if (childDialogTarget == null) {
                    parentRepository.linkChild(child)
                } else {
                    parentRepository.updateChild(child)
                }
                showChildDialog = false
            }
        )
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            profile = guardian,
            onDismiss = { showEditProfileDialog = false },
            onSave = { profile ->
                parentRepository.updateGuardian(profile)
                showEditProfileDialog = false
            }
        )
    }

    if (showNotificationsDialog) {
        NotificationsDialog(
            notifications = notifications,
            onDismiss = { showNotificationsDialog = false },
            onMarkAsRead = {
                parentRepository.markNotificationsAsRead()
                showNotificationsDialog = false
            }
        )
    }

    if (showPaymentDialog) {
        PaymentStatusDialog(
            payment = payment,
            onDismiss = { showPaymentDialog = false },
            onStatusChange = { status -> parentRepository.updatePaymentStatus(status) },
            onUploadReceipt = { parentRepository.registerReceipt() }
        )
    }

    if (showStatsDialog) {
        ChildStatsDialog(
            child = selectedChild,
            onDismiss = { showStatsDialog = false }
        )
    }

    physicalDialogTarget?.let { child ->
        ChildPhysicalDataDialog(
            child = child,
            onDismiss = { physicalDialogTarget = null },
            onSave = { updated ->
                parentRepository.updateChild(updated)
                physicalDialogTarget = null
            }
        )
    }

    contactDialogTarget?.let { child ->
        ChildContactDialog(
            child = child,
            onDismiss = { contactDialogTarget = null },
            onSave = { updated ->
                parentRepository.updateChild(updated)
                contactDialogTarget = null
            }
        )
    }

    createAccessTarget?.let { child ->
        devs.grupo5.sportpro.presentation.parent.dialogs.CreateChildAccessDialog(
            child = child,
            onDismiss = { createAccessTarget = null }
        )
    }

    eventMessage?.let { message ->
        SimpleInfoDialog(
            title = "Detalle",
            message = message,
            onDismiss = { eventMessage = null }
        )
    }
}
