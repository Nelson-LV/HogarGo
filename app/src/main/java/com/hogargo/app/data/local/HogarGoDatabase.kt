package com.hogargo.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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
        HouseholdEntity::class,
        MemberEntity::class,
    ],
    version = 9,
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
    abstract fun householdDao(): HouseholdDao
    abstract fun memberDao(): MemberDao

    companion object {
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE wardrobe_items ADD COLUMN owned INTEGER NOT NULL DEFAULT 0")
                // Items already worn before the shop existed stay owned.
                db.execSQL("UPDATE wardrobe_items SET owned = equipped")
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Everyone who was already in a household stays accepted.
                db.execSQL("ALTER TABLE members ADD COLUMN isApproved INTEGER NOT NULL DEFAULT 1")
            }
        }

        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE members ADD COLUMN recoveryKey TEXT NOT NULL DEFAULT ''")
            }
        }

        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE events ADD COLUMN date INTEGER")
                // Old events stored their date as text; recover the ones written as dd/MM/yyyy.
                val pattern = Regex("^(\\d{2})/(\\d{2})/(\\d{4})")
                val rows = mutableListOf<Triple<String, String, Long>>()
                db.query("SELECT householdId, id, dateLabel FROM events").use { cursor ->
                    while (cursor.moveToNext()) {
                        val match = pattern.find(cursor.getString(2)) ?: continue
                        val (d, m, y) = match.destructured
                        val epochDay = runCatching { java.time.LocalDate.of(y.toInt(), m.toInt(), d.toInt()).toEpochDay() }.getOrNull() ?: continue
                        rows += Triple(cursor.getString(0), cursor.getString(1), epochDay)
                    }
                }
                rows.forEach { (household, id, epochDay) ->
                    db.execSQL("UPDATE events SET date = ? WHERE householdId = ? AND id = ?", arrayOf<Any?>(epochDay, household, id))
                }
            }
        }

        @Volatile
        private var instance: HogarGoDatabase? = null

        fun getInstance(context: Context): HogarGoDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    HogarGoDatabase::class.java,
                    "hogargo.db",
                )
                    .addMigrations(MIGRATION_4_5, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9)
                    // Pre-release schema: destroy & recreate on bump instead of writing migrations.
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build().also { instance = it }
            }
    }
}
