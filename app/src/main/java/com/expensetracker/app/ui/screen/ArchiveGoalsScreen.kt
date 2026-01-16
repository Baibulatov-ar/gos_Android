package com.expensetracker.app.ui.screen

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.expensetracker.app.data.model.Goal
import com.expensetracker.app.ui.theme.*
import com.expensetracker.app.ui.viewmodel.GoalViewModel
import com.expensetracker.app.util.getGoalIcon
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveGoalsScreen(
    goalViewModel: GoalViewModel,
    onBackClick: () -> Unit
) {
    val archivedGoals by goalViewModel.getArchivedGoals().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Архив целей", color = OnSurface) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = OnSurface
                        )
                    }
                },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (archivedGoals.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Archive,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = OnSurface.copy(alpha = 0.4f)
                            )
                            Text(
                                text = "Архив пуст",
                                style = MaterialTheme.typography.titleMedium,
                                color = OnSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "Выполненные цели будут отображаться здесь",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurface.copy(alpha = 0.5f),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            } else {
                items(
                    items = archivedGoals,
                    key = { goal -> goal.id }
                ) { goal ->
                    SwipeableGoalItem(
                        goal = goal,
                        onDelete = {
                            goalViewModel.deleteGoal(goal)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableGoalItem(
    goal: Goal,
    onDelete: () -> Unit
) {
    val dismissState = rememberDismissState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == DismissValue.DismissedToStart) {
                onDelete()
                true
            } else {
                false
            }
        },
        positionalThreshold = { it * .25f }
    )

    // Reset dismiss state when goal changes (to prevent state from persisting between items)
    LaunchedEffect(goal.id) {
        if (dismissState.currentValue != DismissValue.Default) {
            dismissState.reset()
        }
    }

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        background = {
            val color = Color(0xFFE53935) // Red color
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
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
            ArchivedGoalCard(goal = goal)
        }
    )
}

@Composable
fun ArchivedGoalCard(goal: Goal) {
    val progress = ((goal.currentAmount / goal.targetAmount).toFloat()).coerceIn(0f, 1f)
    val dateFormat = SimpleDateFormat("d MMM", Locale("ru"))
    val dateFormatFull = SimpleDateFormat("d MMM yyyy", Locale("ru"))
    
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
            .clip(RoundedCornerShape(16.dp)),
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
                        tint = PrimaryGreen,
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
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Выполнено",
                    tint = PrimaryGreen,
                    modifier = Modifier.size(24.dp)
                )
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
                color = PrimaryGreen,
                trackColor = SurfaceVariant
            )

            Text(
                text = "Цель достигнута! 🎉",
                style = MaterialTheme.typography.bodySmall,
                color = PrimaryGreen,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

