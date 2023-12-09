package com.example.fotoeditor.ui.screens.editimagescreen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import com.example.fotoeditor.ui.utils.Event

class SaveRotateBitmap {

    object SaveRotateBitmap{

         fun rotateBitmap(context: Context, rotationAngle: Float, bitmap: Bitmap, uiState: (Event) -> Unit): Bitmap {
            val matrix = Matrix().apply { postRotate(rotationAngle) }
            val bitmaps = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

             return bitmaps
        }
    }
}