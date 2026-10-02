package com.hogargo.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events WHERE householdId = :householdId")
    fun getAllEvents(householdId: String): Flow<List<EventEntity>>

    @Query("DELETE FROM events WHERE householdId = :householdId")
    suspend fun deleteAllOfHousehold(householdId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Query("UPDATE events SET done = :done WHERE householdId = :householdId AND id = :id")
    suspend fun updateEventDone(householdId: String, id: String, done: Boolean)

    @Delete
    suspend fun deleteEvent(event: EventEntity)
}
