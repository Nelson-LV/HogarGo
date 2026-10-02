package com.hogargo.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE householdId = :householdId")
    fun getAllTasks(householdId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE householdId = :householdId AND completed = 0 ORDER BY id ASC LIMIT 1")
    fun getNextPendingTask(householdId: String): Flow<TaskEntity?>

    @Query("SELECT * FROM tasks WHERE householdId = :householdId AND id = :id")
    suspend fun getTaskById(householdId: String, id: String): TaskEntity?

    /** Tasks of a removed member go back to "unassigned". */
    @Query("UPDATE tasks SET assigneeId = NULL WHERE householdId = :householdId AND assigneeId = :memberId")
    suspend fun clearAssignee(householdId: String, memberId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Query("UPDATE tasks SET completed = :completed, completedDate = :completedDate WHERE householdId = :householdId AND id = :id")
    suspend fun updateTaskCompleted(householdId: String, id: String, completed: Boolean, completedDate: String?)
}
