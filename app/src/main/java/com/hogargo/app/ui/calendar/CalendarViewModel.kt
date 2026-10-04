package com.hogargo.app.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hogargo.app.data.calendar.CalendarRepository
import com.hogargo.app.data.local.BillEntity
import com.hogargo.app.data.local.EventEntity
import com.hogargo.app.data.local.TaskEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class CalendarViewModel(private val repository: CalendarRepository) : ViewModel() {

    val bills: StateFlow<List<BillEntity>> = repository.bills
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val events: StateFlow<List<EventEntity>> = repository.events
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val completedTasks: StateFlow<List<TaskEntity>> = repository.tasks
        .map { list -> list.filter { it.completed && it.completedDate != null } }
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

    fun toggleEventDone(eventId: String, currentDone: Boolean) {
        viewModelScope.launch { repository.toggleEventDone(eventId, currentDone) }
    }

    fun addEvent(title: String, date: LocalDate, timeLabel: String? = null) {
        viewModelScope.launch { repository.addEvent(title, date, timeLabel) }
    }

    fun deleteEvent(event: EventEntity) {
        viewModelScope.launch { repository.deleteEvent(event) }
    }

    companion object {
        fun factory(repository: CalendarRepository) = viewModelFactory {
            initializer { CalendarViewModel(repository) }
        }
    }
}
