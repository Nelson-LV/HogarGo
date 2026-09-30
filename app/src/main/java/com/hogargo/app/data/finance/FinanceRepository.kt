package com.hogargo.app.data.finance

import com.hogargo.app.data.local.ExpenseCategory
import com.hogargo.app.data.local.ExpenseDao
import com.hogargo.app.data.local.ExpenseEntity
import com.hogargo.app.data.local.SavingsGoalDao
import com.hogargo.app.data.local.SavingsGoalEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class FinanceRepository(
    private val expenseDao: ExpenseDao,
    private val savingsGoalDao: SavingsGoalDao,
) {
    val expenses: Flow<List<ExpenseEntity>> = expenseDao.observeAll()
    val savingsGoal: Flow<SavingsGoalEntity?> = savingsGoalDao.observeCurrent()

    suspend fun addExpense(title: String, category: ExpenseCategory, amount: Double, date: LocalDate = LocalDate.now()) {
        expenseDao.insert(ExpenseEntity(title = title, category = category, amount = amount, date = date))
    }

    suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.delete(expense)
    }

    suspend fun createOrRenameGoal(title: String, targetAmount: Double) {
        val current = savingsGoalDao.observeCurrent().first()
        savingsGoalDao.upsert(
            SavingsGoalEntity(
                id = current?.id ?: 0,
                title = title,
                currentAmount = current?.currentAmount ?: 0.0,
                targetAmount = targetAmount,
            ),
        )
    }

    suspend fun contribute(amount: Double) {
        val current = savingsGoalDao.observeCurrent().first() ?: return
        savingsGoalDao.upsert(current.copy(currentAmount = current.currentAmount + amount))
    }
}
