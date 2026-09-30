package com.hogargo.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Singleton row (id is always 1) holding Zori's live stats. */
@Entity(tableName = "pet_state")
data class PetStateEntity(
    @PrimaryKey val id: Int = 1,
    val happiness: Float = 0.5f,
    val satiety: Float = 0.5f,
    val careActions: Int = 0,
    val lastFeedAt: Long = 0L,
    val lastPlayAt: Long = 0L,
    val lastDecayAt: Long,
) {
    val level: Int get() = 1 + careActions / 5
}
