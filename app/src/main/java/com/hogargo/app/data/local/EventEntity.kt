package com.hogargo.app.data.local

import androidx.room.Entity
import java.time.LocalDate

@Entity(tableName = "events", primaryKeys = ["householdId", "id"])
data class EventEntity(
    val householdId: String,
    val id: String,
    val title: String,
    /** Human-readable date kept for display; [date] is the real value the calendar uses. */
    val dateLabel: String,
    val timeLabel: String? = null,
    val done: Boolean = false,
    /** null only for events created before the calendar had real dates and whose label couldn't be parsed. */
    val date: LocalDate? = null,
)
