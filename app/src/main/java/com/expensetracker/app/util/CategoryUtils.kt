package com.expensetracker.app.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.expensetracker.app.ui.theme.*

fun getCategoryIcon(category: String): ImageVector {
    return when (category.lowercase()) {
        "food", "продукты" -> Icons.Default.Restaurant
        "transport", "транспорт" -> Icons.Default.DirectionsCar
        "shopping", "покупки" -> Icons.Default.ShoppingBag
        "bills", "счета" -> Icons.Default.Receipt
        "entertainment", "развлечения" -> Icons.Default.Movie
        "health", "здоровье" -> Icons.Default.LocalHospital
        "education", "образование" -> Icons.Default.School
        "gifts", "подарки" -> Icons.Default.CardGiftcard
        "home", "дом" -> Icons.Default.Home
        "pets", "питомцы" -> Icons.Default.Pets
        "travel", "путешествия" -> Icons.Default.Flight
        "cafe", "рестораны", "кафе и рестораны" -> Icons.Default.LocalDining
        "other", "другое" -> Icons.Default.MoreHoriz
        else -> Icons.Default.Category
    }
}

fun getGoalIcon(iconName: String): ImageVector {
    return when (iconName) {
        "airplane" -> Icons.Default.FlightTakeoff
        "laptop" -> Icons.Default.Computer
        "shield" -> Icons.Default.Verified
        "home" -> Icons.Default.Home
        "car" -> Icons.Default.DirectionsCar
        "school" -> Icons.Default.School
        "shopping" -> Icons.Default.ShoppingBag
        "health" -> Icons.Default.LocalHospital
        "entertainment" -> Icons.Default.Movie
        "gift" -> Icons.Default.CardGiftcard
        "star" -> Icons.Default.Star
        "flag" -> Icons.Default.Flag
        else -> Icons.Default.Flag
    }
}

fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "food", "продукты" -> FoodColor
        "transport", "транспорт" -> TransportColor
        "shopping", "покупки" -> ShoppingColor
        "bills", "счета" -> BillsColor
        "entertainment", "развлечения" -> EntertainmentColor
        "health", "здоровье" -> HealthColor
        "education", "образование" -> EducationColor
        "gifts", "подарки" -> GiftsColor
        "home", "дом" -> HomeColor
        "pets", "питомцы" -> PetsColor
        "travel", "путешествия" -> TravelColor
        "cafe", "рестораны", "кафе и рестораны" -> CafeColor
        "other", "другое" -> OtherColor
        else -> PrimaryGreen
    }
}

fun getCategoryDisplayName(category: String): String {
    return when (category.lowercase()) {
        "food" -> "Продукты"
        "transport" -> "Транспорт"
        "shopping" -> "Покупки"
        "bills" -> "Счета"
        "entertainment" -> "Развлечения"
        "health" -> "Здоровье"
        "education" -> "Образование"
        "gifts" -> "Подарки"
        "home" -> "Дом"
        "pets" -> "Питомцы"
        "travel" -> "Путешествия"
        "cafe", "кафе и рестораны" -> "Рестораны"
        "other" -> "Другое"
        else -> category
    }
}

// Convert Russian category name to English (for database matching)
fun getCategoryEnglishName(russianName: String): String {
    return when (russianName.lowercase()) {
        "продукты" -> "Food"
        "транспорт" -> "Transport"
        "покупки" -> "Shopping"
        "счета" -> "Bills"
        "развлечения" -> "Entertainment"
        "здоровье" -> "Health"
        "образование" -> "Education"
        "подарки" -> "Gifts"
        "дом" -> "Home"
        "питомцы" -> "Pets"
        "путешествия" -> "Travel"
        "рестораны" -> "Cafe"
        "другое" -> "Other"
        else -> russianName
    }
}

// Normalize category name for comparison (handles both Russian and English)
fun normalizeCategoryName(category: String): Set<String> {
    val lower = category.lowercase()
    return when (lower) {
        "food", "продукты" -> setOf("food", "продукты")
        "transport", "транспорт" -> setOf("transport", "транспорт")
        "shopping", "покупки" -> setOf("shopping", "покупки")
        "bills", "счета" -> setOf("bills", "счета")
        "entertainment", "развлечения" -> setOf("entertainment", "развлечения")
        "health", "здоровье" -> setOf("health", "здоровье")
        "education", "образование" -> setOf("education", "образование")
        "gifts", "подарки" -> setOf("gifts", "подарки")
        "home", "дом" -> setOf("home", "дом")
        "pets", "питомцы" -> setOf("pets", "питомцы")
        "travel", "путешествия" -> setOf("travel", "путешествия")
        "cafe", "рестораны", "кафе и рестораны" -> setOf("cafe", "рестораны", "кафе и рестораны")
        "other", "другое" -> setOf("other", "другое")
        else -> setOf(category.lowercase())
    }
}
