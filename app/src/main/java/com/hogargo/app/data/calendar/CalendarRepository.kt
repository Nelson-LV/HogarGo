package com.hogargo.app.data.calendar

import com.hogargo.app.data.local.BillDao
import com.hogargo.app.data.local.BillEntity
import com.hogargo.app.data.local.EventDao
import com.hogargo.app.data.local.EventEntity
import com.hogargo.app.data.local.TaskDao
import com.hogargo.app.data.local.TaskEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

class CalendarRepository(
    private val householdId: String,
    private val billDao: BillDao,
    private val eventDao: EventDao,
    private val taskDao: TaskDao,
) {
    val bills: Flow<List<BillEntity>> = billDao.observeAll(householdId)
    val events: Flow<List<EventEntity>> = eventDao.getAllEvents(householdId)
    val tasks: Flow<List<TaskEntity>> = taskDao.getAllTasks(householdId)

    suspend fun addBill(title: String, amount: Double, dueDate: LocalDate) {
        billDao.insert(BillEntity(householdId = householdId, title = title, amount = amount, dueDate = dueDate))
    }

    suspend fun togglePaid(bill: BillEntity) {
        billDao.update(bill.copy(paid = !bill.paid))
    }

    suspend fun deleteBill(bill: BillEntity) {
        billDao.delete(bill)
    }

    suspend fun toggleEventDone(eventId: String, currentDone: Boolean) {
        eventDao.updateEventDone(householdId, eventId, !currentDone)
    }

    suspend fun addEvent(title: String, date: LocalDate, timeLabel: String? = null) {
        eventDao.insertEvent(
            EventEntity(
                householdId = householdId,
                id = UUID.randomUUID().toString(),
                title = title,
                dateLabel = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                timeLabel = timeLabel?.takeIf { it.isNotBlank() },
                done = false,
                date = date,
            ),
        )
    }

    suspend fun deleteEvent(event: EventEntity) {
        eventDao.deleteEvent(event)
    }
}
