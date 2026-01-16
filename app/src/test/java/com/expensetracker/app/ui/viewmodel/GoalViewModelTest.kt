package com.expensetracker.app.ui.viewmodel

import com.expensetracker.app.data.model.Goal
import com.expensetracker.app.data.repository.GoalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)

/**
 * Unit тесты для GoalViewModel
 * 
 * Тестирует:
 * - Получение всех целей
 * - Получение архивированных целей
 * - Добавление цели
 * - Обновление цели
 * - Архивирование цели
 * - Удаление цели
 */
class GoalViewModelTest {

    private lateinit var repository: GoalRepository
    private lateinit var viewModel: GoalViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        viewModel = GoalViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getAllGoals should return flow from repository`() = runTest {
        // Arrange
        val goals = listOf(
            Goal(1, "Новый ноутбук", 50000.0, 10000.0, Date(), null, null, "laptop", false),
            Goal(2, "Отпуск", 100000.0, 0.0, Date(), null, null, "airplane", false)
        )
        whenever(repository.getAllGoals()).thenReturn(flowOf(goals))

        // Act
        val result = viewModel.getAllGoals().toList()

        // Assert
        assertEquals(1, result.size)
        assertEquals(goals, result[0])
        verify(repository).getAllGoals()
    }

    @Test
    fun `getArchivedGoals should return flow from repository`() = runTest {
        // Arrange
        val archivedGoals = listOf(
            Goal(1, "Выполненная цель", 10000.0, 10000.0, Date(), null, null, "flag", true)
        )
        whenever(repository.getArchivedGoals()).thenReturn(flowOf(archivedGoals))

        // Act
        val result = viewModel.getArchivedGoals().toList()

        // Assert
        assertEquals(1, result.size)
        assertEquals(archivedGoals, result[0])
        verify(repository).getArchivedGoals()
    }

    @Test
    fun `addGoal should call repository insertGoal`() = runTest {
        // Arrange
        val goal = Goal(0, "Новая цель", 5000.0, 0.0, Date(), null, null, "flag", false)
        runBlocking {
            whenever(repository.insertGoal(any())).thenReturn(1L)
        }

        // Act
        viewModel.addGoal(goal)
        advanceUntilIdle()

        // Assert
        runBlocking {
            verify(repository).insertGoal(goal)
        }
    }

    @Test
    fun `updateGoal should call repository updateGoal`() = runTest {
        // Arrange
        val goal = Goal(1, "Обновленная цель", 5000.0, 2000.0, Date(), null, null, "flag", false)

        // Act
        viewModel.updateGoal(goal)
        advanceUntilIdle()

        // Assert
        runBlocking {
            verify(repository).updateGoal(goal)
        }
    }

    @Test
    fun `archiveGoal should set isArchived to true`() = runTest {
        // Arrange
        val goal = Goal(1, "Цель", 5000.0, 5000.0, Date(), null, null, "flag", false)
        val archivedGoal = goal.copy(isArchived = true)

        // Act
        viewModel.archiveGoal(goal)
        advanceUntilIdle()

        // Assert
        runBlocking {
            verify(repository).updateGoal(archivedGoal)
        }
    }

    @Test
    fun `deleteGoal should call repository deleteGoal`() = runTest {
        // Arrange
        val goal = Goal(1, "Цель для удаления", 5000.0, 0.0, Date(), null, null, "flag", false)

        // Act
        viewModel.deleteGoal(goal)
        advanceUntilIdle()

        // Assert
        runBlocking {
            verify(repository).deleteGoal(goal)
        }
    }
}

