package com.example.fotoeditor.FilterColors

import android.graphics.Bitmap
import android.graphics.ColorMatrix
import androidx.compose.ui.graphics.ImageBitmap

class ApplyFilters {
    object ApplyColorFilter{

        fun applyFilters(bitmap: Bitmap, colorMatrix: ColorMatrix): Bitmap{
            val filteredBitmap = Bitmap.createBitmap(
                bitmap.width,
                bitmap.height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = android.graphics.Canvas(filteredBitmap)
            val paint = android.graphics.Paint().apply {
                //colorFilter = ColorFilter.colorMatrix(colorMatrix)
            }

            canvas.drawBitmap(bitmap, 0f, 0f, paint)
            return filteredBitmap
        }
    }
}