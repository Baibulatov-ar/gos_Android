package com.expensetracker.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.expensetracker.app.data.converters.Converters
import com.expensetracker.app.data.dao.CategoryDao
import com.expensetracker.app.data.dao.DailyLimitDao
import com.expensetracker.app.data.dao.ExpenseDao
import com.expensetracker.app.data.dao.GoalDao
import com.expensetracker.app.data.model.Category
import com.expensetracker.app.data.model.DailyLimit
import com.expensetracker.app.data.model.Expense
import com.expensetracker.app.data.model.Goal

@Database(
    entities = [Expense::class, Category::class, Goal::class, DailyLimit::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun goalDao(): GoalDao
    abstract fun dailyLimitDao(): DailyLimitDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE goals ADD COLUMN isArchived INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expense_tracker_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

