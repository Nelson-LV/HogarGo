package com.hogargo.app.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A person that belongs to exactly one household. [nameKey] is the lower-cased name and,
 * together with [householdId], must be unique so two members can't be confused.
 */
@Entity(
    tableName = "members",
    indices = [Index(value = ["householdId", "nameKey"], unique = true)],
)
data class MemberEntity(
    @PrimaryKey val id: String,
    val householdId: String,
    val name: String,
    val nameKey: String,
    val isAdmin: Boolean = false,
    /** false = joined with the code but still waiting for the admin to accept them. */
    val isApproved: Boolean = true,
    /** SHA-256 of the recovery user (see RecoveryUser). Empty = not configured yet. */
    val recoveryKey: String = "",
    val createdAt: Long,
)
