package com.example.fotoeditor.ui.screens.editimagescreen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.compose.ui.res.painterResource

class SaveRotateBitmap {

    object SaveRotateBitmap{

         fun rotateBitmap(context: Context, rotationAngle: Float, bitmap: Bitmap): Bitmap {
            val matrix = Matrix().apply { postRotate(rotationAngle) }
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
    }
}