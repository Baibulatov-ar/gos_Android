package com.expensetracker.app.data.repository

import com.expensetracker.app.data.dao.GoalDao
import com.expensetracker.app.data.model.Goal
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import java.util.*

/**
 * Unit тесты для GoalRepository
 * 
 * Тестирует делегирование всех методов к GoalDao
 */
class GoalRepositoryTest {

    private lateinit var goalDao: GoalDao
    private lateinit var repository: GoalRepository

    @Before
    fun setup() {
        goalDao = mock()
        repository = GoalRepository(goalDao)
    }

    @Test
    fun `getAllGoals should delegate to dao`() = runTest {
        // Arrange
        val goals = listOf(
            Goal(1, "Цель 1", 5000.0, 0.0, Date(), null, null, "flag", false)
        )
        whenever(goalDao.getAllGoals()).thenReturn(flowOf(goals))

        // Act
        val result = repository.getAllGoals()

        // Assert
        assertNotNull(result)
        verify(goalDao).getAllGoals()
    }

    @Test
    fun `getArchivedGoals should delegate to dao`() = runTest {
        // Arrange
        val archivedGoals = listOf(
            Goal(1, "Архивная цель", 5000.0, 5000.0, Date(), null, null, "flag", true)
        )
        whenever(goalDao.getArchivedGoals()).thenReturn(flowOf(archivedGoals))

        // Act
        val result = repository.getArchivedGoals()

        // Assert
        assertNotNull(result)
        verify(goalDao).getArchivedGoals()
    }

    @Test
    fun `insertGoal should delegate to dao`() = runTest {
        // Arrange
        val goal = Goal(0, "Новая цель", 5000.0, 0.0, Date(), null, null, "flag", false)
        runBlocking {
            whenever(goalDao.insertGoal(goal)).thenReturn(1L)
        }

        // Act
        val result = repository.insertGoal(goal)

        // Assert
        assertEquals(1L, result)
        runBlocking {
            verify(goalDao).insertGoal(goal)
        }
    }

    @Test
    fun `updateGoal should delegate to dao`() = runTest {
        // Arrange
        val goal = Goal(1, "Обновленная цель", 5000.0, 2000.0, Date(), null, null, "flag", false)

        // Act
        repository.updateGoal(goal)

        // Assert
        runBlocking {
            verify(goalDao).updateGoal(goal)
        }
    }

    @Test
    fun `deleteGoal should delegate to dao`() = runTest {
        // Arrange
        val goal = Goal(1, "Цель для удаления", 5000.0, 0.0, Date(), null, null, "flag", false)

        // Act
        repository.deleteGoal(goal)

        // Assert
        runBlocking {
            verify(goalDao).deleteGoal(goal)
        }
    }
}

