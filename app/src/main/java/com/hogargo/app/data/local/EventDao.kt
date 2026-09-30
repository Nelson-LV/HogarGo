package com.hogargo.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)

    @Query("UPDATE events SET done = :done WHERE id = :id")
    suspend fun updateEventDone(id: String, done: Boolean)

    @Query("SELECT COUNT(*) FROM events")
    suspend fun getEventCount(): Int

    @Delete
    suspend fun deleteEvent(event: EventEntity)
}
