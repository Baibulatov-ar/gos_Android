package com.expensetracker.app.ui.viewmodel

import com.expensetracker.app.data.model.Expense
import com.expensetracker.app.data.repository.ExpenseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
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
 * Unit тесты для ExpenseViewModel
 * 
 * Тестирует:
 * - Загрузку расходов за сегодня
 * - Добавление расходов
 * - Удаление расходов
 * - Получение всех расходов
 * - Получение расходов по диапазону дат
 * - Подсчет общей суммы за день
 */
class ExpenseViewModelTest {

    private lateinit var repository: ExpenseRepository
    private lateinit var viewModel: ExpenseViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadTodayExpenses should load expenses and calculate total`() = runTest {
        // Arrange
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val expenses = listOf(
            Expense(1, 100.0, "Продукты", "Магазин", today),
            Expense(2, 50.0, "Транспорт", null, today),
            Expense(3, 200.0, "Развлечения", "Кино", today)
        )

        whenever(repository.getExpensesByDate(any())).thenReturn(flowOf(expenses))
        viewModel = ExpenseViewModel(repository)

        // Act
        advanceUntilIdle()

        // Assert
        val todayExpenses = viewModel.todayExpenses.value
        val todayTotal = viewModel.todayTotal.value

        assertEquals(3, todayExpenses.size)
        assertEquals(350.0, todayTotal, 0.01)
        verify(repository, atLeastOnce()).getExpensesByDate(any())
    }

    @Test
    fun `addExpense should call repository insertExpense`() = runTest {
        // Arrange
        val expense = Expense(0, 100.0, "Продукты", "Магазин", Date())
        whenever(repository.getExpensesByDate(any())).thenReturn(flowOf(emptyList()))
        whenever(repository.insertExpense(any())).thenReturn(1L)
        viewModel = ExpenseViewModel(repository)
        advanceUntilIdle()

        // Act
        viewModel.addExpense(expense)
        advanceUntilIdle()

        // Assert
        verify(repository).insertExpense(expense)
    }

    @Test
    fun `deleteExpense should call repository deleteExpense`() = runTest {
        // Arrange
        val expense = Expense(1, 100.0, "Продукты", "Магазин", Date())
        whenever(repository.getExpensesByDate(any())).thenReturn(flowOf(emptyList()))
        viewModel = ExpenseViewModel(repository)
        advanceUntilIdle()

        // Act
        viewModel.deleteExpense(expense)
        advanceUntilIdle()

        // Assert
        verify(repository).deleteExpense(expense)
    }

    @Test
    fun `deleteExpenseById should call repository deleteExpenseById`() = runTest {
        // Arrange
        val expenseId = 1L
        whenever(repository.getExpensesByDate(any())).thenReturn(flowOf(emptyList()))
        viewModel = ExpenseViewModel(repository)
        advanceUntilIdle()

        // Act
        viewModel.deleteExpenseById(expenseId)
        advanceUntilIdle()

        // Assert
        verify(repository).deleteExpenseById(expenseId)
    }

    @Test
    fun `getAllExpenses should return flow from repository`() = runTest {
        // Arrange
        val expenses = listOf(
            Expense(1, 100.0, "Продукты", "Магазин", Date()),
            Expense(2, 50.0, "Транспорт", null, Date())
        )
        whenever(repository.getExpensesByDate(any())).thenReturn(flowOf(emptyList()))
        whenever(repository.getAllExpenses()).thenReturn(flowOf(expenses))
        viewModel = ExpenseViewModel(repository)
        advanceUntilIdle()

        // Act
        val result = viewModel.getAllExpenses().toList()

        // Assert
        assertEquals(1, result.size)
        assertEquals(expenses, result[0])
        verify(repository).getAllExpenses()
    }

    @Test
    fun `getExpensesByDateRange should return flow from repository`() = runTest {
        // Arrange
        val startDate = Date()
        val endDate = Date()
        val expenses = listOf(
            Expense(1, 100.0, "Продукты", "Магазин", startDate)
        )
        whenever(repository.getExpensesByDate(any())).thenReturn(flowOf(emptyList()))
        whenever(repository.getExpensesByDateRange(startDate, endDate))
            .thenReturn(flowOf(expenses))
        viewModel = ExpenseViewModel(repository)
        advanceUntilIdle()

        // Act
        val result = viewModel.getExpensesByDateRange(startDate, endDate).toList()

        // Assert
        assertEquals(1, result.size)
        assertEquals(expenses, result[0])
        verify(repository).getExpensesByDateRange(startDate, endDate)
    }

    @Test
    fun `todayTotal should be zero when no expenses`() = runTest {
        // Arrange
        whenever(repository.getExpensesByDate(any())).thenReturn(flowOf(emptyList()))
        viewModel = ExpenseViewModel(repository)

        // Act
        advanceUntilIdle()

        // Assert
        assertEquals(0.0, viewModel.todayTotal.value, 0.01)
    }
}

