package com.hogargo.app.data.calendar

import com.hogargo.app.data.local.BillDao
import com.hogargo.app.data.local.BillEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class CalendarRepository(private val billDao: BillDao) {

    val bills: Flow<List<BillEntity>> = billDao.observeAll()

    suspend fun addBill(title: String, amount: Double, dueDate: LocalDate) {
        billDao.insert(BillEntity(title = title, amount = amount, dueDate = dueDate))
    }

    suspend fun togglePaid(bill: BillEntity) {
        billDao.update(bill.copy(paid = !bill.paid))
    }

    suspend fun deleteBill(bill: BillEntity) {
        billDao.delete(bill)
    }

    // TODO(tasks-integration): once the Tareas screen's HouseTask data moves to Room too,
    // add an `upcomingTasks: Flow<List<HouseTask>>` here (tasks with a due date, not completed)
    // so the Calendario screen can show a real "Próximas Tareas" section again instead of the
    // static mock that used to live here.
}
