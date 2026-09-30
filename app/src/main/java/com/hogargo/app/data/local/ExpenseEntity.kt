package com.hogargo.app.data.local

import androidx.annotation.StringRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hogargo.app.R
import java.time.LocalDate

enum class ExpenseCategory(@StringRes val labelRes: Int) {
    GROCERIES(R.string.expense_category_groceries),
    BILLS(R.string.expense_category_bills),
    PET(R.string.expense_category_pet),
    LEISURE(R.string.expense_category_leisure),
}

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: ExpenseCategory,
    val amount: Double,
    val date: LocalDate,
)
