package com.hogargo.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WardrobeItemDao {
    @Query("SELECT * FROM wardrobe_items WHERE householdId = :householdId")
    fun observeAll(householdId: String): Flow<List<WardrobeItemEntity>>

    @Query("SELECT * FROM wardrobe_items WHERE householdId = :householdId AND id = :id")
    suspend fun getOnce(householdId: String, id: String): WardrobeItemEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(items: List<WardrobeItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: WardrobeItemEntity)
}
