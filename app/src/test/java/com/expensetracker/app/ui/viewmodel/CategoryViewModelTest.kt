package com.expensetracker.app.ui.viewmodel

import com.expensetracker.app.data.model.Category
import com.expensetracker.app.data.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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

@OptIn(ExperimentalCoroutinesApi::class)

/**
 * Unit тесты для CategoryViewModel
 * 
 * Тестирует:
 * - Загрузку всех категорий
 * - Переключение избранного статуса категории
 */
class CategoryViewModelTest {

    private lateinit var repository: CategoryRepository
    private lateinit var viewModel: CategoryViewModel
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
    fun `loadCategories should load all categories from repository`() = runTest {
        // Arrange
        val categories = listOf(
            Category("Продукты", "restaurant", 0xFF4CAF50, false, 0),
            Category("Транспорт", "directions_car", 0xFF2196F3, false, 1),
            Category("Покупки", "shopping_bag", 0xFF9C27B0, false, 2)
        )
        whenever(repository.getAllCategories()).thenReturn(flowOf(categories))
        viewModel = CategoryViewModel(repository)

        // Act
        advanceUntilIdle()

        // Assert
        val loadedCategories = viewModel.categories.value
        assertEquals(3, loadedCategories.size)
        assertEquals("Продукты", loadedCategories[0].name)
        verify(repository, atLeastOnce()).getAllCategories()
    }

    @Test
    fun `toggleFavorite should update category favorite status`() = runTest {
        // Arrange
        val category = Category("Продукты", "restaurant", 0xFF4CAF50, false, 0)
        val updatedCategory = category.copy(isFavorite = true)
        
        whenever(repository.getAllCategories()).thenReturn(flowOf(emptyList()))
        viewModel = CategoryViewModel(repository)
        advanceUntilIdle()

        // Act
        viewModel.toggleFavorite(category)
        advanceUntilIdle()

        // Assert
        verify(repository).updateCategory(updatedCategory)
    }

    @Test
    fun `toggleFavorite should toggle from true to false`() = runTest {
        // Arrange
        val category = Category("Продукты", "restaurant", 0xFF4CAF50, true, 0)
        val updatedCategory = category.copy(isFavorite = false)
        
        whenever(repository.getAllCategories()).thenReturn(flowOf(emptyList()))
        viewModel = CategoryViewModel(repository)
        advanceUntilIdle()

        // Act
        viewModel.toggleFavorite(category)
        advanceUntilIdle()

        // Assert
        verify(repository).updateCategory(updatedCategory)
    }

    @Test
    fun `categories should be empty initially`() = runTest {
        // Arrange
        whenever(repository.getAllCategories()).thenReturn(flowOf(emptyList()))
        viewModel = CategoryViewModel(repository)

        // Act
        advanceUntilIdle()

        // Assert
        assertTrue(viewModel.categories.value.isEmpty())
    }
}

