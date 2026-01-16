package com.expensetracker.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.expensetracker.app.ui.theme.*
import java.util.*

@Composable
fun CustomDatePickerDialog(
    selectedDate: Date,
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomCalendar(
                    selectedDate = selectedDate,
                    onDateSelected = { date ->
                        onDateSelected(date)
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
fun CustomCalendar(
    selectedDate: Date,
    onDateSelected: (Date) -> Unit
) {
    val selectedCalendar = remember(selectedDate) {
        Calendar.getInstance().apply {
            time = selectedDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }
    
    var currentMonth by remember(selectedDate) { 
        mutableStateOf(selectedCalendar.get(Calendar.MONTH)) 
    }
    var currentYear by remember(selectedDate) { 
        mutableStateOf(selectedCalendar.get(Calendar.YEAR)) 
    }
    
    // Update month/year when selectedDate changes
    LaunchedEffect(selectedDate) {
        currentMonth = selectedCalendar.get(Calendar.MONTH)
        currentYear = selectedCalendar.get(Calendar.YEAR)
    }
    
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    
    val isToday = { date: Calendar ->
        date.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
        date.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
        date.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
    }
    
    val isSelected = { date: Calendar ->
        date.get(Calendar.YEAR) == selectedCalendar.get(Calendar.YEAR) &&
        date.get(Calendar.MONTH) == selectedCalendar.get(Calendar.MONTH) &&
        date.get(Calendar.DAY_OF_MONTH) == selectedCalendar.get(Calendar.DAY_OF_MONTH)
    }
    
    // Month and Year Header
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            if (currentMonth == 0) {
                currentMonth = 11
                currentYear--
            } else {
                currentMonth--
            }
        }) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Предыдущий месяц",
                tint = OnSurface
            )
        }
        
        Text(
            text = getMonthName(currentMonth, currentYear),
            style = MaterialTheme.typography.titleLarge,
            color = OnSurface,
            fontWeight = FontWeight.Bold
        )
        
        IconButton(onClick = {
            if (currentMonth == 11) {
                currentMonth = 0
                currentYear++
            } else {
                currentMonth++
            }
        }) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Следующий месяц",
                tint = OnSurface
            )
        }
    }
    
    // Weekday Headers
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val weekDays = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
        weekDays.forEach { day ->
            Text(
                text = day,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurface.copy(alpha = 0.6f),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
    
    Spacer(modifier = Modifier.height(8.dp))
    
    // Calendar Grid
    val firstDayOfMonth = Calendar.getInstance().apply {
        set(Calendar.YEAR, currentYear)
        set(Calendar.MONTH, currentMonth)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val daysInMonth = firstDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = (firstDayOfMonth.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Convert to Monday = 0
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        var dayCounter = 1
        var weekCounter = 0
        
        while (dayCounter <= daysInMonth || weekCounter == 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (dayOfWeek in 0..6) {
                    if (weekCounter == 0 && dayOfWeek < firstDayOfWeek) {
                        // Empty cell before first day
                        Spacer(modifier = Modifier.weight(1f))
                    } else if (dayCounter <= daysInMonth) {
                        val currentDay = dayCounter // Capture value for closure
                        val dayCalendar = Calendar.getInstance()
                        dayCalendar.set(Calendar.YEAR, currentYear)
                        dayCalendar.set(Calendar.MONTH, currentMonth)
                        dayCalendar.set(Calendar.DAY_OF_MONTH, currentDay)
                        dayCalendar.set(Calendar.HOUR_OF_DAY, 0)
                        dayCalendar.set(Calendar.MINUTE, 0)
                        dayCalendar.set(Calendar.SECOND, 0)
                        dayCalendar.set(Calendar.MILLISECOND, 0)
                        val isDayToday = isToday(dayCalendar)
                        val isDaySelected = isSelected(dayCalendar)
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isDaySelected -> PrimaryGreen
                                        isDayToday -> PrimaryGreen.copy(alpha = 0.2f)
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable {
                                    // Create date explicitly - set fields in correct order
                                    val newCalendar = Calendar.getInstance()
                                    newCalendar.clear() // Clear all fields first
                                    newCalendar.set(Calendar.YEAR, currentYear)
                                    newCalendar.set(Calendar.MONTH, currentMonth)
                                    newCalendar.set(Calendar.DAY_OF_MONTH, currentDay)
                                    newCalendar.set(Calendar.HOUR_OF_DAY, 0)
                                    newCalendar.set(Calendar.MINUTE, 0)
                                    newCalendar.set(Calendar.SECOND, 0)
                                    newCalendar.set(Calendar.MILLISECOND, 0)
                                    onDateSelected(newCalendar.time)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentDay.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = when {
                                    isDaySelected -> OnPrimary
                                    isDayToday -> PrimaryGreen
                                    else -> OnSurface
                                },
                                fontWeight = if (isDaySelected || isDayToday) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                        dayCounter++
                    } else {
                        // Empty cell after last day
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            weekCounter++
        }
    }
}

private fun getMonthName(month: Int, year: Int): String {
    val monthNames = listOf(
        "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
        "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
    )
    return "${monthNames[month]} $year"
}

@Composable
fun CustomDateRangePickerDialog(
    startDate: Date?,
    endDate: Date?,
    onDateRangeSelected: (Date, Date) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomDateRangeCalendar(
                    startDate = startDate,
                    endDate = endDate,
                    onDateRangeSelected = { start, end ->
                        onDateRangeSelected(start, end)
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
fun CustomDateRangeCalendar(
    startDate: Date?,
    endDate: Date?,
    onDateRangeSelected: (Date, Date) -> Unit
) {
    var tempStartDate by remember(startDate) { mutableStateOf<Date?>(startDate) }
    var tempEndDate by remember(endDate) { mutableStateOf<Date?>(endDate) }
    
    // Update temp dates when props change
    LaunchedEffect(startDate, endDate) {
        tempStartDate = startDate
        tempEndDate = endDate
    }
    
    val initialDate = startDate ?: endDate ?: Date()
    val selectedCalendar = remember(initialDate) {
        Calendar.getInstance().apply {
            time = initialDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }
    
    var currentMonth by remember(initialDate) { 
        mutableStateOf(selectedCalendar.get(Calendar.MONTH)) 
    }
    var currentYear by remember(initialDate) { 
        mutableStateOf(selectedCalendar.get(Calendar.YEAR)) 
    }
    
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    
    val isToday = { date: Calendar ->
        date.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
        date.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
        date.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
    }
    
    val isInRange = { date: Calendar ->
        if (tempStartDate == null || tempEndDate == null) {
            false
        } else {
            val dateTime = date.timeInMillis
            val startTime = tempStartDate!!.time
            val endTime = tempEndDate!!.time
            
            // Check if date is strictly between start and end (not equal to either)
            dateTime > startTime && dateTime < endTime
        }
    }
    
    val isStartDate = { date: Calendar ->
        tempStartDate?.let {
            val cal = Calendar.getInstance().apply { time = it }
            cal.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
            cal.get(Calendar.MONTH) == date.get(Calendar.MONTH) &&
            cal.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH)
        } ?: false
    }
    
    val isEndDate = { date: Calendar ->
        tempEndDate?.let {
            val cal = Calendar.getInstance().apply { time = it }
            cal.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
            cal.get(Calendar.MONTH) == date.get(Calendar.MONTH) &&
            cal.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH)
        } ?: false
    }
    
    // Month and Year Header
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            if (currentMonth == 0) {
                currentMonth = 11
                currentYear--
            } else {
                currentMonth--
            }
        }) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Предыдущий месяц",
                tint = OnSurface
            )
        }
        
        Text(
            text = getMonthName(currentMonth, currentYear),
            style = MaterialTheme.typography.titleLarge,
            color = OnSurface,
            fontWeight = FontWeight.Bold
        )
        
        IconButton(onClick = {
            if (currentMonth == 11) {
                currentMonth = 0
                currentYear++
            } else {
                currentMonth++
            }
        }) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Следующий месяц",
                tint = OnSurface
            )
        }
    }
    
    // Weekday Headers
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val weekDays = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
        weekDays.forEach { day ->
            Text(
                text = day,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurface.copy(alpha = 0.6f),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
    
    Spacer(modifier = Modifier.height(8.dp))
    
    // Calendar Grid
    val firstDayOfMonth = Calendar.getInstance().apply {
        set(Calendar.YEAR, currentYear)
        set(Calendar.MONTH, currentMonth)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val daysInMonth = firstDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = (firstDayOfMonth.get(Calendar.DAY_OF_WEEK) + 5) % 7
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        var dayCounter = 1
        var weekCounter = 0
        
        while (dayCounter <= daysInMonth || weekCounter == 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (dayOfWeek in 0..6) {
                    if (weekCounter == 0 && dayOfWeek < firstDayOfWeek) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else if (dayCounter <= daysInMonth) {
                        val currentDay = dayCounter
                        val dayCalendar = Calendar.getInstance()
                        dayCalendar.set(Calendar.YEAR, currentYear)
                        dayCalendar.set(Calendar.MONTH, currentMonth)
                        dayCalendar.set(Calendar.DAY_OF_MONTH, currentDay)
                        dayCalendar.set(Calendar.HOUR_OF_DAY, 0)
                        dayCalendar.set(Calendar.MINUTE, 0)
                        dayCalendar.set(Calendar.SECOND, 0)
                        dayCalendar.set(Calendar.MILLISECOND, 0)
                        
                        val isDayToday = isToday(dayCalendar)
                        val isDayStart = isStartDate(dayCalendar)
                        val isDayEnd = isEndDate(dayCalendar)
                        val isDayInRange = isInRange(dayCalendar) && !isDayStart && !isDayEnd
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isDayStart || isDayEnd -> PrimaryGreen
                                        isDayInRange -> PrimaryGreen.copy(alpha = 0.2f)
                                        isDayToday -> PrimaryGreen.copy(alpha = 0.1f)
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable {
                                    val newCalendar = Calendar.getInstance()
                                    newCalendar.clear()
                                    newCalendar.set(Calendar.YEAR, currentYear)
                                    newCalendar.set(Calendar.MONTH, currentMonth)
                                    newCalendar.set(Calendar.DAY_OF_MONTH, currentDay)
                                    newCalendar.set(Calendar.HOUR_OF_DAY, 0)
                                    newCalendar.set(Calendar.MINUTE, 0)
                                    newCalendar.set(Calendar.SECOND, 0)
                                    newCalendar.set(Calendar.MILLISECOND, 0)
                                    val clickedDate = newCalendar.time
                                    
                                    when {
                                        tempStartDate == null || (tempStartDate != null && tempEndDate != null) -> {
                                            // Start new selection
                                            tempStartDate = clickedDate
                                            tempEndDate = null
                                        }
                                        clickedDate.before(tempStartDate!!) -> {
                                            // Clicked before start, make it new start
                                            tempStartDate = clickedDate
                                            tempEndDate = null
                                        }
                                        else -> {
                                            // Set as end date
                                            tempEndDate = clickedDate
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentDay.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = when {
                                    isDayStart || isDayEnd -> OnPrimary
                                    isDayToday -> PrimaryGreen
                                    else -> OnSurface
                                },
                                fontWeight = if (isDayStart || isDayEnd || isDayToday) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                        dayCounter++
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            weekCounter++
        }
    }
    
    // Confirm button
    Spacer(modifier = Modifier.height(16.dp))
    Button(
        onClick = {
            if (tempStartDate != null && tempEndDate != null) {
                // Ensure start is before end
                val start = if (tempStartDate!!.before(tempEndDate!!)) tempStartDate!! else tempEndDate!!
                val end = if (tempStartDate!!.before(tempEndDate!!)) tempEndDate!! else tempStartDate!!
                onDateRangeSelected(start, end)
            } else if (tempStartDate != null) {
                // If only start is selected, use it as both start and end (single day)
                onDateRangeSelected(tempStartDate!!, tempStartDate!!)
            }
        },
        enabled = tempStartDate != null,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryGreen
        )
    ) {
        Text(
            text = if (tempStartDate != null && tempEndDate != null) "Выбрать период" else if (tempStartDate != null) "Выбрать один день" else "Выберите даты",
            color = OnPrimary
        )
    }
}

