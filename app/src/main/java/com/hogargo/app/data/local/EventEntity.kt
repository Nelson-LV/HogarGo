package com.hogargo.app.data.local

import androidx.room.Entity

@Entity(tableName = "events", primaryKeys = ["householdId", "id"])
data class EventEntity(
    val householdId: String,
    val id: String,
    val title: String,
    val dateLabel: String,
    val timeLabel: String? = null,
    val done: Boolean = false,
)
