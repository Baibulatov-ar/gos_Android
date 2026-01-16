package com.expensetracker.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val targetDate: Date? = null,
    val category: String? = null,
    val notes: String? = null,
    val icon: String = "flag",
    val isArchived: Boolean = false
)

