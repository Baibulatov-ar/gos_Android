package com.expensetracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.expensetracker.app.data.database.AppDatabase
import com.expensetracker.app.data.model.Expense
import com.expensetracker.app.data.model.Goal
import com.expensetracker.app.data.repository.*
import com.expensetracker.app.ui.screen.*
import com.expensetracker.app.ui.theme.ExpenseTrackerTheme
import com.expensetracker.app.ui.viewmodel.*
import com.expensetracker.app.util.DefaultCategories
import com.expensetracker.app.util.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)
        
        // Initialize default categories
        CoroutineScope(Dispatchers.IO).launch {
            val categoryDao = database.categoryDao()
            DefaultCategories.categories.forEach { category ->
                categoryDao.insertCategory(category)
            }
        }

        setContent {
            ExpenseTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ExpenseTrackerApp(database)
                }
            }
        }
    }
}

@Composable
fun ExpenseTrackerApp(database: AppDatabase) {
    val navController = rememberNavController()
    val context = androidx.compose.ui.platform.LocalContext.current

    val expenseRepository = remember { ExpenseRepository(database.expenseDao()) }
    val categoryRepository = remember { CategoryRepository(database.categoryDao()) }
    val goalRepository = remember { GoalRepository(database.goalDao()) }
    val dailyLimitRepository = remember { DailyLimitRepository(database.dailyLimitDao()) }

    val expenseViewModel: ExpenseViewModel = viewModel {
        ExpenseViewModel(expenseRepository)
    }
    val categoryViewModel: CategoryViewModel = viewModel {
        CategoryViewModel(categoryRepository)
    }
    val goalViewModel: GoalViewModel = viewModel {
        GoalViewModel(goalRepository)
    }
    val dailyLimitViewModel: DailyLimitViewModel = viewModel {
        DailyLimitViewModel(dailyLimitRepository)
    }

    var showCreateGoal by remember { mutableStateOf(false) }
    var showEditDailyLimit by remember { mutableStateOf(false) }
    var selectedDateForExpense by remember { mutableStateOf<Date?>(null) }
    var selectedDate by remember { mutableStateOf<Date?>(null) }

    val dailyLimit by dailyLimitViewModel.dailyLimit.collectAsState()
    
    // Check if first launch
    val isFirstLaunch = remember { PreferencesManager.isFirstLaunch(context) }
    val isOnboardingComplete = remember { PreferencesManager.isOnboardingComplete(context) }
    
    // Show daily limit screen on first launch
    LaunchedEffect(isFirstLaunch, isOnboardingComplete) {
        if (isFirstLaunch && !isOnboardingComplete) {
            showEditDailyLimit = true
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in listOf("home", "analytics", "goals")) {
                BottomNavigationBar(
                    currentRoute = currentRoute ?: "home",
                    onNavigate = { route ->
                        when (route) {
                            "home" -> navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                            "analytics" -> navController.navigate("analytics") {
                                popUpTo("home")
                            }
                            "goals" -> navController.navigate("goals") {
                                popUpTo("home")
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                HomeScreen(
                    expenseViewModel = expenseViewModel,
                    dailyLimitViewModel = dailyLimitViewModel,
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it },
                    onAddExpenseClick = { 
                        val today = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.time
                        selectedDateForExpense = selectedDate ?: today
                        navController.navigate("add_expense")
                    },
                    onSeeAllClick = { navController.navigate("history") },
                    onCategoryClick = { category ->
                        navController.navigate("history?category=$category")
                    }
                )
            }

            composable("history") {
                HistoryScreen(
                    expenseViewModel = expenseViewModel,
                    onBackClick = { navController.popBackStack() },
                    onExpenseClick = { expense ->
                        // TODO: Navigate to expense details
                    }
                )
            }

            composable("analytics") {
                AnalyticsScreen(
                    expenseViewModel = expenseViewModel
                )
            }

            composable("goals") {
                GoalsScreen(
                    dailyLimitViewModel = dailyLimitViewModel,
                    goalViewModel = goalViewModel,
                    onCreateGoalClick = { showCreateGoal = true },
                    onEditDailyLimitClick = { showEditDailyLimit = true },
                    onArchiveClick = { navController.navigate("archive_goals") }
                )
            }
            
            composable("archive_goals") {
                ArchiveGoalsScreen(
                    goalViewModel = goalViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
            
            composable("add_expense") {
                AddExpenseScreen(
                    categoryViewModel = categoryViewModel,
                    initialDate = selectedDateForExpense ?: Date(),
                    onDismiss = { 
                        navController.popBackStack()
                        selectedDateForExpense = null
                    },
                    onAddExpense = { expense ->
                        expenseViewModel.addExpense(expense)
                        navController.popBackStack()
                        selectedDateForExpense = null
                    }
                )
            }
        }
    }

    // Create Goal Modal
    if (showCreateGoal) {
        CreateGoalScreen(
            goalViewModel = goalViewModel,
            onDismiss = { showCreateGoal = false },
            onSave = { showCreateGoal = false }
        )
    }
    
    // Edit Daily Limit Modal
    if (showEditDailyLimit) {
        EditDailyLimitScreen(
            dailyLimitViewModel = dailyLimitViewModel,
            currentLimit = dailyLimit,
            onDismiss = { 
                showEditDailyLimit = false
                // Mark onboarding as complete when user dismisses (even if they didn't change)
                if (isFirstLaunch && !isOnboardingComplete) {
                    PreferencesManager.setFirstLaunchComplete(context)
                    PreferencesManager.setOnboardingComplete(context)
                }
            },
            onSave = { 
                showEditDailyLimit = false
                // Mark onboarding as complete when user saves
                if (isFirstLaunch && !isOnboardingComplete) {
                    PreferencesManager.setFirstLaunchComplete(context)
                    PreferencesManager.setOnboardingComplete(context)
                }
            }
        )
    }
}

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = com.expensetracker.app.ui.theme.SurfaceDark
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Главная") },
            selected = currentRoute == "home",
            onClick = { onNavigate("home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = com.expensetracker.app.ui.theme.PrimaryGreen,
                selectedTextColor = com.expensetracker.app.ui.theme.PrimaryGreen,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = "Analytics"
                )
            },
            label = { Text("Аналитика") },
            selected = currentRoute == "analytics",
            onClick = { onNavigate("analytics") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = com.expensetracker.app.ui.theme.PrimaryGreen,
                selectedTextColor = com.expensetracker.app.ui.theme.PrimaryGreen,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = "Goals"
                )
            },
            label = { Text("Цели") },
            selected = currentRoute == "goals",
            onClick = { onNavigate("goals") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = com.expensetracker.app.ui.theme.PrimaryGreen,
                selectedTextColor = com.expensetracker.app.ui.theme.PrimaryGreen,
                indicatorColor = Color.Transparent
            )
        )
    }
}

