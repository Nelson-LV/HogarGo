package com.hogargo.app.ui.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hogargo.app.data.finance.FinanceRepository
import com.hogargo.app.data.local.ExpenseCategory
import com.hogargo.app.data.local.ExpenseEntity
import com.hogargo.app.data.local.SavingsGoalEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class FinanceUiState(
    val expenses: List<ExpenseEntity> = emptyList(),
    val savingsGoal: SavingsGoalEntity? = null,
) {
    val totalSpent: Double get() = expenses.sumOf { it.amount }
    val averageExpense: Double get() = if (expenses.isEmpty()) 0.0 else totalSpent / expenses.size
    val breakdown: List<Pair<ExpenseCategory, Double>>
        get() = expenses
            .groupBy { it.category }
            .mapValues { (_, items) -> items.sumOf { it.amount } }
            .toList()
            .sortedByDescending { it.second }
}

class FinanceViewModel(private val repository: FinanceRepository) : ViewModel() {

    val uiState: StateFlow<FinanceUiState> = combine(repository.expenses, repository.savingsGoal) { expenses, goal ->
        FinanceUiState(expenses = expenses, savingsGoal = goal)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FinanceUiState())

    fun addExpense(title: String, category: ExpenseCategory, amount: Double, date: LocalDate = LocalDate.now()) {
        viewModelScope.launch { repository.addExpense(title, category, amount, date) }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch { repository.deleteExpense(expense) }
    }

    fun createOrRenameGoal(title: String, targetAmount: Double) {
        viewModelScope.launch { repository.createOrRenameGoal(title, targetAmount) }
    }

    fun contribute(amount: Double) {
        viewModelScope.launch { repository.contribute(amount) }
    }

    companion object {
        fun factory(repository: FinanceRepository) = viewModelFactory {
            initializer { FinanceViewModel(repository) }
        }
    }
}
