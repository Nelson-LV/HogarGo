package com.hogargo.app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hogargo.app.HogarGoApplication
import com.hogargo.app.R
import com.hogargo.app.data.AppViewModel
import com.hogargo.app.data.household.ActiveSession
import com.hogargo.app.ui.about.AboutScreen
import com.hogargo.app.ui.calendar.CalendarScreen
import com.hogargo.app.ui.calendar.CalendarViewModel
import com.hogargo.app.ui.finance.FinanceScreen
import com.hogargo.app.ui.finance.FinanceViewModel
import com.hogargo.app.ui.home.HomeScreen
import com.hogargo.app.ui.household.CreateHouseholdScreen
import com.hogargo.app.ui.household.JoinHouseholdScreen
import com.hogargo.app.ui.household.ProfileDialog
import com.hogargo.app.ui.household.SessionState
import com.hogargo.app.ui.household.SessionViewModel
import com.hogargo.app.ui.household.WelcomeScreen
import com.hogargo.app.ui.newtask.NewTaskScreen
import com.hogargo.app.ui.pet.PetScreen
import com.hogargo.app.ui.pet.PetViewModel
import com.hogargo.app.ui.tasks.TasksScreen

/**
 * Entry point of the UI. Flow: Welcome -> (Create | Join) -> Home. A person that was already
 * signed in goes straight to Home; "Cerrar sesión" in their profile brings them back to Welcome.
 */
@Composable
fun HogarGoApp() {
    val application = LocalContext.current.applicationContext as HogarGoApplication
    val sessionViewModel: SessionViewModel = viewModel(
        factory = SessionViewModel.factory(application.householdRepository),
    )
    val sessionState by sessionViewModel.state.collectAsState()

    // Wait for the (very quick) "was somebody signed in?" check so we never flash the wrong screen.
    if (sessionState is SessionState.Loading) {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        )
        return
    }

    HogarGoAppContent(
        sessionViewModel = sessionViewModel,
        startLoggedIn = sessionState is SessionState.LoggedIn,
    )
}

@Composable
private fun HogarGoAppContent(sessionViewModel: SessionViewModel, startLoggedIn: Boolean) {
    val navController = rememberNavController()
    val application = LocalContext.current.applicationContext as HogarGoApplication
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    val sessionState by sessionViewModel.state.collectAsState()
    val activeSession = (sessionState as? SessionState.LoggedIn)?.session

    // Keep the last session around while the sign-out transition plays, so screens don't blank out.
    var lastSession by remember { mutableStateOf<ActiveSession?>(null) }
    LaunchedEffect(activeSession) {
        if (activeSession != null) lastSession = activeSession
    }
    val session = activeSession ?: lastSession

    var showProfile by rememberSaveable { mutableStateOf(false) }

    val isAuthRoute = currentRoute == Routes.WELCOME || currentRoute == Routes.JOIN || currentRoute == Routes.CREATE
    val showBottomBar = currentDestination?.hierarchy?.any { dest ->
        BottomNavDestinations.any { it.route == dest.route }
    } ?: false

    // Everything below that stores family data is built for this household only.
    val appViewModel: AppViewModel? = session?.let { current ->
        val householdId = current.household.id
        val memberId = current.member.id
        viewModel(
            key = "app_${householdId}_$memberId",
            factory = viewModelFactory {
                initializer { AppViewModel(application, householdId, memberId) }
            },
        )
    }

    val goHome: () -> Unit = {
        navController.navigate(Routes.HOME) {
            popUpTo(Routes.WELCOME) { inclusive = true }
            launchSingleTop = true
        }
    }

    if (showProfile && session != null && appViewModel != null) {
        val uiState by appViewModel.uiState.collectAsState()
        ProfileDialog(
            session = session,
            members = uiState.members,
            onDismiss = { showProfile = false },
            onSignOut = {
                showProfile = false
                sessionViewModel.signOut()
                navController.navigate(Routes.WELCOME) {
                    popUpTo(Routes.HOME) { inclusive = true }
                    launchSingleTop = true
                }
            },
        )
    }

    Scaffold(
        topBar = {
            when (currentRoute) {
                Routes.WELCOME -> Unit
                Routes.JOIN -> HogarGoDetailTopBar(
                    title = stringResource(R.string.join_title),
                    onBack = { navController.popBackStack() },
                )
                Routes.CREATE -> HogarGoDetailTopBar(
                    title = stringResource(R.string.create_title),
                    onBack = { navController.popBackStack() },
                )
                Routes.NEW_TASK -> HogarGoDetailTopBar(
                    title = stringResource(R.string.new_task_title),
                    onBack = { navController.popBackStack() },
                )
                Routes.ABOUT -> HogarGoDetailTopBar(
                    title = stringResource(R.string.about_title),
                    onBack = { navController.popBackStack() },
                )
                else -> HogarGoTopBar(
                    memberName = session?.member?.name.orEmpty(),
                    onProfileClick = { showProfile = true },
                )
            }
        },
        bottomBar = {
            if (showBottomBar && !isAuthRoute) {
                HogarGoBottomBar(navController = navController, currentRoute = currentRoute)
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (startLoggedIn) Routes.HOME else Routes.WELCOME,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { hogarGoEnter() },
            exitTransition = { hogarGoExit() },
            popEnterTransition = { hogarGoEnter() },
            popExitTransition = { hogarGoExit() },
        ) {
            // ---- Pre-login flow
            composable(Routes.WELCOME) {
                WelcomeScreen(
                    onCreateHousehold = { navController.navigate(Routes.CREATE) },
                    onJoinHousehold = { navController.navigate(Routes.JOIN) },
                    onSignIn = { name, code -> sessionViewModel.signIn(name, code) },
                    onSignedIn = goHome,
                )
            }
            composable(Routes.CREATE) {
                CreateHouseholdScreen(
                    generateCode = { sessionViewModel.generateUniqueCode() },
                    onCreate = { userName, householdName, code ->
                        sessionViewModel.createHousehold(userName, householdName, code)
                    },
                    onCreated = goHome,
                    onJoinInstead = {
                        navController.navigate(Routes.JOIN) {
                            popUpTo(Routes.WELCOME)
                            launchSingleTop = true
                        }
                    },
                )
            }
            composable(Routes.JOIN) {
                JoinHouseholdScreen(
                    onJoin = { name, code -> sessionViewModel.joinHousehold(name, code) },
                    onJoined = goHome,
                    onCreateInstead = {
                        navController.navigate(Routes.CREATE) {
                            popUpTo(Routes.WELCOME)
                            launchSingleTop = true
                        }
                    },
                )
            }

            // ---- Main app (always inside one household)
            composable(Routes.HOME) {
                if (session != null && appViewModel != null) {
                    HomeScreen(
                        appViewModel = appViewModel,
                        householdId = session.household.id,
                        onNewTask = { navController.navigate(Routes.NEW_TASK) },
                        onOpenFinance = { navController.navigateToTab(Routes.FINANCE) },
                        onOpenPet = { navController.navigateToTab(Routes.PET) },
                        onOpenAbout = { navController.navigate(Routes.ABOUT) },
                    )
                }
            }
            composable(Routes.TASKS) {
                if (appViewModel != null) {
                    TasksScreen(
                        appViewModel = appViewModel,
                        onProposeNewTask = { navController.navigate(Routes.NEW_TASK) },
                    )
                }
            }
            composable(Routes.NEW_TASK) {
                if (appViewModel != null) {
                    NewTaskScreen(
                        appViewModel = appViewModel,
                        onBack = { navController.popBackStack() },
                    )
                }
            }
            composable(Routes.ABOUT) {
                AboutScreen()
            }
            composable(Routes.FINANCE) {
                if (session != null) {
                    val financeViewModel: FinanceViewModel = viewModel(
                        key = "finance_${session.household.id}",
                        factory = FinanceViewModel.factory(application.financeRepository(session.household.id)),
                    )
                    FinanceScreen(viewModel = financeViewModel)
                }
            }
            composable(Routes.PET) {
                if (session != null) {
                    val petViewModel: PetViewModel = viewModel(
                        key = "pet_${session.household.id}",
                        factory = PetViewModel.factory(application.petRepository(session.household.id)),
                    )
                    PetScreen(viewModel = petViewModel)
                }
            }
            composable(Routes.CALENDAR) {
                if (session != null) {
                    val calendarViewModel: CalendarViewModel = viewModel(
                        key = "calendar_${session.household.id}",
                        factory = CalendarViewModel.factory(application.calendarRepository(session.household.id)),
                    )
                    CalendarScreen(viewModel = calendarViewModel)
                }
            }
        }
    }
}

@Composable
private fun HogarGoBottomBar(navController: NavController, currentRoute: String?) {
    NavigationBar {
        BottomNavDestinations.forEach { destination ->
            val selected = currentRoute == destination.route
            NavigationBarItem(
                selected = selected,
                onClick = { navController.navigateToTab(destination.route) },
                icon = {
                    Icon(
                        imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                        contentDescription = null,
                    )
                },
                label = { Text(stringResource(destination.labelRes)) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            )
        }
    }
}

/** Tabs stack on top of Home (the root of the signed-in flow), not on top of Welcome. */
private fun NavController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(Routes.HOME) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
