package com.hogargo.app.data.finance

import com.hogargo.app.data.local.ExpenseCategory
import com.hogargo.app.data.local.ExpenseDao
import com.hogargo.app.data.local.ExpenseEntity
import com.hogargo.app.data.local.SavingsGoalDao
import com.hogargo.app.data.local.SavingsGoalEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class FinanceRepository(
    private val householdId: String,
    private val expenseDao: ExpenseDao,
    private val savingsGoalDao: SavingsGoalDao,
) {
    val expenses: Flow<List<ExpenseEntity>> = expenseDao.observeAll(householdId)
    val savingsGoals: Flow<List<SavingsGoalEntity>> = savingsGoalDao.observeAll(householdId)

    suspend fun addExpense(title: String, category: ExpenseCategory, amount: Double, date: LocalDate = LocalDate.now()) {
        expenseDao.insert(ExpenseEntity(householdId = householdId, title = title, category = category, amount = amount, date = date))
    }

    suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.delete(expense)
    }

    suspend fun addGoal(title: String, targetAmount: Double) {
        savingsGoalDao.upsert(SavingsGoalEntity(householdId = householdId, title = title, currentAmount = 0.0, targetAmount = targetAmount))
    }

    suspend fun deleteGoal(goal: SavingsGoalEntity) {
        savingsGoalDao.delete(goal)
    }

    suspend fun contribute(goalId: Long, amount: Double) {
        val goal = savingsGoalDao.getById(householdId, goalId) ?: return
        savingsGoalDao.upsert(goal.copy(currentAmount = goal.currentAmount + amount))
    }
}
