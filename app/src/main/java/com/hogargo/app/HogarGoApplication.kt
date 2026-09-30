package com.hogargo.app

import android.app.Application
import com.hogargo.app.data.finance.FinanceRepository
import com.hogargo.app.data.local.HogarGoDatabase

/**
 * Minimal manual dependency container (no DI framework): builds the shared Room
 * database once and exposes per-feature repositories. Add your feature's repository
 * here the same way as [financeRepository] when Mascota/Calendario go real.
 */
class HogarGoApplication : Application() {
    private val database by lazy { HogarGoDatabase.getInstance(this) }

    val financeRepository by lazy { FinanceRepository(database.expenseDao(), database.savingsGoalDao()) }
}
