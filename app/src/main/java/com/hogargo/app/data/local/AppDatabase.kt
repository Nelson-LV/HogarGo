package com.hogargo.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hogargo.app.data.InitialTasks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val InitialEvents = listOf(
    EventEntity("vacuum_living_room", "Aspirar la sala de estar", "Hoy • 16:00"),
    EventEntity("water_plants", "Regar las plantas del balcón", "Mañana"),
)

val InitialExpenses = listOf(
    ExpenseEntity("1", "Trader Joe's", "Supermercado", 142.50, "Ayer"),
    ExpenseEntity("2", "Veterinaria Zori", "Mascota", 85.00, "12 Oct"),
    ExpenseEntity("3", "Luz y Energía", "Servicios", 120.00, "11 Oct"),
)

@Database(entities = [TaskEntity::class, EventEntity::class, ExpenseEntity::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun eventDao(): EventDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hogargo_database",
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .addCallback(DatabaseCallback(context.applicationContext))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val context: Context) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    val database = getDatabase(context)
                    database.taskDao().insertTasks(InitialTasks.map { it.toTaskEntity() })
                    database.eventDao().insertEvents(InitialEvents)
                    database.expenseDao().insertExpenses(InitialExpenses)
                }
            }
        }
    }
}
