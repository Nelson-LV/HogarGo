package com.hogargo.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Persisted per-item state. The catalog (name/icon/unlock level) is static app content. */
@Entity(tableName = "wardrobe_items")
data class WardrobeItemEntity(
    @PrimaryKey val id: String,
    val equipped: Boolean = false,
)
