package com.expensetracker.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "daily_limits")
data class DailyLimit(
    @PrimaryKey
    val date: Date,
    val limit: Double
)

