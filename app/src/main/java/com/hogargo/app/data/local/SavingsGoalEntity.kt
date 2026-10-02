package com.hogargo.app.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "savings_goal", indices = [Index("householdId")])
data class SavingsGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val householdId: String,
    val title: String,
    val currentAmount: Double,
    val targetAmount: Double,
)
