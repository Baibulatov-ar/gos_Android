package com.expensetracker.app.data.repository

import com.expensetracker.app.data.dao.CategoryDao
import com.expensetracker.app.data.model.Category
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {
    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()

    fun getFavoriteCategories(): Flow<List<Category>> = categoryDao.getFavoriteCategories()

    suspend fun insertCategory(category: Category) = categoryDao.insertCategory(category)

    suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)

    suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)
}

