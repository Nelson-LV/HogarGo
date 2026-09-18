package com.hogargo.app.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.hogargo.app.R

enum class TaskCategory(@StringRes val labelRes: Int) {
    KITCHEN(R.string.category_kitchen),
    GARDEN(R.string.category_garden),
    GENERAL(R.string.category_general),
    CLEANING(R.string.category_cleaning),
}

data class FamilyMember(
    val id: String,
    @StringRes val nameRes: Int,
    @DrawableRes val avatarRes: Int,
)

data class HouseTask(
    val id: String,
    @StringRes val titleRes: Int,
    val category: TaskCategory,
    val minutes: Int? = null,
    val dueTime: String? = null,
    val completed: Boolean,
    val assigneeId: String? = null,
    val coinReward: Int,
)

data class Expense(
    val id: String,
    @StringRes val merchantRes: Int,
    @StringRes val categoryRes: Int,
    val amount: Double,
    @StringRes val dateRes: Int,
    val icon: ImageVector,
    val iconBackground: Color,
)

data class ExpenseCategoryShare(
    @StringRes val labelRes: Int,
    val amount: Double,
    val fraction: Float,
    val color: Color,
)

data class Bill(
    val id: String,
    @StringRes val nameRes: Int,
    val amount: Double,
    val dueToday: Boolean,
    val dateLabel: String? = null,
    val icon: ImageVector,
    val iconBackground: Color,
)

data class SavingsGoal(
    @StringRes val titleRes: Int,
    val current: Int,
    val target: Int,
)

data class CalendarDayInfo(
    val day: Int,
    val dotColors: List<Color> = emptyList(),
)

data class UpcomingChore(
    @StringRes val titleRes: Int,
    val whenLabelKey: WhenLabel,
    val done: Boolean = false,
)

enum class WhenLabel { TODAY_4PM, TOMORROW }

data class PetWardrobeItem(
    val id: String,
    @StringRes val nameRes: Int,
    val icon: ImageVector,
    val unlocked: Boolean,
    val equipped: Boolean = false,
    val tasksRemaining: Int? = null,
)

data class PetState(
    val level: Int,
    val happiness: Float,
    val satiety: Float,
)
