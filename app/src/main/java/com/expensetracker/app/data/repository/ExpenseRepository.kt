package com.expensetracker.app.data.repository

import com.expensetracker.app.data.dao.ExpenseDao
import com.expensetracker.app.data.model.Expense
import kotlinx.coroutines.flow.Flow
import java.util.Date

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    fun getAllExpenses(): Flow<List<Expense>> = expenseDao.getAllExpenses()

    fun getExpensesByDate(date: Date): Flow<List<Expense>> = expenseDao.getExpensesByDate(date)

    fun getExpensesByDateRange(startDate: Date, endDate: Date): Flow<List<Expense>> =
        expenseDao.getExpensesByDateRange(startDate, endDate)

    fun getExpensesByCategory(category: String): Flow<List<Expense>> =
        expenseDao.getExpensesByCategory(category)

    suspend fun getTotalByDate(date: Date): Double = expenseDao.getTotalByDate(date) ?: 0.0

    suspend fun getTotalByDateRange(startDate: Date, endDate: Date): Double =
        expenseDao.getTotalByDateRange(startDate, endDate) ?: 0.0

    suspend fun insertExpense(expense: Expense): Long = expenseDao.insertExpense(expense)

    suspend fun updateExpense(expense: Expense) = expenseDao.updateExpense(expense)

    suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)

    suspend fun deleteExpenseById(id: Long) = expenseDao.deleteExpenseById(id)
}

