package com.expensetracker.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey
    val name: String,
    val icon: String,
    val color: Long,
    val isFavorite: Boolean = false,
    val order: Int = 0
)

