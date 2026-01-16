package com.expensetracker.app.data.repository

import com.expensetracker.app.data.dao.ExpenseDao
import com.expensetracker.app.data.model.Expense
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.*
import java.util.*

/**
 * Unit тесты для ExpenseRepository
 * 
 * Тестирует делегирование всех методов к ExpenseDao
 */
class ExpenseRepositoryTest {

    private lateinit var expenseDao: ExpenseDao
    private lateinit var repository: ExpenseRepository

    @Before
    fun setup() {
        expenseDao = mock()
        repository = ExpenseRepository(expenseDao)
    }

    @Test
    fun `getAllExpenses should delegate to dao`() = runTest {
        // Arrange
        val expenses = listOf(
            Expense(1, 100.0, "Продукты", "Магазин", Date())
        )
        whenever(expenseDao.getAllExpenses()).thenReturn(flowOf(expenses))

        // Act
        val result = repository.getAllExpenses()

        // Assert
        assertNotNull(result)
        verify(expenseDao).getAllExpenses()
    }

    @Test
    fun `getExpensesByDate should delegate to dao`() = runTest {
        // Arrange
        val date = Date()
        val expenses = listOf(Expense(1, 100.0, "Продукты", "Магазин", date))
        whenever(expenseDao.getExpensesByDate(date)).thenReturn(flowOf(expenses))

        // Act
        val result = repository.getExpensesByDate(date)

        // Assert
        assertNotNull(result)
        verify(expenseDao).getExpensesByDate(date)
    }

    @Test
    fun `getExpensesByDateRange should delegate to dao`() = runTest {
        // Arrange
        val startDate = Date()
        val endDate = Date()
        val expenses = listOf(Expense(1, 100.0, "Продукты", "Магазин", startDate))
        whenever(expenseDao.getExpensesByDateRange(startDate, endDate))
            .thenReturn(flowOf(expenses))

        // Act
        val result = repository.getExpensesByDateRange(startDate, endDate)

        // Assert
        assertNotNull(result)
        verify(expenseDao).getExpensesByDateRange(startDate, endDate)
    }

    @Test
    fun `getExpensesByCategory should delegate to dao`() = runTest {
        // Arrange
        val category = "Продукты"
        val expenses = listOf(Expense(1, 100.0, category, "Магазин", Date()))
        whenever(expenseDao.getExpensesByCategory(category)).thenReturn(flowOf(expenses))

        // Act
        val result = repository.getExpensesByCategory(category)

        // Assert
        assertNotNull(result)
        verify(expenseDao).getExpensesByCategory(category)
    }

    @Test
    fun `getTotalByDate should return sum from dao`() = runTest {
        // Arrange
        val date = Date()
        runBlocking {
            whenever(expenseDao.getTotalByDate(date)).thenReturn(250.0)
        }

        // Act
        val result = repository.getTotalByDate(date)

        // Assert
        assertEquals(250.0, result, 0.01)
        runBlocking {
            verify(expenseDao).getTotalByDate(date)
        }
    }

    @Test
    fun `getTotalByDate should return zero when dao returns null`() = runTest {
        // Arrange
        val date = Date()
        runBlocking {
            whenever(expenseDao.getTotalByDate(date)).thenReturn(null)
        }

        // Act
        val result = repository.getTotalByDate(date)

        // Assert
        assertEquals(0.0, result, 0.01)
    }

    @Test
    fun `getTotalByDateRange should return sum from dao`() = runTest {
        // Arrange
        val startDate = Date()
        val endDate = Date()
        runBlocking {
            whenever(expenseDao.getTotalByDateRange(startDate, endDate)).thenReturn(500.0)
        }

        // Act
        val result = repository.getTotalByDateRange(startDate, endDate)

        // Assert
        assertEquals(500.0, result, 0.01)
        runBlocking {
            verify(expenseDao).getTotalByDateRange(startDate, endDate)
        }
    }

    @Test
    fun `insertExpense should delegate to dao`() = runTest {
        // Arrange
        val expense = Expense(0, 100.0, "Продукты", "Магазин", Date())
        runBlocking {
            whenever(expenseDao.insertExpense(expense)).thenReturn(1L)
        }

        // Act
        val result = repository.insertExpense(expense)

        // Assert
        assertEquals(1L, result)
        runBlocking {
            verify(expenseDao).insertExpense(expense)
        }
    }

    @Test
    fun `updateExpense should delegate to dao`() = runTest {
        // Arrange
        val expense = Expense(1, 100.0, "Продукты", "Магазин", Date())

        // Act
        repository.updateExpense(expense)

        // Assert
        runBlocking {
            verify(expenseDao).updateExpense(expense)
        }
    }

    @Test
    fun `deleteExpense should delegate to dao`() = runTest {
        // Arrange
        val expense = Expense(1, 100.0, "Продукты", "Магазин", Date())

        // Act
        repository.deleteExpense(expense)

        // Assert
        runBlocking {
            verify(expenseDao).deleteExpense(expense)
        }
    }

    @Test
    fun `deleteExpenseById should delegate to dao`() = runTest {
        // Arrange
        val expenseId = 1L

        // Act
        repository.deleteExpenseById(expenseId)

        // Assert
        runBlocking {
            verify(expenseDao).deleteExpenseById(expenseId)
        }
    }
}

