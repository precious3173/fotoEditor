package com.example.fotoeditor.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.fotoeditor.ui.screens.homescreen.HomeScreenEvent
import com.example.fotoeditor.ui.screens.homescreen.HomeScreenViewModel
import com.example.fotoeditor.ui.screens.homescreen.HomeUiState


class AutoTune{
    object AutoTune{

        fun autoTuneImage( brightness: Float, contrast: Float, saturation: Float, homeScreenViewModel: HomeScreenViewModel, imageUri: Uri, bitmap: Bitmap): ColorMatrix {
            // Create a ColorMatrix that combines adjustments for brightness, contrast, and saturation
            // Create a ColorMatrix that combines adjustments for brightness, contrast, and saturation
            var colorMatrix = ColorMatrix()


            val brightnessMatrix = ColorMatrix().apply {
                set(0, 0, brightness)
                set(1, 1, brightness)
                set(2, 2, brightness)
            }

            val contrastMatrix = ColorMatrix().apply {
                set(0, 0, contrast)
                set(1, 1, contrast)
                set(2, 2, contrast)
            }

            val luminance = 0.299f * (1 - saturation)
            val saturationMatrix = ColorMatrix().apply {
                set(0, 0, luminance + saturation)
                set(1, 1, luminance + saturation)
                set(2, 2, luminance + saturation)
            }


            // Combine the matrices
            colorMatrix = autoTuneMatrix(saturationMatrix, autoTuneMatrix(
                contrastMatrix, brightnessMatrix
            ))


            val floatArray = FloatArray(20)

            for (i in 0 until 4) {
                for (j in 0 until 5) {
                    floatArray[i * 5 + j] = colorMatrix[i, j]
                }
            }

            if (floatArray !=null){
                homeScreenViewModel.onEvent(HomeScreenEvent.updateEditColorFilterArray(floatArray, imageUri, bitmap))

            }
            return colorMatrix

            // Combine the matrices

//            // Apply the ColorMatrix to the image using a ColorMatrixColorFilter
//            val paint = Paint().apply {
//                colorFilter = ColorMatrixColorFilter(colorMatrix)
//            }
//
//            // Create a new Bitmap with the adjusted colors
//            val adjustedBitmap = Bitmap.createBitmap(
//                imageBitmap.width,
//                imageBitmap.height,
//                Bitmap.Config.ARGB_8888
//            )
//            val canvas = Canvas(adjustedBitmap)
//            canvas.drawBitmap(imageBitmap.asAndroidBitmap(), 0f, 0f, paint)
//
//            return adjustedBitmap.asImageBitmap()


        }

        private fun autoTuneMatrix(matrixA: ColorMatrix, matrixB: ColorMatrix): ColorMatrix {
            val result = ColorMatrix()
            for (i in 0 until 4) {
                for (j in 0 until 5) {
                    var sum = 0f
                    for (k in 0 until 4) {
                        sum += matrixA[i ,k] * matrixB[k,j]

                    }
                    result[i , j] = sum
                }
            }
            return result
        }

    }
}