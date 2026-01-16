package com.expensetracker.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val category: String,
    val note: String? = null,
    val date: Date,
    val tags: List<String> = emptyList(),
    val receiptImagePath: String? = null
)

