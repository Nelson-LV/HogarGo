package com.hogargo.app.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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

data class SavingsGoal(
    @StringRes val titleRes: Int,
    val current: Int,
    val target: Int,
)

