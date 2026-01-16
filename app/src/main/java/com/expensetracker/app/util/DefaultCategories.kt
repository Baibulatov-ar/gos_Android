package com.expensetracker.app.util

import com.expensetracker.app.data.model.Category
import com.expensetracker.app.ui.theme.*

object DefaultCategories {
    val categories = listOf(
        Category("Продукты", "restaurant", FoodColor.value.toLong(), false, 0),
        Category("Транспорт", "directions_car", TransportColor.value.toLong(), false, 1),
        Category("Покупки", "shopping_bag", ShoppingColor.value.toLong(), false, 2),
        Category("Счета", "receipt", BillsColor.value.toLong(), false, 3),
        Category("Развлечения", "movie", EntertainmentColor.value.toLong(), false, 4),
        Category("Здоровье", "local_hospital", HealthColor.value.toLong(), false, 5),
        Category("Образование", "school", EducationColor.value.toLong(), false, 6),
        Category("Подарки", "card_giftcard", GiftsColor.value.toLong(), false, 7),
        Category("Дом", "home", HomeColor.value.toLong(), false, 8),
        Category("Питомцы", "pets", PetsColor.value.toLong(), false, 9),
        Category("Путешествия", "flight", TravelColor.value.toLong(), false, 10),
        Category("Рестораны", "local_dining", CafeColor.value.toLong(), false, 11),
        Category("Другое", "more_horiz", OtherColor.value.toLong(), false, 12),
    )
}
