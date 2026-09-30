package com.hogargo.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PetStateDao {
    @Query("SELECT * FROM pet_state WHERE id = 1")
    fun observe(): Flow<PetStateEntity?>

    @Query("SELECT * FROM pet_state WHERE id = 1")
    suspend fun getOnce(): PetStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(state: PetStateEntity)
}
