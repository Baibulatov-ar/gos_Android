package com.expensetracker.app.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import com.expensetracker.app.ui.theme.*
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit тесты для CategoryUtils
 * 
 * Тестирует:
 * - Получение иконок для категорий (русские и английские названия)
 * - Получение цветов для категорий
 * - Получение отображаемых имен категорий
 * - Нормализацию имен категорий для сравнения
 */
class CategoryUtilsTest {

    @Test
    fun `getCategoryIcon should return correct icon for Russian category names`() {
        assertEquals(Icons.Default.Restaurant, getCategoryIcon("Продукты"))
        assertEquals(Icons.Default.DirectionsCar, getCategoryIcon("Транспорт"))
        assertEquals(Icons.Default.ShoppingBag, getCategoryIcon("Покупки"))
        assertEquals(Icons.Default.Receipt, getCategoryIcon("Счета"))
        assertEquals(Icons.Default.Movie, getCategoryIcon("Развлечения"))
        assertEquals(Icons.Default.LocalHospital, getCategoryIcon("Здоровье"))
        assertEquals(Icons.Default.School, getCategoryIcon("Образование"))
        assertEquals(Icons.Default.CardGiftcard, getCategoryIcon("Подарки"))
        assertEquals(Icons.Default.Home, getCategoryIcon("Дом"))
        assertEquals(Icons.Default.Pets, getCategoryIcon("Питомцы"))
        assertEquals(Icons.Default.Flight, getCategoryIcon("Путешествия"))
        assertEquals(Icons.Default.LocalDining, getCategoryIcon("Рестораны"))
        assertEquals(Icons.Default.MoreHoriz, getCategoryIcon("Другое"))
    }

    @Test
    fun `getCategoryIcon should return correct icon for English category names`() {
        assertEquals(Icons.Default.Restaurant, getCategoryIcon("Food"))
        assertEquals(Icons.Default.DirectionsCar, getCategoryIcon("Transport"))
        assertEquals(Icons.Default.ShoppingBag, getCategoryIcon("Shopping"))
        assertEquals(Icons.Default.Receipt, getCategoryIcon("Bills"))
        assertEquals(Icons.Default.Movie, getCategoryIcon("Entertainment"))
        assertEquals(Icons.Default.LocalHospital, getCategoryIcon("Health"))
        assertEquals(Icons.Default.School, getCategoryIcon("Education"))
        assertEquals(Icons.Default.CardGiftcard, getCategoryIcon("Gifts"))
        assertEquals(Icons.Default.Home, getCategoryIcon("Home"))
        assertEquals(Icons.Default.Pets, getCategoryIcon("Pets"))
        assertEquals(Icons.Default.Flight, getCategoryIcon("Travel"))
        assertEquals(Icons.Default.LocalDining, getCategoryIcon("Cafe"))
        assertEquals(Icons.Default.MoreHoriz, getCategoryIcon("Other"))
    }

    @Test
    fun `getCategoryIcon should be case insensitive`() {
        assertEquals(Icons.Default.Restaurant, getCategoryIcon("ПРОДУКТЫ"))
        assertEquals(Icons.Default.Restaurant, getCategoryIcon("продукты"))
        assertEquals(Icons.Default.Restaurant, getCategoryIcon("FOOD"))
        assertEquals(Icons.Default.Restaurant, getCategoryIcon("food"))
    }

    @Test
    fun `getCategoryIcon should return default icon for unknown category`() {
        assertEquals(Icons.Default.Category, getCategoryIcon("UnknownCategory"))
        assertEquals(Icons.Default.Category, getCategoryIcon("Неизвестная категория"))
    }

    @Test
    fun `getCategoryColor should return correct color for Russian category names`() {
        assertEquals(FoodColor, getCategoryColor("Продукты"))
        assertEquals(TransportColor, getCategoryColor("Транспорт"))
        assertEquals(ShoppingColor, getCategoryColor("Покупки"))
        assertEquals(BillsColor, getCategoryColor("Счета"))
        assertEquals(EntertainmentColor, getCategoryColor("Развлечения"))
        assertEquals(HealthColor, getCategoryColor("Здоровье"))
        assertEquals(EducationColor, getCategoryColor("Образование"))
        assertEquals(GiftsColor, getCategoryColor("Подарки"))
        assertEquals(HomeColor, getCategoryColor("Дом"))
        assertEquals(PetsColor, getCategoryColor("Питомцы"))
        assertEquals(TravelColor, getCategoryColor("Путешествия"))
        assertEquals(CafeColor, getCategoryColor("Рестораны"))
        assertEquals(OtherColor, getCategoryColor("Другое"))
    }

    @Test
    fun `getCategoryColor should return correct color for English category names`() {
        assertEquals(FoodColor, getCategoryColor("Food"))
        assertEquals(TransportColor, getCategoryColor("Transport"))
        assertEquals(ShoppingColor, getCategoryColor("Shopping"))
        assertEquals(BillsColor, getCategoryColor("Bills"))
        assertEquals(EntertainmentColor, getCategoryColor("Entertainment"))
        assertEquals(HealthColor, getCategoryColor("Health"))
        assertEquals(EducationColor, getCategoryColor("Education"))
        assertEquals(GiftsColor, getCategoryColor("Gifts"))
        assertEquals(HomeColor, getCategoryColor("Home"))
        assertEquals(PetsColor, getCategoryColor("Pets"))
        assertEquals(TravelColor, getCategoryColor("Travel"))
        assertEquals(CafeColor, getCategoryColor("Cafe"))
        assertEquals(OtherColor, getCategoryColor("Other"))
    }

    @Test
    fun `getCategoryColor should return default color for unknown category`() {
        assertEquals(PrimaryGreen, getCategoryColor("UnknownCategory"))
    }

    @Test
    fun `getCategoryDisplayName should return Russian name for English input`() {
        assertEquals("Продукты", getCategoryDisplayName("Food"))
        assertEquals("Транспорт", getCategoryDisplayName("Transport"))
        assertEquals("Покупки", getCategoryDisplayName("Shopping"))
        assertEquals("Счета", getCategoryDisplayName("Bills"))
        assertEquals("Развлечения", getCategoryDisplayName("Entertainment"))
        assertEquals("Здоровье", getCategoryDisplayName("Health"))
        assertEquals("Образование", getCategoryDisplayName("Education"))
        assertEquals("Подарки", getCategoryDisplayName("Gifts"))
        assertEquals("Дом", getCategoryDisplayName("Home"))
        assertEquals("Питомцы", getCategoryDisplayName("Pets"))
        assertEquals("Путешествия", getCategoryDisplayName("Travel"))
        assertEquals("Рестораны", getCategoryDisplayName("Cafe"))
        assertEquals("Другое", getCategoryDisplayName("Other"))
    }

    @Test
    fun `getCategoryDisplayName should return original for Russian input`() {
        assertEquals("Продукты", getCategoryDisplayName("Продукты"))
        assertEquals("Транспорт", getCategoryDisplayName("Транспорт"))
    }

    @Test
    fun `getCategoryDisplayName should return original for unknown category`() {
        assertEquals("UnknownCategory", getCategoryDisplayName("UnknownCategory"))
    }

    @Test
    fun `normalizeCategoryName should return set of normalized names`() {
        val foodNames = normalizeCategoryName("Food")
        assertTrue(foodNames.contains("food"))
        assertTrue(foodNames.contains("продукты"))

        val productsNames = normalizeCategoryName("Продукты")
        assertTrue(productsNames.contains("food"))
        assertTrue(productsNames.contains("продукты"))
    }

    @Test
    fun `normalizeCategoryName should handle all categories`() {
        val categories = listOf(
            "Food", "Transport", "Shopping", "Bills", "Entertainment",
            "Health", "Education", "Gifts", "Home", "Pets", "Travel", "Cafe", "Other"
        )

        categories.forEach { category ->
            val normalized = normalizeCategoryName(category)
            assertTrue("Category $category should have normalized names", normalized.isNotEmpty())
        }
    }

    @Test
    fun `normalizeCategoryName should return single item set for unknown category`() {
        val normalized = normalizeCategoryName("UnknownCategory")
        assertEquals(1, normalized.size)
        assertTrue(normalized.contains("unknowncategory"))
    }

    @Test
    fun `getGoalIcon should return correct icon for icon names`() {
        assertEquals(Icons.Default.FlightTakeoff, getGoalIcon("airplane"))
        assertEquals(Icons.Default.Computer, getGoalIcon("laptop"))
        assertEquals(Icons.Default.Verified, getGoalIcon("shield"))
        assertEquals(Icons.Default.Home, getGoalIcon("home"))
        assertEquals(Icons.Default.DirectionsCar, getGoalIcon("car"))
        assertEquals(Icons.Default.School, getGoalIcon("school"))
        assertEquals(Icons.Default.ShoppingBag, getGoalIcon("shopping"))
        assertEquals(Icons.Default.LocalHospital, getGoalIcon("health"))
        assertEquals(Icons.Default.Movie, getGoalIcon("entertainment"))
        assertEquals(Icons.Default.CardGiftcard, getGoalIcon("gift"))
        assertEquals(Icons.Default.Star, getGoalIcon("star"))
        assertEquals(Icons.Default.Flag, getGoalIcon("flag"))
    }

    @Test
    fun `getGoalIcon should return default icon for unknown icon name`() {
        assertEquals(Icons.Default.Flag, getGoalIcon("unknown"))
    }
}

