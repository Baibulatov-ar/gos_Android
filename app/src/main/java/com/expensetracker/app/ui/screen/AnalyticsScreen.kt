package com.expensetracker.app.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.expensetracker.app.data.model.Expense
import com.expensetracker.app.ui.component.CustomDateRangePickerDialog
import com.expensetracker.app.ui.theme.*
import com.expensetracker.app.ui.viewmodel.ExpenseViewModel
import com.expensetracker.app.util.getCategoryColor
import com.expensetracker.app.util.getCategoryDisplayName
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    expenseViewModel: ExpenseViewModel
) {
    var selectedPeriod by remember { mutableStateOf(0) } // 0: Day, 1: Week, 2: Month, 3: Custom
    var showDatePicker by remember { mutableStateOf(false) }
    var customStartDate by remember { mutableStateOf<Date?>(null) }
    var customEndDate by remember { mutableStateOf<Date?>(null) }
    
    val periods = listOf("День", "Неделя", "Месяц", "Период")
    
    val (startDate, endDate) = remember(selectedPeriod, customStartDate, customEndDate) {
        if (selectedPeriod == 3 && customStartDate != null && customEndDate != null) {
            // Custom period
            val start = Calendar.getInstance().apply {
                time = customStartDate!!
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val end = Calendar.getInstance().apply {
                time = customEndDate!!
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }
            start.time to end.time
        } else {
            val calendar = Calendar.getInstance()
            val end = Date()
            val start = when (selectedPeriod) {
                0 -> calendar.apply { 
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                1 -> calendar.apply { 
                    add(Calendar.DAY_OF_YEAR, -7)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                2 -> calendar.apply { 
                    add(Calendar.MONTH, -1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                else -> calendar
            }
            start.time to end
        }
    }

    // Current period expenses
    val expenses by expenseViewModel.getExpensesByDateRange(startDate, endDate)
        .collectAsState(initial = emptyList())
    
    // Check if period is a single day
    val isSingleDay = remember(startDate, endDate) {
        val startCal = Calendar.getInstance().apply { time = startDate }
        val endCal = Calendar.getInstance().apply { time = endDate }
        startCal.get(Calendar.YEAR) == endCal.get(Calendar.YEAR) &&
        startCal.get(Calendar.DAY_OF_YEAR) == endCal.get(Calendar.DAY_OF_YEAR)
    }
    
    // Previous period expenses for comparison
    val previousPeriodDuration = endDate.time - startDate.time
    val previousStartDate = Date(startDate.time - previousPeriodDuration - 86400000) // -1 day gap
    val previousEndDate = Date(startDate.time - 86400000) // -1 day before current start
    
    val previousExpenses by expenseViewModel.getExpensesByDateRange(previousStartDate, previousEndDate)
.collectAsState(initial = emptyList())

    val totalSpent = expenses.sumOf { it.amount }
    val previousTotalSpent = previousExpenses.sumOf { it.amount }
    val changePercent = if (previousTotalSpent > 0) {
        ((totalSpent - previousTotalSpent) / previousTotalSpent * 100)
    } else if (totalSpent > 0) {
        100.0
    } else {
        0.0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Аналитика", color = OnSurface) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundDark),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                // Period Selector
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        periods.forEachIndexed { index, period ->
                            FilterChip(
                                selected = selectedPeriod == index,
                                onClick = { 
                                    if (index == 3) {
                                        showDatePicker = true
                                    } else {
                                    selectedPeriod = index
                                        customStartDate = null
                                        customEndDate = null
                                    }
                                },
                                label = { Text(period) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = SurfaceDark,
                                    labelColor = OnSurface,
                                    selectedContainerColor = PrimaryGreen,
                                    selectedLabelColor = OnPrimary
                                ),
                                border = null
                            )
                        }
                    }
                    if (selectedPeriod == 3 && customStartDate != null && customEndDate != null) {
                        val dateFormat = SimpleDateFormat("d MMM", Locale.getDefault())
                        Text(
                            text = "${dateFormat.format(customStartDate!!)} - ${dateFormat.format(customEndDate!!)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimaryGreen,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            item {
                // Total Spend Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                    Text(
                        text = "${String.format("%.2f", totalSpent)} ₽",
                            style = MaterialTheme.typography.displayMedium,
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Bold
                    )
                        if (previousTotalSpent > 0 || totalSpent > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                        Icon(
                                    imageVector = if (changePercent >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = null,
                                    tint = if (changePercent >= 0) PrimaryGreen else Color(0xFFFF5252),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                                    text = "${if (changePercent >= 0) "+" else ""}${String.format("%.1f", changePercent)}%",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (changePercent >= 0) PrimaryGreen else Color(0xFFFF5252)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "к предыдущему периоду",
                            style = MaterialTheme.typography.bodySmall,
                                    color = OnSurface.copy(alpha = 0.7f)
                        )
                            }
                        }
                    }
                }
            }

            // Show empty state if no expenses
            if (expenses.isEmpty()) {
                item {
                    EmptyAnalyticsState()
                }
            } else {
            item {
                    // Breakdown Header
                    Text(
                        text = "Разбивка по категориям",
                        style = MaterialTheme.typography.titleLarge,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
            }

            item {
                CategoryBreakdownChart(expenses = expenses)
            }

                // Spending Trend - only show if not a single day
                if (!isSingleDay) {
                    item {
                        Text(
                            text = "Тренд расходов",
                            style = MaterialTheme.typography.titleLarge,
                            color = OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        SpendingTrendCard(expenses = expenses, period = selectedPeriod, startDate = startDate, endDate = endDate)
                    }
                }
            }
        }
    }
    
    // Date Range Picker Dialog
    if (showDatePicker) {
        CustomDateRangePickerDialog(
            startDate = customStartDate,
            endDate = customEndDate,
            onDateRangeSelected = { start, end ->
                customStartDate = start
                customEndDate = end
                selectedPeriod = 3
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
fun CategoryBreakdownChart(expenses: List<Expense>) {
    val categoryTotals = expenses.groupBy { it.category }
        .mapValues { it.value.sumOf { e -> e.amount } }
        .toList()
        .sortedByDescending { it.second }

    val total = expenses.sumOf { it.amount }
    if (total == 0.0 || categoryTotals.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(16.dp)
        ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                    .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                    text = "Нет расходов за выбранный период",
                    color = OnSurface.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodyMedium
            )
            }
        }
        return
    }

    // Get colors for categories
    val categoryColors = categoryTotals.map { (category, _) ->
        getCategoryColor(category)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier.size(140.dp),
                contentAlignment = Alignment.Center
            ) {
                DonutChart(
                    data = categoryTotals.map { it.second.toFloat() },
                    colors = categoryColors
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                categoryTotals.take(5).forEachIndexed { index, (category, amount) ->
                    val percentage = (amount / total * 100).toInt()
                    CategoryBreakdownItem(
                        category = category,
                        amount = amount,
                        percentage = percentage,
                        color = categoryColors[index % categoryColors.size]
                    )
                }
            }
        }
    }
}

@Composable
fun DonutChart(data: List<Float>, colors: List<Color>) {
    val total = data.sum()
    if (total == 0f) return

    var startAngle = -90f

    Canvas(modifier = Modifier.size(140.dp)) {
        val strokeWidth = 32.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2

        data.forEachIndexed { index, value ->
            val sweepAngle = (value / total * 360)
            val color = colors[index % colors.size]

            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(
                    (size.width - radius * 2) / 2,
                    (size.height - radius * 2) / 2
                ),
                size = Size(radius * 2, radius * 2)
            )

            startAngle += sweepAngle
        }
    }
}

@Composable
fun CategoryBreakdownItem(
    category: String,
    amount: Double,
    percentage: Int,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
            Text(
                    text = getCategoryDisplayName(category),
                style = MaterialTheme.typography.bodyMedium,
                    color = OnSurface,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurface.copy(alpha = 0.6f)
                )
            }
        }
        Text(
            text = "${String.format("%.0f", amount)} ₽",
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurface,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SpendingTrendCard(
    expenses: List<Expense>,
    period: Int,
    startDate: Date,
    endDate: Date
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val (labels, data) = when (period) {
                1 -> {
                    // Week - show days with current day as last
                    val today = Calendar.getInstance()
                    val todayDayOfWeek = (today.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Monday = 0, Sunday = 6
                    
                    // Create list starting from tomorrow, ending with today
                    val dayNames = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
                    val days = mutableListOf<String>()
                    val dayExpenses = mutableListOf<Double>()
                    
                    // Start from tomorrow (todayDayOfWeek + 1) and go to today
                    for (i in 1..7) {
                        val dayIndex = (todayDayOfWeek + i) % 7
                        days.add(dayNames[dayIndex])
                        
                        val dayTotal = expenses.filter { expense ->
                            val cal = Calendar.getInstance().apply { time = expense.date }
                            val expenseDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7
                            expenseDayOfWeek == dayIndex
                        }.sumOf { it.amount }
                        dayExpenses.add(dayTotal)
                    }
                    
                    days to dayExpenses
                }
                2 -> {
                    // Month - show weeks properly
                    val calendar = Calendar.getInstance().apply { time = startDate }
                    val today = Calendar.getInstance()
                    val weeks = mutableListOf<String>()
                    val weekExpenses = mutableListOf<Double>()
                    var currentDate = calendar.clone() as Calendar
                    
                    // Find first Monday of the period
                    while (currentDate.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY && 
                           currentDate.time.before(endDate)) {
                        currentDate.add(Calendar.DAY_OF_YEAR, -1)
                    }
                    
                    // If we went back too far, move to next Monday
                    if (currentDate.time.before(startDate)) {
                        currentDate.add(Calendar.DAY_OF_YEAR, 7)
                    }
                    
                    while (currentDate.time.before(endDate) || currentDate.time == endDate) {
                        val weekStart = currentDate.clone() as Calendar
                        val weekEnd = currentDate.clone() as Calendar
                        
                        // Week ends on Sunday or endDate, whichever comes first
                        val daysToAdd = minOf(6, 
                            ((endDate.time - weekStart.time.time) / (1000 * 60 * 60 * 24)).toInt()
                        )
                        weekEnd.add(Calendar.DAY_OF_YEAR, daysToAdd)
                        
                        // Ensure weekEnd doesn't exceed endDate
                        if (weekEnd.time.after(endDate)) {
                            weekEnd.time = endDate
                        }
                        
                        val startDay = weekStart.get(Calendar.DAY_OF_MONTH)
                        val endDay = weekEnd.get(Calendar.DAY_OF_MONTH)
                        
                        val weekLabel = "$startDay-$endDay"
                        weeks.add(weekLabel)
                        
                        val weekTotal = expenses.filter { expense ->
                            val expenseDate = expense.date
                            expenseDate >= weekStart.time && expenseDate <= weekEnd.time
                        }.sumOf { it.amount }
                        weekExpenses.add(weekTotal)
                        
                        currentDate.add(Calendar.DAY_OF_YEAR, 7)
                        if (weekEnd.time >= endDate) break
                    }
                    weeks to weekExpenses
                }
                else -> {
                    // Custom period - group days evenly (4-7 groups)
                    val calendar = Calendar.getInstance().apply { time = startDate }
                    
                    // Calculate total days
                    val totalDays = ((endDate.time - startDate.time) / (1000 * 60 * 60 * 24)).toInt() + 1
                    
                    val days = mutableListOf<String>()
                    val dayExpenses = mutableListOf<Double>()
                    
                    // Determine optimal number of groups (4-7)
                    val optimalGroups = when {
                        totalDays <= 4 -> totalDays
                        totalDays <= 7 -> 4
                        totalDays <= 14 -> 5
                        totalDays <= 21 -> 6
                        else -> 7
                    }
                    
                    // Calculate base group size and remainder for even distribution
                    val baseGroupSize = totalDays / optimalGroups
                    val remainder = totalDays % optimalGroups
                    
                    var currentDate = calendar.clone() as Calendar
                    val dateFormat = SimpleDateFormat("d MMM", Locale.getDefault())
                    
                    for (groupIndex in 0 until optimalGroups) {
                        val groupStart = currentDate.clone() as Calendar
                        var groupEnd = currentDate.clone() as Calendar
                        var groupTotal = 0.0
                        
                        // Distribute remainder days evenly across first groups
                        val currentGroupSize = baseGroupSize + if (groupIndex < remainder) 1 else 0
                        
                        // Collect expenses for this group
                        for (i in 0 until currentGroupSize) {
                            if (groupEnd.time.after(endDate)) break
                            
                            val dayTotal = expenses.filter { expense ->
                                val expenseCal = Calendar.getInstance().apply { time = expense.date }
                                expenseCal.get(Calendar.YEAR) == groupEnd.get(Calendar.YEAR) &&
                                expenseCal.get(Calendar.DAY_OF_YEAR) == groupEnd.get(Calendar.DAY_OF_YEAR)
                            }.sumOf { it.amount }
                            groupTotal += dayTotal
                            
                            if (i < currentGroupSize - 1) {
                                groupEnd.add(Calendar.DAY_OF_YEAR, 1)
                                if (groupEnd.time.after(endDate)) break
                            }
                        }
                        
                        // Format label
                        val startDay = groupStart.get(Calendar.DAY_OF_MONTH)
                        val startMonth = groupStart.get(Calendar.MONTH)
                        val endDay = groupEnd.get(Calendar.DAY_OF_MONTH)
                        val endMonth = groupEnd.get(Calendar.MONTH)
                        
                        val label = if (startDay == endDay && startMonth == endMonth) {
                            startDay.toString()
                        } else if (startMonth == endMonth) {
                            "$startDay-$endDay"
                        } else {
                            // Different months
                            val startMonthName = dateFormat.format(groupStart.time).split(" ")[1]
                            val endMonthName = dateFormat.format(groupEnd.time).split(" ")[1]
                            if (startMonthName == endMonthName) {
                                "$startDay-$endDay"
                            } else {
                                "$startDay $startMonthName - $endDay $endMonthName"
                            }
                        }
                        
                        days.add(label)
                        dayExpenses.add(groupTotal)
                        
                        // Move to next group
                        currentDate.add(Calendar.DAY_OF_YEAR, currentGroupSize)
                        if (currentDate.time.after(endDate)) break
                    }
                    
                    days to dayExpenses
                }
            }
            
            val maxExpense = data.maxOrNull() ?: 1.0
            val chartHeight = 160.dp
            
            // Animated chart
                Box(
                    modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight + 40.dp)
            ) {
                // Chart with line and bars
                SpendingTrendChart(
                    data = data,
                    labels = labels,
                    maxValue = maxExpense,
                    chartHeight = chartHeight
                )
            }
        }
    }
}

@Composable
fun SpendingTrendChart(
    data: List<Double>,
    labels: List<String>,
    maxValue: Double,
    chartHeight: Dp
) {
    val padding = 20.dp
    val pointRadius = 6.dp
    val lineStrokeWidth = 3.dp
    val currentDayIndex = labels.size - 1 // Last day is current day
    
    // Animate chart appearance
    var animationProgress by remember { mutableStateOf(0f) }
    LaunchedEffect(data) {
        animationProgress = 0f
        animationProgress = 1f
    }
    val animatedProgress by animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(durationMillis = 800)
    )
    
    Column {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight)
        ) {
            if (data.isEmpty() || maxValue == 0.0) return@Canvas
            
            val chartAreaHeight = chartHeight.toPx()
            val chartAreaWidth = size.width - padding.toPx() * 2f
            val startX = padding.toPx()
            val startY = padding.toPx()
            val endY = startY + chartAreaHeight - padding.toPx()
            
            // Calculate point positions to match label positions
            // Labels use SpaceBetween with weight(1f), so each label is centered in its weighted space
            // We need to calculate the center of each weighted segment
            val labelAreaWidth = chartAreaWidth
            val pointPositions = if (data.size == 1) {
                listOf(startX + labelAreaWidth / 2f)
            } else {
                // Each label gets equal weight, so we calculate center of each segment
                val segmentWidth = labelAreaWidth / data.size.toFloat()
                (0 until data.size).map { index ->
                    startX + segmentWidth * (index + 0.5f)
                }
            }
            
            // Draw grid lines (subtle)
            val gridLineColor = OnSurface.copy(alpha = 0.08f)
            for (i in 0..4) {
                val y = startY + ((endY - startY) / 4) * i
                drawLine(
                    color = gridLineColor,
                    start = Offset(startX, y),
                    end = Offset(startX + chartAreaWidth, y),
                    strokeWidth = 0.5.dp.toPx()
                )
            }
            
            // Calculate points for smooth line chart - aligned with labels
            val points = data.mapIndexed { index, value ->
                val x = pointPositions[index]
                val normalizedValue = (value.toFloat() / maxValue.toFloat()).coerceIn(0f, 1f)
                val y = endY - ((endY - startY) * normalizedValue * animatedProgress)
                Offset(x, y)
            }
            
            // Draw gradient area under line (smooth curve through all points)
            if (points.size > 1) {
                val path = Path().apply {
                    moveTo(points.first().x, endY)
                    lineTo(points.first().x, points.first().y)
                    
                    // Create smooth curve that passes through all points
                    if (points.size == 2) {
                        // Simple line for 2 points
                        lineTo(points.last().x, points.last().y)
                    } else {
                        // Smooth curve through all points using cubic bezier
                        for (i in 0 until points.size - 1) {
                            val current = points[i]
                            val next = points[i + 1]
                            
                            if (i == 0) {
                                // First segment: use next point as control
                                val controlX = current.x + (next.x - current.x) * 0.3f
                                val controlY = current.y
                                cubicTo(
                                    x1 = controlX, y1 = controlY,
                                    x2 = next.x - (next.x - current.x) * 0.3f, y2 = next.y,
                                    x3 = next.x, y3 = next.y
                                )
                            } else if (i == points.size - 2) {
                                // Last segment: use previous point as reference
                                val prev = points[i - 1]
                                val controlX = current.x + (next.x - current.x) * 0.3f
                                val controlY = current.y
                                cubicTo(
                                    x1 = current.x + (next.x - current.x) * 0.3f, y1 = current.y,
                                    x2 = next.x - (next.x - current.x) * 0.3f, y2 = next.y,
                                    x3 = next.x, y3 = next.y
                                )
                            } else {
                                // Middle segments: smooth transition
                                val prev = points[i - 1]
                                val nextNext = points[i + 1]
                                
                                // Control points for smooth curve
                                val cp1x = current.x + (next.x - prev.x) * 0.15f
                                val cp1y = current.y
                                val cp2x = next.x - (nextNext.x - current.x) * 0.15f
                                val cp2y = next.y
                                
                                cubicTo(
                                    x1 = cp1x, y1 = cp1y,
                                    x2 = cp2x, y2 = cp2y,
                                    x3 = next.x, y3 = next.y
                                )
                            }
                        }
                    }
                    
                    lineTo(points.last().x, endY)
                    close()
                }
                
                val minY = points.minOfOrNull { it.y } ?: endY
                val gradient = Brush.verticalGradient(
                    colors = listOf(
                        PrimaryGreen.copy(alpha = 0.3f),
                        PrimaryGreen.copy(alpha = 0.15f),
                        PrimaryGreen.copy(alpha = 0.0f)
                    ),
                    startY = minY,
                    endY = endY
                )
                
                drawPath(path, brush = gradient)
            }
            
            // Draw smooth trend line (passes through all points)
            if (points.size > 1) {
                val linePath = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    
                    // Create smooth curve that passes through all points
                    if (points.size == 2) {
                        // Simple line for 2 points
                        lineTo(points.last().x, points.last().y)
                    } else {
                        // Smooth curve through all points using cubic bezier
                        for (i in 0 until points.size - 1) {
                            val current = points[i]
                            val next = points[i + 1]
                            
                            if (i == 0) {
                                // First segment
                                val controlX = current.x + (next.x - current.x) * 0.3f
                                val controlY = current.y
                                cubicTo(
                                    x1 = controlX, y1 = controlY,
                                    x2 = next.x - (next.x - current.x) * 0.3f, y2 = next.y,
                                    x3 = next.x, y3 = next.y
                                )
                            } else if (i == points.size - 2) {
                                // Last segment
                                cubicTo(
                                    x1 = current.x + (next.x - current.x) * 0.3f, y1 = current.y,
                                    x2 = next.x - (next.x - current.x) * 0.3f, y2 = next.y,
                                    x3 = next.x, y3 = next.y
                                )
                            } else {
                                // Middle segments: smooth transition
                                val prev = points[i - 1]
                                val nextNext = points[i + 1]
                                
                                // Control points for smooth curve
                                val cp1x = current.x + (next.x - prev.x) * 0.15f
                                val cp1y = current.y
                                val cp2x = next.x - (nextNext.x - current.x) * 0.15f
                                val cp2y = next.y
                                
                                cubicTo(
                                    x1 = cp1x, y1 = cp1y,
                                    x2 = cp2x, y2 = cp2y,
                                    x3 = next.x, y3 = next.y
                                )
                            }
                        }
                    }
                }
                
                drawPath(
                    path = linePath,
                    color = PrimaryGreen,
                    style = Stroke(width = lineStrokeWidth.toPx(), cap = StrokeCap.Round)
                )
            }
            
            // Draw points on line (highlight current day)
            points.forEachIndexed { index, point ->
                val isCurrentDay = index == currentDayIndex
                
                if (isCurrentDay) {
                    // Highlight current day with white circle and green border
                    drawCircle(
                        color = Color.White,
                        radius = pointRadius.toPx() + 2.dp.toPx(),
                        center = point
                    )
                    drawCircle(
                        color = PrimaryGreen,
                        radius = pointRadius.toPx() + 2.dp.toPx(),
                        center = point,
                        style = Stroke(width = 2.dp.toPx())
                    )
                    drawCircle(
                        color = PrimaryGreen,
                        radius = pointRadius.toPx() * 0.6f,
                        center = point
                    )
                } else {
                    // Regular points
                    drawCircle(
                        color = PrimaryGreen,
                        radius = pointRadius.toPx(),
                        center = point
                    )
                    drawCircle(
                        color = Color.White,
                        radius = pointRadius.toPx() * 0.5f,
                        center = point
                    )
                }
            }
        }
        
        // Labels below chart (highlight current day)
        // Use BoxWithConstraints to get exact width for alignment
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = padding, vertical = 12.dp)
        ) {
            val labelAreaWidth = constraints.maxWidth.toFloat()
            val labelSpacing = if (labels.size > 1) labelAreaWidth / (labels.size - 1) else 0f
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                labels.forEachIndexed { index, label ->
                    val isCurrentDay = index == currentDayIndex
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isCurrentDay) PrimaryGreen else OnSurface.copy(alpha = 0.6f),
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            fontWeight = if (isCurrentDay) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyAnalyticsState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Analytics,
                contentDescription = null,
                tint = OnSurface.copy(alpha = 0.3f),
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = "Нет данных",
                style = MaterialTheme.typography.titleLarge,
                color = OnSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "За выбранный период нет расходов.\nВыберите другой период или добавьте расходы.",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}
