package com.example.fotoeditor.ui.screens.Settings

import android.content.Context
import android.content.SharedPreferences

class ThemeManager(private val context: Context) {

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val PREF_THEME = "pref_theme"
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
    }

    fun getSelectedTheme(): String {
        return sharedPrefs.getString(PREF_THEME, THEME_LIGHT) ?: THEME_LIGHT
    }

    fun setSelectedTheme(theme: String) {
        sharedPrefs.edit().putString(PREF_THEME, theme).apply()
    }
}