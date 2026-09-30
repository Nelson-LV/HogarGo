package com.hogargo.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: String,
    val title: String,
    val dateLabel: String,
    val timeLabel: String? = null,
    val done: Boolean = false,
)
