package com.hogargo.app

import android.app.Application
import com.hogargo.app.data.calendar.CalendarRepository
import com.hogargo.app.data.finance.FinanceRepository
import com.hogargo.app.data.local.HogarGoDatabase
import com.hogargo.app.data.pet.PetRepository

/**
 * Minimal manual dependency container (no DI framework): builds the shared Room
 * database once and exposes per-feature repositories. Tareas (AppViewModel) still
 * talks to [database] directly as an AndroidViewModel — it predates this container
 * and wasn't worth a disruptive rewrite during the integration merge.
 */
class HogarGoApplication : Application() {
    private val database by lazy { HogarGoDatabase.getInstance(this) }

    val financeRepository by lazy { FinanceRepository(database.expenseDao(), database.savingsGoalDao()) }
    val petRepository by lazy { PetRepository(database.petStateDao(), database.wardrobeItemDao(), database.taskDao()) }
    val calendarRepository by lazy { CalendarRepository(database.billDao(), database.eventDao()) }
}
