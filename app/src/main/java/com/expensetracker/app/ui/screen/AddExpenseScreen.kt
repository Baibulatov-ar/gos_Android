package com.expensetracker.app.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.ExperimentalComposeUiApi
import com.expensetracker.app.data.model.Category
import com.expensetracker.app.data.model.Expense
import com.expensetracker.app.ui.theme.*
import com.expensetracker.app.ui.viewmodel.CategoryViewModel
import com.expensetracker.app.util.DefaultCategories
import com.expensetracker.app.util.getCategoryIcon
import com.expensetracker.app.util.getCategoryDisplayName
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AddExpenseScreen(
    categoryViewModel: CategoryViewModel,
    initialDate: Date = Date(),
    onDismiss: () -> Unit,
    onAddExpense: (Expense) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var note by remember { mutableStateOf("") }
    var showAllCategories by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    
    val handleDismiss = {
        keyboardController?.hide()
        onDismiss()
    }

    LaunchedEffect(selectedCategory) {
        if (selectedCategory != null) {
            listState.animateScrollToItem(0)
        }
    }

    val categories by categoryViewModel.categories.collectAsState()
    val rawCategories = if (categories.isEmpty()) DefaultCategories.categories else categories
    val displayCategories = remember(rawCategories, selectedCategory) {
        val uniqueCategories = rawCategories.distinctBy { getCategoryDisplayName(it.name) }
        if (selectedCategory != null) {
            val selected = uniqueCategories.find { getCategoryDisplayName(it.name) == getCategoryDisplayName(selectedCategory!!.name) }
            if (selected != null) {
                listOf(selected) + (uniqueCategories - selected)
            } else {
                uniqueCategories
            }
        } else {
            uniqueCategories
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        TopAppBar(
            title = { Text("Добавить расход", color = OnSurface) },
            navigationIcon = {
                IconButton(onClick = handleDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Закрыть",
                        tint = OnSurface
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = BackgroundDark
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(bottom = 80.dp), // Отступ от навигации
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Amount Input Field
            OutlinedTextField(
                value = amount,
                onValueChange = { newValue ->
                    // Разрешаем только цифры, точку и запятую (для русской локали)
                    val filtered = newValue.replace(",", ".")
                    if (filtered.matches(Regex("^\\d*\\.?\\d{0,2}$")) || filtered.isEmpty()) {
                        amount = filtered
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                label = { Text("Сумма", color = OnSurface.copy(alpha = 0.7f)) },
                trailingIcon = {
                    Text(
                        text = "₽",
                        style = MaterialTheme.typography.titleLarge,
                        color = OnSurface,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                },
                textStyle = MaterialTheme.typography.displayMedium.copy(
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 48.sp
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryGreen,
                    unfocusedTextColor = PrimaryGreen,
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = SurfaceVariant,
                    focusedLabelColor = OnSurface.copy(alpha = 0.7f),
                    unfocusedLabelColor = OnSurface.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Note Input
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "На что потратили?",
                        color = OnSurface.copy(alpha = 0.5f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = OnSurface.copy(alpha = 0.7f)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = OnSurface,
                    unfocusedTextColor = OnSurface,
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = SurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Category Selection
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "КАТЕГОРИИ",
                        style = MaterialTheme.typography.labelLarge,
                        color = OnSurface.copy(alpha = 0.7f)
                    )
                    TextButton(onClick = { showAllCategories = true }) {
                    Text(
                        text = "Ещё",
                        color = PrimaryGreen
                    )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    state = listState,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(displayCategories) { category ->
                        CategoryChip(
                            category = category,
                            isSelected = selectedCategory?.name == category.name,
                            onClick = { selectedCategory = category }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            
            // Add Expense Button
            Button(
                onClick = {
                    if (selectedCategory != null && amount.toDoubleOrNull() != null && amount.toDouble() > 0) {
                        keyboardController?.hide()
                        // If selected date is today, use current time. Otherwise use the selected date (00:00)
                        val now = Calendar.getInstance()
                        val selectedCal = Calendar.getInstance().apply { time = initialDate }
                        
                        val isToday = now.get(Calendar.YEAR) == selectedCal.get(Calendar.YEAR) &&
                                     now.get(Calendar.DAY_OF_YEAR) == selectedCal.get(Calendar.DAY_OF_YEAR)
                        
                        val finalDate = if (isToday) Date() else initialDate

                        onAddExpense(
                            Expense(
                                amount = amount.toDouble(),
                                category = selectedCategory!!.name,
                                note = note.ifEmpty { null },
                                date = finalDate
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen,
                    contentColor = OnPrimary
                ),
                enabled = selectedCategory != null && amount.toDoubleOrNull() != null && amount.toDouble() > 0,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Добавить расход",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }

    if (showAllCategories) {
        AlertDialog(
            onDismissRequest = { showAllCategories = false },
            title = {
                Text(
                    text = "Выберите категорию",
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
                        items(displayCategories) { category ->
                            CategoryChip(
                                category = category,
                                isSelected = selectedCategory?.name == category.name,
                                onClick = {
                                    selectedCategory = category
                                    showAllCategories = false
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAllCategories = false }) {
                    Text("Закрыть", color = PrimaryGreen)
                }
            },
            containerColor = SurfaceDark,
            titleContentColor = OnSurface,
            textContentColor = OnSurface
        )
    }
}

@Composable
fun CategoryChip(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val categoryColor = Color(category.color.toULong())
    
    // When selected: Solid category color background, White content
    // When not selected: SurfaceVariant background, colored icon/text or white/grey
    val backgroundColor = if (isSelected) categoryColor else SurfaceVariant
    val contentColor = if (isSelected) Color.White else OnSurface
    
    // Optional: Add border or scale effect if needed, but user asked for "not reducing size"
    // Bold text when selected

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp)
    ) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = getCategoryIcon(category.name),
            contentDescription = category.name,
            tint = contentColor,
            modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = getCategoryDisplayName(category.name),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) categoryColor else OnSurface, // Keep text colored if selected to match theme, or white? 
            // User said "in app colors". If the bubble is solid color, maybe text below should also be colored?
            // Actually, if background is solid color, the icon inside is white. The text is outside.
            // Let's make text colored when selected.
            maxLines = 1,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}


