package com.expensetracker.app.util

import android.content.Context
import android.content.SharedPreferences

object PreferencesManager {
    private const val PREFS_NAME = "expense_tracker_prefs"
    private const val KEY_FIRST_LAUNCH = "first_launch"
    private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isFirstLaunch(context: Context): Boolean {
        val prefs = getSharedPreferences(context)
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    fun setFirstLaunchComplete(context: Context) {
        val prefs = getSharedPreferences(context)
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }

    fun isOnboardingComplete(context: Context): Boolean {
        val prefs = getSharedPreferences(context)
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false)
    }

    fun setOnboardingComplete(context: Context) {
        val prefs = getSharedPreferences(context)
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETE, true).apply()
    }
}

