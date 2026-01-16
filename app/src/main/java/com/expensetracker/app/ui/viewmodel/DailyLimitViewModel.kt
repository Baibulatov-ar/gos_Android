package com.expensetracker.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expensetracker.app.data.model.DailyLimit
import com.expensetracker.app.data.repository.DailyLimitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class DailyLimitViewModel(private val repository: DailyLimitRepository) : ViewModel() {
    private val _dailyLimit = MutableStateFlow(1500.0)
    val dailyLimit: StateFlow<Double> = _dailyLimit.asStateFlow()

    init {
        loadDailyLimit()
    }

    private fun loadDailyLimit() {
        viewModelScope.launch {
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            val limit = repository.getLimitByDate(today)?.limit
                ?: repository.getLatestLimit()?.limit
                ?: 1500.0

            _dailyLimit.value = limit
        }
    }

    fun getLimitForDate(date: Date): Flow<Double> {
        return flow {
            val normalizedDate = Calendar.getInstance().apply {
                time = date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            val limit = repository.getLimitByDate(normalizedDate)?.limit
                ?: repository.getLatestLimitBeforeOrOnDate(normalizedDate)?.limit
                ?: repository.getLatestLimit()?.limit
                ?: 1500.0

            emit(limit)
        }
    }

    fun updateDailyLimit(limit: Double) {
        viewModelScope.launch {
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            repository.insertLimit(DailyLimit(today, limit))
            _dailyLimit.value = limit
        }
    }
}

