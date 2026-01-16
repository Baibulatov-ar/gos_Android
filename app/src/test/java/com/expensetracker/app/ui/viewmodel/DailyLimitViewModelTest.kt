package com.expensetracker.app.ui.viewmodel

import com.expensetracker.app.data.model.DailyLimit
import com.expensetracker.app.data.repository.DailyLimitRepository
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
 * Unit тесты для DailyLimitViewModel
 * 
 * Тестирует:
 * - Загрузку дневного лимита для сегодня
 * - Получение лимита для конкретной даты
 * - Обновление дневного лимита
 * - Логику выбора лимита (сегодняшний -> последний -> дефолтный)
 */
class DailyLimitViewModelTest {

    private lateinit var repository: DailyLimitRepository
    private lateinit var viewModel: DailyLimitViewModel
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
    fun `loadDailyLimit should load limit for today`() = runTest {
        // Arrange
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val limit = DailyLimit(today, 2000.0)
        whenever(repository.getLimitByDate(any())).thenReturn(limit)
        viewModel = DailyLimitViewModel(repository)

        // Act
        advanceUntilIdle()

        // Assert
        assertEquals(2000.0, viewModel.dailyLimit.value, 0.01)
        verify(repository, atLeastOnce()).getLimitByDate(any())
    }

    @Test
    fun `loadDailyLimit should use latest limit if no limit for today`() = runTest {
        // Arrange
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val yesterday = Calendar.getInstance().apply {
            time = today
            add(Calendar.DAY_OF_YEAR, -1)
        }.time

        val latestLimit = DailyLimit(yesterday, 1500.0)
        whenever(repository.getLimitByDate(any())).thenReturn(null)
        whenever(repository.getLatestLimit()).thenReturn(latestLimit)
        viewModel = DailyLimitViewModel(repository)

        // Act
        advanceUntilIdle()

        // Assert
        assertEquals(1500.0, viewModel.dailyLimit.value, 0.01)
        verify(repository, atLeastOnce()).getLimitByDate(any())
        verify(repository).getLatestLimit()
    }

    @Test
    fun `loadDailyLimit should use default if no limits exist`() = runTest {
        // Arrange
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        whenever(repository.getLimitByDate(any())).thenReturn(null)
        whenever(repository.getLatestLimit()).thenReturn(null)
        viewModel = DailyLimitViewModel(repository)

        // Act
        advanceUntilIdle()

        // Assert
        assertEquals(1500.0, viewModel.dailyLimit.value, 0.01) // Default value
    }

    @Test
    fun `getLimitForDate should return limit for specific date`() = runTest {
        // Arrange
        val date = Calendar.getInstance().apply {
            set(2024, Calendar.JANUARY, 15, 10, 30, 45)
        }.time

        val normalizedDate = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val limit = DailyLimit(normalizedDate, 3000.0)
        whenever(repository.getLimitByDate(any())).thenReturn(null)
        whenever(repository.getLatestLimit()).thenReturn(null)
        whenever(repository.getLimitByDate(normalizedDate)).thenReturn(limit)
        viewModel = DailyLimitViewModel(repository)
        advanceUntilIdle()

        // Act
        val result = viewModel.getLimitForDate(date).toList()

        // Assert
        assertEquals(1, result.size)
        assertEquals(3000.0, result[0], 0.01)
        verify(repository).getLimitByDate(normalizedDate)
    }

    @Test
    fun `getLimitForDate should use latest limit before date if no limit for date`() = runTest {
        // Arrange
        val date = Calendar.getInstance().apply {
            set(2024, Calendar.JANUARY, 15, 0, 0, 0)
        }.time

        val previousDate = Calendar.getInstance().apply {
            set(2024, Calendar.JANUARY, 10, 0, 0, 0)
        }.time

        val previousLimit = DailyLimit(previousDate, 2500.0)
        whenever(repository.getLimitByDate(any())).thenReturn(null)
        whenever(repository.getLatestLimit()).thenReturn(null)
        whenever(repository.getLatestLimitBeforeOrOnDate(any())).thenReturn(previousLimit)
        viewModel = DailyLimitViewModel(repository)
        advanceUntilIdle()

        // Act
        val result = viewModel.getLimitForDate(date).toList()

        // Assert
        assertEquals(1, result.size)
        assertEquals(2500.0, result[0], 0.01)
        verify(repository, atLeastOnce()).getLimitByDate(any())
        verify(repository).getLatestLimitBeforeOrOnDate(any())
    }

    @Test
    fun `updateDailyLimit should insert new limit and update state`() = runTest {
        // Arrange
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val newLimit = 2500.0
        whenever(repository.getLimitByDate(any())).thenReturn(null)
        whenever(repository.getLatestLimit()).thenReturn(null)
        viewModel = DailyLimitViewModel(repository)
        advanceUntilIdle()

        // Act
        viewModel.updateDailyLimit(newLimit)
        advanceUntilIdle()

        // Assert
        assertEquals(newLimit, viewModel.dailyLimit.value, 0.01)
        verify(repository).insertLimit(argThat { limit -> limit.date == today && limit.limit == newLimit })
    }
}

