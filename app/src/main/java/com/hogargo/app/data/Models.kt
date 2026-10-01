package com.hogargo.app.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
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
    @StringRes val titleRes: Int? = null,
    val titleText: String? = null,
    val category: TaskCategory,
    val minutes: Int? = null,
    val dueTime: String? = null,
    val completed: Boolean,
    val completedDate: String? = null,
    val assigneeId: String? = null,
    val coinReward: Int,
)

@Composable
fun HouseTask.getDisplayTitle(): String {
    return titleRes?.let { stringResource(it) } ?: titleText ?: ""
}
