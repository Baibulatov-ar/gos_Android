package com.expensetracker.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expensetracker.app.ui.theme.*
import com.expensetracker.app.ui.viewmodel.DailyLimitViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun EditDailyLimitScreen(
    dailyLimitViewModel: DailyLimitViewModel,
    currentLimit: Double,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    var limitText by remember { mutableStateOf(String.format("%.0f", currentLimit)) }
    
    val limitValue = limitText.toDoubleOrNull() ?: 0.0
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    
    val handleDismiss = {
        keyboardController?.hide()
        onDismiss()
    }

    val context = LocalContext.current
    val isFirstLaunch = remember { 
        com.expensetracker.app.util.PreferencesManager.isFirstLaunch(context)
    }
    val isOnboardingComplete = remember { 
        com.expensetracker.app.util.PreferencesManager.isOnboardingComplete(context)
    }
    val showOnboarding = isFirstLaunch && !isOnboardingComplete

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (showOnboarding) "Установите дневной лимит" else "Изменить дневной лимит", 
                        color = OnSurface
                    ) 
                },
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
            // Info section
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
                        text = "ДНЕВНОЙ ЛИМИТ РАСХОДОВ",
                        style = MaterialTheme.typography.labelLarge,
                        color = OnSurface.copy(alpha = 0.7f)
                    )

                    Column {
                        Text(
                            text = "Сумма лимита",
                            style = MaterialTheme.typography.labelMedium,
                            color = OnSurface.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = limitText,
                            onValueChange = { newValue ->
                                // Allow only digits
                                if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                    limitText = newValue
                                }
                            },
                            label = { },
                            leadingIcon = {
                                Text("₽", style = MaterialTheme.typography.titleLarge, color = OnSurface)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 80.dp)
                                .focusRequester(focusRequester),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Start
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = OnSurface,
                                unfocusedTextColor = OnSurface,
                                focusedBorderColor = PrimaryGreen,
                                unfocusedBorderColor = SurfaceVariant,
                                focusedContainerColor = SurfaceDark,
                                unfocusedContainerColor = SurfaceDark
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            maxLines = 1
                        )
                    }

                    // Quick amount buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("500", "1000", "2000").forEach { amount ->
                            FilterChip(
                                selected = limitText == amount,
                                onClick = { limitText = amount },
                                label = { Text("$amount ₽") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Additional quick amounts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("5000", "10000", "15000").forEach { amount ->
                            FilterChip(
                                selected = limitText == amount,
                                onClick = { limitText = amount },
                                label = { Text("$amount ₽") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Info card
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
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = PrimaryGreen
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (showOnboarding) {
                            "Установите дневной лимит расходов, чтобы контролировать свой бюджет и не превышать его."
                        } else {
                            "Лимит поможет контролировать ежедневные расходы и не превышать бюджет."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryGreen
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (limitValue > 0) {
                        keyboardController?.hide()
                        dailyLimitViewModel.updateDailyLimit(limitValue)
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
                enabled = limitValue > 0,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    if (showOnboarding) "Установить лимит" else "Сохранить", 
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null
                )
            }
        }
    }
}

