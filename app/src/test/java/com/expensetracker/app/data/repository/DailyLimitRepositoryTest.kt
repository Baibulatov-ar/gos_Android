package com.expensetracker.app.data.repository

import com.expensetracker.app.data.dao.DailyLimitDao
import com.expensetracker.app.data.model.DailyLimit
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import java.util.*

/**
 * Unit тесты для DailyLimitRepository
 * 
 * Тестирует делегирование всех методов к DailyLimitDao
 */
class DailyLimitRepositoryTest {

    private lateinit var dailyLimitDao: DailyLimitDao
    private lateinit var repository: DailyLimitRepository

    @Before
    fun setup() {
        dailyLimitDao = mock()
        repository = DailyLimitRepository(dailyLimitDao)
    }

    @Test
    fun `getLimitByDate should delegate to dao`() = runTest {
        // Arrange
        val date = Date()
        val limit = DailyLimit(date, 2000.0)
        runBlocking {
            whenever(dailyLimitDao.getLimitByDate(date)).thenReturn(limit)
        }

        // Act
        val result = repository.getLimitByDate(date)

        // Assert
        assertNotNull(result)
        assertEquals(2000.0, result!!.limit, 0.01)
        runBlocking {
            verify(dailyLimitDao).getLimitByDate(date)
        }
    }

    @Test
    fun `getLatestLimitBeforeOrOnDate should delegate to dao`() = runTest {
        // Arrange
        val date = Date()
        val limit = DailyLimit(date, 1500.0)
        runBlocking {
            whenever(dailyLimitDao.getLatestLimitBeforeOrOnDate(date)).thenReturn(limit)
        }

        // Act
        val result = repository.getLatestLimitBeforeOrOnDate(date)

        // Assert
        assertNotNull(result)
        assertEquals(1500.0, result!!.limit, 0.01)
        runBlocking {
            verify(dailyLimitDao).getLatestLimitBeforeOrOnDate(date)
        }
    }

    @Test
    fun `getLatestLimit should delegate to dao`() = runTest {
        // Arrange
        val date = Date()
        val limit = DailyLimit(date, 2500.0)
        runBlocking {
            whenever(dailyLimitDao.getLatestLimit()).thenReturn(limit)
        }

        // Act
        val result = repository.getLatestLimit()

        // Assert
        assertNotNull(result)
        assertEquals(2500.0, result!!.limit, 0.01)
        runBlocking {
            verify(dailyLimitDao).getLatestLimit()
        }
    }

    @Test
    fun `insertLimit should delegate to dao`() = runTest {
        // Arrange
        val date = Date()
        val limit = DailyLimit(date, 3000.0)

        // Act
        repository.insertLimit(limit)

        // Assert
        runBlocking {
            verify(dailyLimitDao).insertLimit(limit)
        }
    }

    @Test
    fun `updateLimit should delegate to dao`() = runTest {
        // Arrange
        val date = Date()
        val limit = DailyLimit(date, 3500.0)

        // Act
        repository.updateLimit(limit)

        // Assert
        runBlocking {
            verify(dailyLimitDao).updateLimit(limit)
        }
    }
}

