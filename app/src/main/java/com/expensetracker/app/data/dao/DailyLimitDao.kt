package com.expensetracker.app.data.dao

import androidx.room.*
import com.expensetracker.app.data.model.DailyLimit
import java.util.Date

@Dao
interface DailyLimitDao {
    @Query("SELECT * FROM daily_limits WHERE date = :date LIMIT 1")
    suspend fun getLimitByDate(date: Date): DailyLimit?

    @Query("SELECT * FROM daily_limits WHERE date <= :date ORDER BY date DESC LIMIT 1")
    suspend fun getLatestLimitBeforeOrOnDate(date: Date): DailyLimit?

    @Query("SELECT * FROM daily_limits ORDER BY date DESC LIMIT 1")
    suspend fun getLatestLimit(): DailyLimit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLimit(limit: DailyLimit)

    @Update
    suspend fun updateLimit(limit: DailyLimit)
}

