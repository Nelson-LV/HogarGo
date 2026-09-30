package com.hogargo.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val merchant: String,
    val category: String,
    val amount: Double,
    val dateLabel: String,
)
