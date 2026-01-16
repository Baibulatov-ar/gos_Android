package com.expensetracker.app.data.repository

import com.expensetracker.app.data.dao.CategoryDao
import com.expensetracker.app.data.model.Category
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.*

/**
 * Unit тесты для CategoryRepository
 * 
 * Тестирует делегирование всех методов к CategoryDao
 */
class CategoryRepositoryTest {

    private lateinit var categoryDao: CategoryDao
    private lateinit var repository: CategoryRepository

    @Before
    fun setup() {
        categoryDao = mock()
        repository = CategoryRepository(categoryDao)
    }

    @Test
    fun `getAllCategories should delegate to dao`() = runTest {
        // Arrange
        val categories = listOf(
            Category("Продукты", "restaurant", 0xFF4CAF50, false, 0)
        )
        whenever(categoryDao.getAllCategories()).thenReturn(flowOf(categories))

        // Act
        val result = repository.getAllCategories()

        // Assert
        assertNotNull(result)
        verify(categoryDao).getAllCategories()
    }

    @Test
    fun `getFavoriteCategories should delegate to dao`() = runTest {
        // Arrange
        val favoriteCategories = listOf(
            Category("Продукты", "restaurant", 0xFF4CAF50, true, 0)
        )
        whenever(categoryDao.getFavoriteCategories()).thenReturn(flowOf(favoriteCategories))

        // Act
        val result = repository.getFavoriteCategories()

        // Assert
        assertNotNull(result)
        verify(categoryDao).getFavoriteCategories()
    }

    @Test
    fun `insertCategory should delegate to dao`() = runTest {
        // Arrange
        val category = Category("Новая категория", "icon", 0xFF000000, false, 0)

        // Act
        repository.insertCategory(category)

        // Assert
        runBlocking {
            verify(categoryDao).insertCategory(category)
        }
    }

    @Test
    fun `updateCategory should delegate to dao`() = runTest {
        // Arrange
        val category = Category("Обновленная категория", "icon", 0xFF000000, true, 0)

        // Act
        repository.updateCategory(category)

        // Assert
        runBlocking {
            verify(categoryDao).updateCategory(category)
        }
    }

    @Test
    fun `deleteCategory should delegate to dao`() = runTest {
        // Arrange
        val category = Category("Категория для удаления", "icon", 0xFF000000, false, 0)

        // Act
        repository.deleteCategory(category)

        // Assert
        runBlocking {
            verify(categoryDao).deleteCategory(category)
        }
    }
}

