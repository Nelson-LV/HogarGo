package com.hogargo.app.data

import com.hogargo.app.R

val FamilyMembers = listOf(
    FamilyMember("mom", R.string.member_mom, R.drawable.avatar_mama),
    FamilyMember("dad", R.string.member_dad, R.drawable.avatar_papa),
    FamilyMember("kid", R.string.member_kid, R.drawable.avatar_leo),
)

fun familyMember(id: String?): FamilyMember? = FamilyMembers.find { it.id == id }

val InitialTasks = listOf(
    HouseTask(
        id = "empty_dishwasher",
        titleRes = R.string.task_empty_dishwasher,
        category = TaskCategory.KITCHEN,
        dueTime = "5 PM",
        completed = false,
        assigneeId = "mom",
        coinReward = 15,
    ),
    HouseTask(
        id = "water_tomatoes",
        titleRes = R.string.task_water_tomatoes,
        category = TaskCategory.GARDEN,
        completed = false,
        assigneeId = "mom",
        coinReward = 20,
    ),
    HouseTask(
        id = "fold_laundry",
        titleRes = R.string.task_fold_laundry,
        category = TaskCategory.GENERAL,
        completed = false,
        assigneeId = null,
        coinReward = 30,
    ),
)
