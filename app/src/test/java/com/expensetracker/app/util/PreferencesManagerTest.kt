package com.expensetracker.app.util

import android.content.Context
import android.content.SharedPreferences
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Unit тесты для PreferencesManager
 * 
 * Тестирует:
 * - Проверку первого запуска
 * - Установку флага первого запуска
 * - Проверку завершения онбординга
 * - Установку флага завершения онбординга
 * 
 * Примечание: Это упрощенные тесты, так как PreferencesManager использует
 * реальный SharedPreferences. В реальном проекте можно использовать
 * Robolectric для более полного тестирования Android компонентов.
 */
class PreferencesManagerTest {

    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    @Before
    fun setup() {
        context = mock()
        sharedPreferences = mock()
        editor = mock()

        whenever(context.getSharedPreferences(any(), any())).thenReturn(sharedPreferences)
        whenever(sharedPreferences.edit()).thenReturn(editor)
        whenever(editor.putBoolean(any(), any())).thenReturn(editor)
    }

    @Test
    fun `isFirstLaunch should return true by default`() {
        // Arrange
        whenever(sharedPreferences.getBoolean("first_launch", true)).thenReturn(true)

        // Act
        val result = PreferencesManager.isFirstLaunch(context)

        // Assert
        assertTrue(result)
        verify(sharedPreferences).getBoolean("first_launch", true)
    }

    @Test
    fun `isFirstLaunch should return false after setFirstLaunchComplete`() {
        // Arrange
        whenever(sharedPreferences.getBoolean("first_launch", true)).thenReturn(false)

        // Act
        val result = PreferencesManager.isFirstLaunch(context)

        // Assert
        assertFalse(result)
    }

    @Test
    fun `setFirstLaunchComplete should set first_launch to false`() {
        // Act
        PreferencesManager.setFirstLaunchComplete(context)

        // Assert
        verify(editor).putBoolean("first_launch", false)
        verify(editor).apply()
    }

    @Test
    fun `isOnboardingComplete should return false by default`() {
        // Arrange
        whenever(sharedPreferences.getBoolean("onboarding_complete", false)).thenReturn(false)

        // Act
        val result = PreferencesManager.isOnboardingComplete(context)

        // Assert
        assertFalse(result)
        verify(sharedPreferences).getBoolean("onboarding_complete", false)
    }

    @Test
    fun `isOnboardingComplete should return true after setOnboardingComplete`() {
        // Arrange
        whenever(sharedPreferences.getBoolean("onboarding_complete", false)).thenReturn(true)

        // Act
        val result = PreferencesManager.isOnboardingComplete(context)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `setOnboardingComplete should set onboarding_complete to true`() {
        // Act
        PreferencesManager.setOnboardingComplete(context)

        // Assert
        verify(editor).putBoolean("onboarding_complete", true)
        verify(editor).apply()
    }
}

