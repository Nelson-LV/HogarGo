package com.hogargo.app.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.ui.graphics.vector.ImageVector
import com.hogargo.app.R

/** Routes used by the NavHost. NEW_TASK has no bottom-nav entry (opened on top of Home/Tasks). */
object Routes {
    const val HOME = "home"
    const val TASKS = "tasks"
    const val FINANCE = "finance"
    const val PET = "pet"
    const val CALENDAR = "calendar"
    const val NEW_TASK = "new_task"
}

data class BottomNavDestination(
    val route: String,
    @StringRes val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

val BottomNavDestinations = listOf(
    BottomNavDestination(Routes.HOME, R.string.nav_home, Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavDestination(Routes.TASKS, R.string.nav_tasks, Icons.Filled.Checklist, Icons.Outlined.Checklist),
    BottomNavDestination(Routes.FINANCE, R.string.nav_finance, Icons.Filled.Savings, Icons.Outlined.Savings),
    BottomNavDestination(Routes.PET, R.string.nav_pet, Icons.Filled.Pets, Icons.Outlined.Pets),
    BottomNavDestination(Routes.CALENDAR, R.string.nav_calendar, Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
)
