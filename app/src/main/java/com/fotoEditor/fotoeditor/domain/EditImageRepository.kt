package com.fotoEditor.fotoeditor.domain

import android.graphics.Bitmap
import com.fotoEditor.fotoeditor.domain.models.ImageFilter

interface EditImageRepository {
    fun loadImageFilters(image: Bitmap): List<ImageFilter>
    suspend fun saveFilteredImage(filteredBitmap: Bitmap): String?

}