package com.hogargo.app.data.local

import androidx.room.Entity

/** Persisted per-item state for one household. The catalog (name/icon/unlock level) is static app content. */
@Entity(tableName = "wardrobe_items", primaryKeys = ["householdId", "id"])
data class WardrobeItemEntity(
    val householdId: String,
    val id: String,
    val equipped: Boolean = false,
    /** Bought with Zori coins; items must be owned before they can be worn. */
    val owned: Boolean = false,
)
