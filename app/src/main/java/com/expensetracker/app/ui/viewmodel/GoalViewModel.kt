package com.expensetracker.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expensetracker.app.data.model.Goal
import com.expensetracker.app.data.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class GoalViewModel(private val repository: GoalRepository) : ViewModel() {
    fun getAllGoals(): Flow<List<Goal>> = repository.getAllGoals()

    fun getArchivedGoals(): Flow<List<Goal>> = repository.getArchivedGoals()

    fun addGoal(goal: Goal) {
        viewModelScope.launch {
            repository.insertGoal(goal)
        }
    }

    fun updateGoal(goal: Goal) {
        viewModelScope.launch {
            repository.updateGoal(goal)
        }
    }

    fun archiveGoal(goal: Goal) {
        viewModelScope.launch {
            repository.updateGoal(goal.copy(isArchived = true))
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }
}

