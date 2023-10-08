package com.example.fotoeditor.ui.screens.homescreen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri

@Suppress("UNREACHABLE_CODE")
class ImageLookSelector() {

    object ImageLook{

        fun applyFilter(originalBitmap: Bitmap?,filters: List<FloatArray>, context: Context, uri: Uri): Bitmap? {

            val filteredBitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri)).copy(Bitmap.Config.ARGB_8888, true)


            filteredBitmap.let {
                filters.forEach { filter ->


                    val canvas = Canvas(filteredBitmap)

                    val paint = Paint().apply {
                        colorFilter = ColorMatrixColorFilter(ColorMatrix(filter))
                    }
                    canvas.drawBitmap(it, 0f, 0f, paint)


            }



            return filteredBitmap
        }
       // return null
    }



    }

}
