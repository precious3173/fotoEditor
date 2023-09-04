package com.example.fotoeditor.domain

import android.graphics.Bitmap
import com.example.fotoeditor.domain.models.ImageFilter

interface EditImageRepository {
    fun loadImageFilters(image: Bitmap): List<ImageFilter>
    suspend fun saveFilteredImage(filteredBitmap: Bitmap): String?

}