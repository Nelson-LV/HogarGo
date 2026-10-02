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
    version = 7,
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

        @Volatile
        private var instance: HogarGoDatabase? = null

        fun getInstance(context: Context): HogarGoDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    HogarGoDatabase::class.java,
                    "hogargo.db",
                )
                    .addMigrations(MIGRATION_4_5, MIGRATION_6_7)
                    // Pre-release schema: destroy & recreate on bump instead of writing migrations.
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build().also { instance = it }
            }
    }
}
