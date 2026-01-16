package com.expensetracker.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.expensetracker.app.data.model.Goal
import com.expensetracker.app.ui.component.CustomDatePickerDialog
import com.expensetracker.app.ui.theme.*
import com.expensetracker.app.ui.viewmodel.GoalViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun CreateGoalScreen(
    goalViewModel: GoalViewModel,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    var targetAmount by remember { mutableStateOf("") }
    var goalName by remember { mutableStateOf("") }
    var targetDate by remember { mutableStateOf<Date?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showIconDialog by remember { mutableStateOf(false) }
    var selectedIcon by remember { mutableStateOf("flag") }
    
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    
    val handleDismiss = {
        keyboardController?.hide()
        onDismiss()
    }
    
    val goalIcons = listOf(
        "flag" to Icons.Default.Flag,
        "airplane" to Icons.Default.FlightTakeoff,
        "laptop" to Icons.Default.Computer,
        "shield" to Icons.Default.Verified,
        "home" to Icons.Default.Home,
        "car" to Icons.Default.DirectionsCar,
        "school" to Icons.Default.School,
        "shopping" to Icons.Default.ShoppingBag,
        "health" to Icons.Default.LocalHospital,
        "entertainment" to Icons.Default.Movie,
        "gift" to Icons.Default.CardGiftcard,
        "star" to Icons.Default.Star
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Создать новую цель", color = OnSurface) },
                navigationIcon = {
                    IconButton(onClick = handleDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundDark)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            OutlinedTextField(
                value = targetAmount,
                onValueChange = { newValue ->
                    // Allow only digits
                    if (newValue.isEmpty() || newValue.all { it.isDigit() || it == '.' || it == ',' }) {
                        targetAmount = newValue.replace(",", ".")
                    }
                },
                label = { Text("ЦЕЛЕВАЯ СУММА") },
                leadingIcon = {
                    Text("₽ ", style = MaterialTheme.typography.titleLarge, color = OnSurface)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = OnSurface,
                    unfocusedTextColor = OnSurface,
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = SurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = goalName,
                onValueChange = { goalName = it },
                label = { Text("Название цели") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = OnSurface,
                    unfocusedTextColor = OnSurface,
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = SurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            // Icon Selection
            OutlinedTextField(
                value = goalIcons.find { it.first == selectedIcon }?.let { 
                    "Иконка: ${it.first}" 
                } ?: "",
                onValueChange = { },
                label = { Text("Иконка") },
                leadingIcon = {
                    Icon(
                        imageVector = goalIcons.find { it.first == selectedIcon }?.second ?: Icons.Default.Flag,
                        contentDescription = null,
                        tint = PrimaryGreen
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Icon"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showIconDialog = true },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = OnSurface,
                    unfocusedTextColor = OnSurface,
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = SurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp),
                readOnly = true,
                enabled = false
            )

            OutlinedTextField(
                value = targetDate?.let { dateFormat.format(it) } ?: "",
                onValueChange = { },
                label = { Text("Целевая дата") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Дата"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = OnSurface,
                    unfocusedTextColor = OnSurface,
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = SurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp),
                readOnly = true,
                enabled = false
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val amount = targetAmount.toDoubleOrNull()
                    if (goalName.isNotEmpty() && amount != null && amount > 0) {
                        keyboardController?.hide()
                        goalViewModel.addGoal(
                            Goal(
                                name = goalName.trim(),
                                targetAmount = amount,
                                targetDate = targetDate,
                                category = null,
                                notes = null,
                                icon = selectedIcon
                            )
                        )
                        onSave()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen,
                    contentColor = OnPrimary
                ),
                enabled = goalName.trim().isNotEmpty() && 
                         targetAmount.toDoubleOrNull()?.let { it > 0 } == true,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Сохранить цель", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null
                )
            }
        }
    }
    
    // Custom Date Picker Dialog
    if (showDatePicker) {
        val dateToShow = targetDate ?: Date()
        CustomDatePickerDialog(
            selectedDate = dateToShow,
            onDateSelected = { date ->
                targetDate = date
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
    
    // Icon Selection Dialog
    if (showIconDialog) {
        AlertDialog(
            onDismissRequest = { showIconDialog = false },
            title = {
                Text(
                    "Выберите иконку",
                    style = MaterialTheme.typography.titleLarge,
                    color = OnSurface
                )
            },
            text = {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    items(goalIcons) { (iconName, iconVector) ->
                        IconChipForGoal(
                            iconName = iconName,
                            iconVector = iconVector,
                            isSelected = selectedIcon == iconName,
                            onClick = {
                                selectedIcon = iconName
                                showIconDialog = false
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showIconDialog = false }) {
                    Text("Готово", color = PrimaryGreen)
                }
            },
            containerColor = SurfaceDark,
            titleContentColor = OnSurface,
            textContentColor = OnSurface
        )
    }
}

@Composable
fun IconChipForGoal(
    iconName: String,
    iconVector: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryGreen else SurfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = iconName,
                tint = if (isSelected) Color.White else OnSurface,
                modifier = Modifier.size(32.dp)
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.BottomEnd)
                )
            }
        }
    }
}

