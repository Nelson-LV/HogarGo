package com.hogargo.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PetStateDao {
    @Query("SELECT * FROM pet_state WHERE householdId = :householdId")
    fun observe(householdId: String): Flow<PetStateEntity?>

    @Query("SELECT * FROM pet_state WHERE householdId = :householdId")
    suspend fun getOnce(householdId: String): PetStateEntity?

    @Query("DELETE FROM pet_state WHERE householdId = :householdId")
    suspend fun deleteAllOfHousehold(householdId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(state: PetStateEntity)
}
