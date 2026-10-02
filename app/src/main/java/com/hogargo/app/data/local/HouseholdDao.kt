package com.hogargo.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HouseholdDao {
    @Query("SELECT * FROM households WHERE code = :code LIMIT 1")
    suspend fun findByCode(code: String): HouseholdEntity?

    @Query("SELECT * FROM households WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): HouseholdEntity?

    @Query("SELECT * FROM households WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<HouseholdEntity?>

    @Query("DELETE FROM households WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE households SET name = :name WHERE id = :id")
    suspend fun updateName(id: String, name: String)

    /** Returns -1 when the unique code is already taken (nothing is inserted). */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(household: HouseholdEntity): Long
}
