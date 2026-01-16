package com.expensetracker.app.data.dao

import androidx.room.*
import com.expensetracker.app.data.model.Goal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals WHERE isArchived = 0 ORDER BY targetDate ASC")
    fun getAllGoals(): Flow<List<Goal>>

    @Query("SELECT * FROM goals WHERE isArchived = 1 ORDER BY targetDate DESC")
    fun getArchivedGoals(): Flow<List<Goal>>

    @Insert
    suspend fun insertGoal(goal: Goal): Long

    @Update
    suspend fun updateGoal(goal: Goal)

    @Delete
    suspend fun deleteGoal(goal: Goal)
}

