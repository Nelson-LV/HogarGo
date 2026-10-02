package com.hogargo.app.data.local

import androidx.annotation.StringRes
import androidx.room.Entity
import com.hogargo.app.data.HouseTask
import com.hogargo.app.data.TaskCategory

@Entity(tableName = "tasks", primaryKeys = ["householdId", "id"])
data class TaskEntity(
    val householdId: String,
    val id: String,
    @get:StringRes val titleRes: Int? = null,
    val titleText: String? = null,
    val category: TaskCategory,
    val minutes: Int? = null,
    val dueTime: String? = null,
    val completed: Boolean = false,
    val completedDate: String? = null,
    val assigneeId: String? = null,
    val coinReward: Int = 10,
)

fun TaskEntity.toHouseTask(): HouseTask {
    return HouseTask(
        id = id,
        titleRes = titleRes,
        titleText = titleText,
        category = category,
        minutes = minutes,
        dueTime = dueTime,
        completed = completed,
        completedDate = completedDate,
        assigneeId = assigneeId,
        coinReward = coinReward,
    )
}

fun HouseTask.toTaskEntity(householdId: String): TaskEntity {
    return TaskEntity(
        householdId = householdId,
        id = id,
        titleRes = titleRes,
        titleText = titleText,
        category = category,
        minutes = minutes,
        dueTime = dueTime,
        completed = completed,
        completedDate = completedDate,
        assigneeId = assigneeId,
        coinReward = coinReward,
    )
}
