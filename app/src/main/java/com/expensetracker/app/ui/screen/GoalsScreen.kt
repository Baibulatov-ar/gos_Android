package com.expensetracker.app.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.expensetracker.app.data.model.Goal
import com.expensetracker.app.ui.theme.*
import com.expensetracker.app.ui.viewmodel.DailyLimitViewModel
import com.expensetracker.app.ui.viewmodel.GoalViewModel
import com.expensetracker.app.util.getGoalIcon
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    dailyLimitViewModel: DailyLimitViewModel,
    goalViewModel: GoalViewModel,
    onCreateGoalClick: () -> Unit,
    onEditDailyLimitClick: () -> Unit,
    onArchiveClick: () -> Unit = {}
) {
    val dailyLimit by dailyLimitViewModel.dailyLimit.collectAsState()
    val goals by goalViewModel.getAllGoals().collectAsState(initial = emptyList())
    val archivedGoals by goalViewModel.getArchivedGoals().collectAsState(initial = emptyList())
    val hasArchivedGoals = archivedGoals.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Цели и лимиты", color = OnSurface) },
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
                // Daily Spending Limit
                DailyLimitSection(
                    limit = dailyLimit,
                    onEditClick = onEditDailyLimitClick
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Финансовые цели",
                        style = MaterialTheme.typography.titleLarge,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    if (hasArchivedGoals) {
                        TextButton(onClick = onArchiveClick) {
                            Text(
                                text = "Архив",
                                color = PrimaryGreen
                            )
                        }
                    }
                }
            }

            items(goals) { goal ->
                GoalCard(goal = goal, goalViewModel = goalViewModel)
            }

            item {
                OutlinedButton(
                    onClick = onCreateGoalClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = OnSurface
                    ),
                    border = BorderStroke(1.dp, SurfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Создать новую цель")
                }
            }
        }
    }
}

@Composable
fun DailyLimitSection(
    limit: Double,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .clickable { onEditClick() },
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "ДНЕВНОЙ ЛИМИТ РАСХОДОВ",
                style = MaterialTheme.typography.labelLarge,
                color = OnSurface.copy(alpha = 0.7f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${String.format("%.0f", limit)} ₽",
                    style = MaterialTheme.typography.displaySmall,
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Изменить",
                        tint = OnSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = PrimaryGreen
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Это здоровый баланс! Помогает оставаться в рамках.",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryGreen
                    )
                }
            }
        }
    }
}

@Composable
fun GoalCard(
    goal: Goal,
    goalViewModel: GoalViewModel
) {
    var showAddAmountDialog by remember { mutableStateOf(false) }
    var amountToAdd by remember { mutableStateOf("") }
    var showCompletionDialog by remember { mutableStateOf(false) }
    var wasCompleted by remember { mutableStateOf(false) }
    
    val progress = ((goal.currentAmount / goal.targetAmount).toFloat()).coerceIn(0f, 1f)
    val isCompleted = progress >= 1f && !goal.isArchived
    val remaining = goal.targetAmount - goal.currentAmount
    val dateFormat = SimpleDateFormat("d MMM", Locale("ru"))
    val dateFormatFull = SimpleDateFormat("d MMM yyyy", Locale("ru"))
    
    // Animation for completed goal
    val infiniteTransition = rememberInfiniteTransition(label = "completion")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    // Check if goal just completed
    LaunchedEffect(goal.id, goal.currentAmount, goal.targetAmount) {
        val justCompleted = progress >= 1f && !goal.isArchived && !wasCompleted
        if (justCompleted) {
            wasCompleted = true
            showCompletionDialog = true
        }
    }
    
    val progressColor = when {
        isCompleted -> PrimaryGreen
        progress >= 0.9f -> PrimaryGreen
        progress >= 0.5f -> Color(0xFF2196F3) // Blue
        else -> Color(0xFFFF9800) // Orange
    }
    
    val progressText = when {
        isCompleted -> "Цель достигнута! 🎉"
        progress >= 0.9f -> "Почти готово!"
        progress >= 0.5f -> "Накоплено ${(progress * 100).toInt()}%"
        else -> "Медленно, но верно"
    }
    
    val dateText = if (goal.targetDate != null) {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val targetCal = Calendar.getInstance().apply { time = goal.targetDate!! }
        
        val targetFormatted = if (targetCal.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
            dateFormat.format(goal.targetDate)
        } else {
            dateFormatFull.format(goal.targetDate)
        }
        "Срок: $targetFormatted"
    } else {
        "Срок не установлен"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isCompleted) Modifier.scale(scale) else Modifier),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = getGoalIcon(goal.icon),
                        contentDescription = null,
                        tint = progressColor,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = goal.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = dateText,
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                IconButton(onClick = { showAddAmountDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Добавить сумму",
                        tint = PrimaryGreen
                    )
                }
            }

            Text(
                text = "${String.format("%.0f", goal.currentAmount)} ₽ из ${String.format("%.0f", goal.targetAmount)} ₽",
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurface,
                fontWeight = FontWeight.Medium
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = progressText,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (progress >= 0.9f) PrimaryGreen else OnSurface.copy(alpha = 0.7f),
                    fontWeight = if (progress >= 0.9f) FontWeight.Bold else FontWeight.Normal
                )
                if (remaining > 0) {
                    Text(
                        text = "Осталось ${String.format("%.0f", remaining)} ₽",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
    
    // Dialog for adding amount
    if (showAddAmountDialog) {
        AddAmountToGoalDialog(
            goal = goal,
            onDismiss = { 
                showAddAmountDialog = false
                amountToAdd = ""
            },
            onAddAmount = { amount ->
                goalViewModel.updateGoal(
                    goal.copy(currentAmount = (goal.currentAmount + amount).coerceAtMost(goal.targetAmount))
                )
                showAddAmountDialog = false
                amountToAdd = ""
            }
        )
    }
    
    if (showCompletionDialog) {
        Dialog(
            onDismissRequest = {
                showCompletionDialog = false
                // Archive the goal after dialog is dismissed
                goalViewModel.archiveGoal(goal)
            },
            properties = androidx.compose.ui.window.DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Black.copy(alpha = 0.8f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Text(
                                "Поздравляем! 🎉",
                                style = MaterialTheme.typography.titleLarge,
                                color = OnSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Вы достигли цели \"${goal.name}\"!\n\nСвою цель сможете найти в архиве.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurface,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = {
                                    showCompletionDialog = false
                                    // Archive the goal after user clicks "Отлично!"
                                    goalViewModel.archiveGoal(goal)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryGreen,
                                    contentColor = OnPrimary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Отлично!")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddAmountToGoalDialog(
    goal: Goal,
    onDismiss: () -> Unit,
    onAddAmount: (Double) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    
    Dialog(
        onDismissRequest = {
            keyboardController?.hide()
            onDismiss()
        },
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(1.0f)
                .fillMaxHeight(0.36f),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    "Сколько еще готовы отложить?",
                    style = MaterialTheme.typography.titleMedium,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    "Цель: ${goal.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurface.copy(alpha = 0.7f)
                )
                
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { newValue ->
                        val filtered = newValue.replace(",", ".")
                        if (filtered.isEmpty() || filtered.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                            amountText = filtered
                        }
                    },
                    label = { Text("Сумма") },
                    leadingIcon = {
                        Text("₽ ", style = MaterialTheme.typography.titleLarge, color = OnSurface)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = SurfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                
                Text(
                    "Текущая сумма: ${String.format("%.2f", goal.currentAmount)} ₽ из ${String.format("%.2f", goal.targetAmount)} ₽",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurface.copy(alpha = 0.6f)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            keyboardController?.hide()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Отмена", color = OnSurface)
                    }
                    Button(
                        onClick = {
                            val amount = amountText.toDoubleOrNull()
                            if (amount != null && amount > 0) {
                                keyboardController?.hide()
                                onAddAmount(amount)
                            }
                        },
                        enabled = amountText.toDoubleOrNull()?.let { it > 0 } == true,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen,
                            contentColor = OnPrimary
                        )
                    ) {
                        Text("Добавить")
                    }
                }
            }
        }
    }
}

