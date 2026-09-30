package com.hogargo.app

import android.app.Application
import com.hogargo.app.data.finance.FinanceRepository
import com.hogargo.app.data.local.HogarGoDatabase
import com.hogargo.app.data.pet.PetRepository

/**
 * Minimal manual dependency container (no DI framework): builds the shared Room
 * database once and exposes per-feature repositories. Add your feature's repository
 * here the same way as [financeRepository]/[petRepository] when Calendario goes real.
 */
class HogarGoApplication : Application() {
    private val database by lazy { HogarGoDatabase.getInstance(this) }

    val financeRepository by lazy { FinanceRepository(database.expenseDao(), database.savingsGoalDao()) }
    val petRepository by lazy { PetRepository(database.petStateDao(), database.wardrobeItemDao()) }
}
