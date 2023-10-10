package com.example.fotoeditor.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap


class AutoTune{
    object AutoTune{

        fun autoTuneImage(imageBitmap: ImageBitmap, brightness: Float, contrast: Float, saturation: Float): ImageBitmap {
            // Create a ColorMatrix that combines adjustments for brightness, contrast, and saturation
            val colorMatrix = ColorMatrix().apply {
                setScale(brightness, brightness, brightness, 1f)
                postConcat(ColorMatrix(floatArrayOf(
                    contrast, 0f, 0f, 0f, 0f,
                    0f, contrast, 0f, 0f, 0f,
                    0f, 0f, contrast, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )))
                val luminance = 0.299f * (1 - saturation)
                postConcat(ColorMatrix(floatArrayOf(
                    luminance + saturation, luminance, luminance, 0f, 0f,
                    luminance, luminance + saturation, luminance, 0f, 0f,
                    luminance, luminance, luminance + saturation, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )))
            }

            // Apply the ColorMatrix to the image using a ColorMatrixColorFilter
            val paint = Paint().apply {
                colorFilter = ColorMatrixColorFilter(colorMatrix)
            }

            // Create a new Bitmap with the adjusted colors
            val adjustedBitmap = Bitmap.createBitmap(
                imageBitmap.width,
                imageBitmap.height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(adjustedBitmap)
            canvas.drawBitmap(imageBitmap.asAndroidBitmap(), 0f, 0f, paint)

            return adjustedBitmap.asImageBitmap()
        }

    }
}