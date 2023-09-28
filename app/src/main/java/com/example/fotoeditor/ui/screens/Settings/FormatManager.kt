package com.example.fotoeditor.ui.screens.Settings

import android.content.Context
import android.content.SharedPreferences

class FormatManager (private val context: Context) {

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("format", Context.MODE_PRIVATE)

    companion object {
        private const val PREF_FORMAT = "key"
    }

    fun getSelectedFormat(): String {
        val selectedImageSize = sharedPrefs.getString(PREF_FORMAT, "JPG 100%")
        return selectedImageSize!!
    }

    fun setSelectedFormat(selectedSize: String) {
        sharedPrefs.edit().putString(PREF_FORMAT, selectedSize).apply()
    }

}