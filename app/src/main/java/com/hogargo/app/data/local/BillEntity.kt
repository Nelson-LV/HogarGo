package com.hogargo.app.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "bills", indices = [Index("householdId")])
data class BillEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val householdId: String,
    val title: String,
    val amount: Double,
    val dueDate: LocalDate,
    val paid: Boolean = false,
)
