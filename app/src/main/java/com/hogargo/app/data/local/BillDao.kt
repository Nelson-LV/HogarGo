package com.hogargo.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BillDao {
    @Query("SELECT * FROM bills WHERE householdId = :householdId ORDER BY dueDate ASC")
    fun observeAll(householdId: String): Flow<List<BillEntity>>

    @Query("DELETE FROM bills WHERE householdId = :householdId")
    suspend fun deleteAllOfHousehold(householdId: String)

    @Insert
    suspend fun insert(bill: BillEntity)

    @Update
    suspend fun update(bill: BillEntity)

    @Delete
    suspend fun delete(bill: BillEntity)
}
