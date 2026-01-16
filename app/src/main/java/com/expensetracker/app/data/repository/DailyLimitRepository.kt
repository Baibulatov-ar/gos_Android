package com.expensetracker.app.data.repository

import com.expensetracker.app.data.dao.DailyLimitDao
import com.expensetracker.app.data.model.DailyLimit
import java.util.Date

class DailyLimitRepository(private val dailyLimitDao: DailyLimitDao) {
    suspend fun getLimitByDate(date: Date): DailyLimit? = dailyLimitDao.getLimitByDate(date)

    suspend fun getLatestLimitBeforeOrOnDate(date: Date): DailyLimit? = 
        dailyLimitDao.getLatestLimitBeforeOrOnDate(date)

    suspend fun getLatestLimit(): DailyLimit? = dailyLimitDao.getLatestLimit()

    suspend fun insertLimit(limit: DailyLimit) = dailyLimitDao.insertLimit(limit)

    suspend fun updateLimit(limit: DailyLimit) = dailyLimitDao.updateLimit(limit)
}

