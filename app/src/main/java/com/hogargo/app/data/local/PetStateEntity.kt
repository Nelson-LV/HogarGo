package com.hogargo.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** One row per household (keyed by [householdId]) holding that home's Zori stats. */
@Entity(tableName = "pet_state")
data class PetStateEntity(
    @PrimaryKey val householdId: String,
    val happiness: Float = 0.5f,
    val satiety: Float = 0.5f,
    val careActions: Int = 0,
    val lastFeedAt: Long = 0L,
    val lastPlayAt: Long = 0L,
    val lastDecayAt: Long,
) {
    val level: Int get() = 1 + careActions / 5
}
