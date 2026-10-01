package com.hogargo.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hogargo.app.HogarGoApplication
import com.hogargo.app.R
import com.hogargo.app.data.AppViewModel
import com.hogargo.app.ui.calendar.CalendarScreen
import com.hogargo.app.ui.calendar.CalendarViewModel
import com.hogargo.app.ui.finance.FinanceScreen
import com.hogargo.app.ui.finance.FinanceViewModel
import com.hogargo.app.ui.home.HomeScreen
import com.hogargo.app.ui.newtask.NewTaskScreen
import com.hogargo.app.ui.pet.PetScreen
import com.hogargo.app.ui.pet.PetViewModel
import com.hogargo.app.ui.tasks.TasksScreen

@Composable
fun HogarGoApp(appViewModel: AppViewModel = viewModel()) {
    val navController = rememberNavController()
    val application = LocalContext.current.applicationContext as HogarGoApplication
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination

    val showBottomBar = currentRoute?.hierarchy?.any { dest ->
        BottomNavDestinations.any { it.route == dest.route }
    } ?: true

    Scaffold(
        topBar = {
            if (currentRoute?.route == Routes.NEW_TASK) {
                HogarGoDetailTopBar(
                    title = stringResource(R.string.new_task_title),
                    onBack = { navController.popBackStack() },
                )
            } else {
                HogarGoTopBar()
            }
        },
        bottomBar = {
            if (showBottomBar) {
                HogarGoBottomBar(navController = navController, currentRoute = currentRoute?.route)
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    appViewModel = appViewModel,
                    onNewTask = { navController.navigate(Routes.NEW_TASK) },
                )
            }
            composable(Routes.TASKS) {
                TasksScreen(
                    appViewModel = appViewModel,
                    onProposeNewTask = { navController.navigate(Routes.NEW_TASK) },
                )
            }
            composable(Routes.NEW_TASK) {
                NewTaskScreen(
                    appViewModel = appViewModel,
                    onBack = { navController.popBackStack() },
                )
            }
            composable(Routes.FINANCE) {
                val financeViewModel: FinanceViewModel = viewModel(factory = FinanceViewModel.factory(application.financeRepository))
                FinanceScreen(viewModel = financeViewModel)
            }
            composable(Routes.PET) {
                val petViewModel: PetViewModel = viewModel(factory = PetViewModel.factory(application.petRepository))
                PetScreen(viewModel = petViewModel)
            }
            composable(Routes.CALENDAR) {
                val calendarViewModel: CalendarViewModel = viewModel(factory = CalendarViewModel.factory(application.calendarRepository))
                CalendarScreen(viewModel = calendarViewModel)
            }
        }
    }
}

@Composable
private fun HogarGoBottomBar(navController: androidx.navigation.NavController, currentRoute: String?) {
    NavigationBar {
        BottomNavDestinations.forEach { destination ->
            val selected = currentRoute == destination.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
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
