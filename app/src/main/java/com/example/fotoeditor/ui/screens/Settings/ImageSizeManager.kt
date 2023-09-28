package com.example.fotoeditor.ui.screens.Settings

import android.content.Context
import android.content.SharedPreferences

class ImageSizeManager(private val context: Context) {


    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("image_size", Context.MODE_PRIVATE)

    companion object {
        private const val PREF_SIZE = "key"
    }

    fun getSelectedImageSize(): String {
        val selectedImageSize = sharedPrefs.getString(PREF_SIZE, "Do not resize")
        return selectedImageSize!!
    }

    fun setSelectedImageSize(selectedSize: String) {
        sharedPrefs.edit().putString(PREF_SIZE, selectedSize).apply()
    }
}