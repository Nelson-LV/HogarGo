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
    entities = [
        ExpenseEntity::class,
        SavingsGoalEntity::class,
        PetStateEntity::class,
        WardrobeItemEntity::class,
        BillEntity::class,
        TaskEntity::class,
        EventEntity::class,
    ],
    version = 4,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class HogarGoDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun savingsGoalDao(): SavingsGoalDao
    abstract fun petStateDao(): PetStateDao
    abstract fun wardrobeItemDao(): WardrobeItemDao
    abstract fun billDao(): BillDao
    abstract fun taskDao(): TaskDao
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var instance: HogarGoDatabase? = null

        fun getInstance(context: Context): HogarGoDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    HogarGoDatabase::class.java,
                    "hogargo.db",
                )
                    // Pre-release schema: destroy & recreate on bump instead of writing migrations.
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build().also { instance = it }
            }
    }
}
