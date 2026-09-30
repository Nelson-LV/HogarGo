package com.hogargo.app.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hogargo.app.data.calendar.CalendarRepository
import com.hogargo.app.data.local.BillEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class CalendarViewModel(private val repository: CalendarRepository) : ViewModel() {

    val bills: StateFlow<List<BillEntity>> = repository.bills
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addBill(title: String, amount: Double, dueDate: LocalDate) {
        viewModelScope.launch { repository.addBill(title, amount, dueDate) }
    }

    fun togglePaid(bill: BillEntity) {
        viewModelScope.launch { repository.togglePaid(bill) }
    }

    fun deleteBill(bill: BillEntity) {
        viewModelScope.launch { repository.deleteBill(bill) }
    }

    companion object {
        fun factory(repository: CalendarRepository) = viewModelFactory {
            initializer { CalendarViewModel(repository) }
        }
    }
}
