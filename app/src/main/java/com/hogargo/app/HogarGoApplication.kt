package com.hogargo.app

import android.app.Application
import com.hogargo.app.data.calendar.CalendarRepository
import com.hogargo.app.data.finance.FinanceRepository
import com.hogargo.app.data.household.HouseholdRepository
import com.hogargo.app.data.household.SessionStore
import com.hogargo.app.data.local.HogarGoDatabase
import com.hogargo.app.data.pet.PetRepository

/**
 * Minimal manual dependency container (no DI framework): builds the shared Room
 * database once and exposes the repositories. Everything that stores family data is
 * built *per household* so one home can never read or write another home's rows.
 * Tareas (AppViewModel) still talks to [database] directly as an AndroidViewModel.
 */
class HogarGoApplication : Application() {
    val database by lazy { HogarGoDatabase.getInstance(this) }

    val sessionStore by lazy { SessionStore(this) }
    val householdRepository by lazy { HouseholdRepository(database, sessionStore) }

    fun financeRepository(householdId: String) =
        FinanceRepository(householdId, database.expenseDao(), database.savingsGoalDao())

    fun petRepository(householdId: String) =
        PetRepository(householdId, database.petStateDao(), database.wardrobeItemDao(), database.taskDao())

    fun calendarRepository(householdId: String) =
        CalendarRepository(householdId, database.billDao(), database.eventDao(), database.taskDao())
}
