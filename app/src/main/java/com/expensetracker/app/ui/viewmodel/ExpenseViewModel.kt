package com.expensetracker.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expensetracker.app.data.model.Expense
import com.expensetracker.app.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {
    private val _todayExpenses = MutableStateFlow<List<Expense>>(emptyList())
    val todayExpenses: StateFlow<List<Expense>> = _todayExpenses.asStateFlow()

    private val _todayTotal = MutableStateFlow(0.0)
    val todayTotal: StateFlow<Double> = _todayTotal.asStateFlow()

    init {
        loadTodayExpenses()
    }

    private fun loadTodayExpenses() {
        viewModelScope.launch {
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            repository.getExpensesByDate(today).collect { expenses ->
                _todayExpenses.value = expenses
                _todayTotal.value = expenses.sumOf { it.amount }
            }
        }
    }

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            repository.insertExpense(expense)
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    fun deleteExpenseById(id: Long) {
        viewModelScope.launch {
            repository.deleteExpenseById(id)
        }
    }

    fun getAllExpenses(): Flow<List<Expense>> = repository.getAllExpenses()

    fun getExpensesByDateRange(startDate: Date, endDate: Date): Flow<List<Expense>> =
        repository.getExpensesByDateRange(startDate, endDate)
}

