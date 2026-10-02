package com.hogargo.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingsGoalDao {
    @Query("SELECT * FROM savings_goal WHERE householdId = :householdId ORDER BY id ASC")
    fun observeAll(householdId: String): Flow<List<SavingsGoalEntity>>

    @Query("SELECT * FROM savings_goal WHERE householdId = :householdId AND id = :id")
    suspend fun getById(householdId: String, id: Long): SavingsGoalEntity?

    @Delete
    suspend fun delete(goal: SavingsGoalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(goal: SavingsGoalEntity)
}
