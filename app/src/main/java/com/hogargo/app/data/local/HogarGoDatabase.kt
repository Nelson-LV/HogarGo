package com.hogargo.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Single shared Room database for the app. Each teammate's feature adds its own
 * entity/DAO files under data/local and registers them here — expect this file
 * (and only this file) to see merge conflicts when multiple features land at once.
 */
@Database(
    entities = [ExpenseEntity::class, SavingsGoalEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class HogarGoDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun savingsGoalDao(): SavingsGoalDao

    companion object {
        @Volatile
        private var instance: HogarGoDatabase? = null

        fun getInstance(context: Context): HogarGoDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    HogarGoDatabase::class.java,
                    "hogargo.db",
                ).build().also { instance = it }
            }
    }
}
