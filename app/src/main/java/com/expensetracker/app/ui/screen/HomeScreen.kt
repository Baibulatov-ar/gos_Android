package com.expensetracker.app.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expensetracker.app.data.model.Expense
import com.expensetracker.app.ui.component.CustomDatePickerDialog
import com.expensetracker.app.ui.theme.*
import com.expensetracker.app.ui.viewmodel.ExpenseViewModel
import com.expensetracker.app.util.getCategoryColor
import com.expensetracker.app.util.getCategoryIcon
import com.expensetracker.app.util.getCategoryDisplayName
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    expenseViewModel: ExpenseViewModel,
    dailyLimitViewModel: com.expensetracker.app.ui.viewmodel.DailyLimitViewModel,
    selectedDate: Date?,
    onDateSelected: (Date) -> Unit,
    onAddExpenseClick: () -> Unit,
    onSeeAllClick: () -> Unit,
    onCategoryClick: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    
    val calendar = Calendar.getInstance()
    val today = calendar.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time
    
    val displayDate = selectedDate ?: today
    
    // Compare dates without time
    val displayCalendar = Calendar.getInstance().apply {
        time = displayDate
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val todayCalendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val isToday = displayCalendar.timeInMillis == todayCalendar.timeInMillis
    
    val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("ru"))
    val dateText = if (isToday) "Сегодня" else dateFormat.format(displayDate)
    
    val expenses by remember(displayDate) {
        expenseViewModel.getExpensesByDateRange(
            displayDate,
            Calendar.getInstance().apply {
                time = displayDate
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.time
        )
    }.collectAsState(initial = emptyList())
    
    // Check if there are any expenses at all (for "Все" button)
    val allExpenses by expenseViewModel.getAllExpenses().collectAsState(initial = emptyList())
    val hasAnyExpenses = allExpenses.isNotEmpty()
    
    val total = expenses.sumOf { it.amount }
    
    // Get limit for the selected date
    val dailyLimit by remember(displayDate) {
        dailyLimitViewModel.getLimitForDate(displayDate)
    }.collectAsState(initial = 1500.0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showDatePicker = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Выбрать дату",
                        tint = PrimaryGreen
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurface
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = BackgroundDark
            )
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                // Total Spent Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Всего потрачено",
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${String.format("%.2f", total)} ₽",
                        style = MaterialTheme.typography.displayMedium,
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                // Donut Chart
                ExpenseDonutChart(
                    expenses = expenses,
                    onCategoryClick = onCategoryClick
                )
            }

            item {
                // Daily Budget Section - always show for any date
                DailyBudgetCard(
                    spent = total,
                    limit = dailyLimit
                )
            }

            item {
                // Recent Transactions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Недавние расходы",
                        style = MaterialTheme.typography.titleLarge,
                        color = OnSurface
                    )
                    if (hasAnyExpenses) {
                        TextButton(onClick = onSeeAllClick) {
                            Text(
                                text = "Все",
                                color = PrimaryGreen
                            )
                        }
                    }
                }
            }

            if (expenses.isEmpty()) {
                item {
                    // Empty state placeholder
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = OnSurface.copy(alpha = 0.4f)
                            )
                            Text(
                                text = "Нет транзакций за сегодня",
                                style = MaterialTheme.typography.titleMedium,
                                color = OnSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "Добавьте первую транзакцию, чтобы начать отслеживать расходы",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurface.copy(alpha = 0.5f),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            } else {
                items(expenses.take(4)) { expense ->
                    ExpenseItem(expense = expense)
                }
            }
            
            item {
                // Large Add Expense Button at the bottom
                Button(
                    onClick = { onAddExpenseClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Expense",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Добавить расходы",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
    
    // Custom Date Picker Dialog
    if (showDatePicker) {
        CustomDatePickerDialog(
            selectedDate = displayDate,
            onDateSelected = { date ->
                // Use the date directly - it's already normalized in the picker
                onDateSelected(date)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
fun ExpenseDonutChart(
    expenses: List<Expense>,
    onCategoryClick: (String) -> Unit
) {
    val categoryTotals = expenses.groupBy { it.category }
        .mapValues { it.value.sumOf { e -> e.amount } }
        .toList()
        .sortedByDescending { it.second }

    val total = expenses.sumOf { it.amount }
    if (total == 0.0) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(SurfaceDark),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Нет расходов",
                    color = OnSurface.copy(alpha = 0.5f)
                )
            }
        }
        return
    }

    var startAngle = -90f
    val colors = listOf(PrimaryGreen, TransportColor, ShoppingColor, BillsColor, EntertainmentColor, HealthColor)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(200.dp)
                .clickable { }
        ) {
            val strokeWidth = 40.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2

            categoryTotals.forEachIndexed { index, (category, amount) ->
                val sweepAngle = (amount / total * 360).toFloat()
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

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { }
        ) {
            Text(
                text = "Всего потрачено",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurface.copy(alpha = 0.7f)
            )
            Text(
                text = "${String.format("%.2f", total)} ₽",
                style = MaterialTheme.typography.titleLarge,
                color = PrimaryGreen,
                fontWeight = FontWeight.Bold
            )
        }
    }

    // Category indicators
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        categoryTotals.take(4).forEachIndexed { index, (category, _) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onCategoryClick(category) }
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(colors[index % colors.size])
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = getCategoryDisplayName(category),
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurface
                )
            }
        }
    }
}

@Composable
fun DailyBudgetCard(
    spent: Double,
    limit: Double
) {
    val progress = ((spent / limit).toFloat()).coerceIn(0f, 1f)
    val remaining = (limit - spent).coerceAtLeast(0.0)
    val isOverLimit = spent > limit

    val progressColor = if (isOverLimit) Error else PrimaryGreen

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Дневной бюджет",
                style = MaterialTheme.typography.titleMedium,
                color = OnSurface
            )

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = progressColor,
                trackColor = SurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Использовано ${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "Лимит: ${String.format("%.2f", limit)} ₽",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurface.copy(alpha = 0.7f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isOverLimit) Icons.Default.Warning else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (isOverLimit) Error else PrimaryGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isOverLimit) {
                        "Вы превысили дневной лимит"
                    } else {
                        "Вы в пределах дневного лимита"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isOverLimit) Error else PrimaryGreen
                )
            }
        }
    }
}

@Composable
fun ExpenseItem(expense: Expense) {
    val dateFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val isToday = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time.before(expense.date)

    val dateText = if (isToday) {
        "Сегодня, ${timeFormat.format(expense.date)}"
    } else {
        dateFormat.format(expense.date)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(getCategoryColor(expense.category)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getCategoryIcon(expense.category),
                        contentDescription = null,
                        tint = OnPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    val mainText = expense.note ?: getCategoryDisplayName(expense.category)
                    val subText = if (expense.note != null) {
                        "${getCategoryDisplayName(expense.category)} • $dateText"
                    } else {
                        dateText
                    }

                    Text(
                        text = mainText,
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurface
                    )
                    Text(
                        text = subText,
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurface.copy(alpha = 0.6f)
                    )
                }
            }
            Text(
                text = "-${String.format("%.2f", expense.amount)} ₽",
                style = MaterialTheme.typography.titleMedium,
                color = OnSurface,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


