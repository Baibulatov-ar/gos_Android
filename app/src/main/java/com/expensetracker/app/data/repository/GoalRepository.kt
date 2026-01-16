package com.expensetracker.app.data.repository

import com.expensetracker.app.data.dao.GoalDao
import com.expensetracker.app.data.model.Goal
import kotlinx.coroutines.flow.Flow

class GoalRepository(private val goalDao: GoalDao) {
    fun getAllGoals(): Flow<List<Goal>> = goalDao.getAllGoals()

    fun getArchivedGoals(): Flow<List<Goal>> = goalDao.getArchivedGoals()

    suspend fun insertGoal(goal: Goal): Long = goalDao.insertGoal(goal)

    suspend fun updateGoal(goal: Goal) = goalDao.updateGoal(goal)

    suspend fun deleteGoal(goal: Goal) = goalDao.deleteGoal(goal)
}

