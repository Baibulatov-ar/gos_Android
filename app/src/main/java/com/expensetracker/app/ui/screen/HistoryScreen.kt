package com.expensetracker.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.expensetracker.app.data.model.Category
import com.expensetracker.app.data.model.Expense
import com.expensetracker.app.ui.component.CustomDatePickerDialog
import com.expensetracker.app.ui.component.CustomDateRangePickerDialog
import com.expensetracker.app.ui.theme.*
import com.expensetracker.app.ui.viewmodel.ExpenseViewModel
import com.expensetracker.app.util.DefaultCategories
import com.expensetracker.app.util.getCategoryColor
import com.expensetracker.app.util.getCategoryDisplayName
import com.expensetracker.app.util.getCategoryIcon
import com.expensetracker.app.util.normalizeCategoryName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    expenseViewModel: ExpenseViewModel,
    onBackClick: () -> Unit,
    onExpenseClick: (Expense) -> Unit
) {
    val allExpenses by expenseViewModel.getAllExpenses().collectAsState(initial = emptyList())
    
    // Date range state
    var showDatePicker by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf<Date?>(null) }
    var endDate by remember { mutableStateOf<Date?>(null) }
    var selectedCategories by remember { mutableStateOf<Set<String>>(emptySet()) }
    var showCategoryDialog by remember { mutableStateOf(false) }

    // Function to show Date Range Picker
    fun showDateRangePicker() {
        showDatePicker = true
    }

    val filteredExpenses = remember(allExpenses, startDate, endDate, selectedCategories) {
        allExpenses.filter { expense ->
            val dateMatch = if (startDate != null && endDate != null) {
                val expenseTime = expense.date.time
                expenseTime >= startDate!!.time && expenseTime <= endDate!!.time
            } else {
                true // No filtering if no full range is selected (or just let user clear it)
            }
            
            val categoryMatch = if (selectedCategories.isEmpty()) {
                true
            } else {
                // Normalize expense category and check if it matches any selected category
                val expenseCategoryVariants = normalizeCategoryName(expense.category)
                selectedCategories.any { selectedCategory ->
                    val selectedCategoryVariants = normalizeCategoryName(selectedCategory)
                    expenseCategoryVariants.intersect(selectedCategoryVariants).isNotEmpty()
                }
            }
            
            dateMatch && categoryMatch
        }
    }

    val groupedExpenses = filteredExpenses.groupBy {
        val calendar = Calendar.getInstance().apply { time = it.date }
        calendar.get(Calendar.DAY_OF_YEAR) to calendar.get(Calendar.YEAR)
    }

    val totalSpent = filteredExpenses.sumOf { it.amount }

    var deletedExpense by remember { mutableStateOf<Expense?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("История", color = OnSurface) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = OnSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark
                )
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundDark)
        ) {
            // Filters and Total Summary
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Total Spent Card - only show when period is selected
                if (startDate != null && endDate != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Всего за период",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurface.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${String.format("%.2f", totalSpent)} ₽",
                                style = MaterialTheme.typography.displaySmall,
                                color = PrimaryGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Filter Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Date Filter
                    FilterChip(
                        selected = startDate != null,
                        onClick = { showDateRangePicker() },
                        label = {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (startDate != null && endDate != null) {
                                    val format = SimpleDateFormat("dd.MM", Locale.getDefault())
                                    "${format.format(startDate!!)} - ${format.format(endDate!!)}"
                                } else "Период"
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = SurfaceDark,
                            labelColor = OnSurface,
                            selectedContainerColor = PrimaryGreen,
                            selectedLabelColor = OnPrimary
                        ),
                        border = null
                    )

                    // Category Filter
                    FilterChip(
                        selected = selectedCategories.isNotEmpty(),
                        onClick = { showCategoryDialog = true },
                        label = {
                            Text("Категория")
                        },
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

            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val sortedGroups = groupedExpenses.toList().sortedByDescending { (key, _) ->
                    key.second * 1000 + key.first
                }
                
                sortedGroups.forEach { (_, expenses) ->
                    val firstExpense = expenses.first()
                    val dateLabel = getDateLabel(firstExpense.date)

                    item {
                        Text(
                            text = dateLabel,
                            style = MaterialTheme.typography.titleMedium,
                            color = OnSurface.copy(alpha = 0.7f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(
                        items = expenses,
                        key = { it.id }
                    ) { expense ->
                        SwipeableExpenseItem(
                            expense = expense,
                            onExpenseClick = { onExpenseClick(expense) },
                            onDelete = {
                                deletedExpense = expense
                                expenseViewModel.deleteExpense(expense)
                                CoroutineScope(Dispatchers.Main).launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Транзакция удалена",
                                        duration = SnackbarDuration.Short,
                                        withDismissAction = true
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        CustomDateRangePickerDialog(
            startDate = startDate,
            endDate = endDate,
            onDateRangeSelected = { start, end ->
                // Ensure correct start/end of day
                val startCal = Calendar.getInstance().apply {
                    time = start
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                
                val endCal = Calendar.getInstance().apply {
                    time = end
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                    set(Calendar.MILLISECOND, 999)
                }
                
                startDate = startCal.time
                endDate = endCal.time
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    // Category selection dialog
    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = {
                Text(
                    text = "Выберите категории",
                    style = MaterialTheme.typography.titleLarge,
                    color = OnSurface
                )
            },
            text = {
                Box(modifier = Modifier.height(300.dp)) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(DefaultCategories.categories) { category ->
                            CategoryChip(
                                category = category,
                                isSelected = category.name in selectedCategories,
                                onClick = {
                                    selectedCategories = if (category.name in selectedCategories) {
                                        selectedCategories - category.name
                                    } else {
                                        selectedCategories + category.name
                                    }
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryDialog = false }) {
                    Text("Готово", color = PrimaryGreen)
                }
            },
            containerColor = SurfaceDark,
            titleContentColor = OnSurface,
            textContentColor = OnSurface
        )
    }

    // Show snackbar with undo action
    LaunchedEffect(deletedExpense) {
        deletedExpense?.let { expense ->
            val result = snackbarHostState.showSnackbar(
                message = "Транзакция удалена",
                actionLabel = "ОТМЕНИТЬ",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                expenseViewModel.addExpense(expense)
            }
            deletedExpense = null
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableExpenseItem(
    expense: Expense,
    onExpenseClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberDismissState(
        confirmValueChange = {
            if (it == DismissValue.DismissedToStart) {
                onDelete()
                true
            } else {
                false
            }
        },
        positionalThreshold = { it * .25f }
    )

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        background = {
            val color = Color(0xFFE53935) // Red color
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        },
        dismissContent = {
            ExpenseItemContent(expense, onExpenseClick)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseItemContent(
    expense: Expense,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        onClick = onClick
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
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(getCategoryColor(expense.category)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getCategoryIcon(expense.category),
                        contentDescription = null,
                        tint = OnPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    val mainText = expense.note ?: expense.category
                    val subText = if (expense.note != null) {
                         getCategoryDisplayName(expense.category)
                    } else {
                        null
                    }

                    Text(
                        text = mainText,
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurface
                    )
                    if (subText != null) {
                        Text(
                            text = subText,
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurface.copy(alpha = 0.6f)
                        )
                    }
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

fun getDateLabel(date: Date): String {
    val calendar = Calendar.getInstance()
    val expenseCalendar = Calendar.getInstance().apply { time = date }
    
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    
    val yesterday = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    
    val expenseDate = Calendar.getInstance().apply {
        time = date
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    
    return when {
        expenseDate.timeInMillis == today.timeInMillis -> "СЕГОДНЯ"
        expenseDate.timeInMillis == yesterday.timeInMillis -> "ВЧЕРА"
        else -> {
            val format = SimpleDateFormat("d MMM", Locale.getDefault())
            format.format(date)
        }
    }
}

