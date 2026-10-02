package com.hogargo.app.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A family space. [code] is stored normalized (6 uppercase alphanumerics, no dash) and is
 * unique: it is the only thing a person needs to join this household and no other.
 */
@Entity(
    tableName = "households",
    indices = [Index(value = ["code"], unique = true)],
)
data class HouseholdEntity(
    @PrimaryKey val id: String,
    val name: String,
    val code: String,
    val createdAt: Long,
)
