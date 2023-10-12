package com.example.fotoeditor.ui.screens.Settings
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.ColorMatrix
import com.example.fotoeditor.ui.screens.homescreen.HomeScreenEvent
import com.example.fotoeditor.ui.screens.homescreen.HomeScreenViewModel

class ColorMatrixTuneImage {

    object colorMatrix {

        fun createColorMatrix(
            brightness: Float,
            contrast: Float,
            ambiance: Float,
            highlights: Float,
            shadows: Float,
            warmth: Float,
            homeScreenViewModel: HomeScreenViewModel,
            imageUri: Uri,
            bitmap: Bitmap
        ): ColorMatrix {
            var colorMatrix = ColorMatrix()

            val scale = 1f + brightness
            // Adjust brightness and contrast
            val brightnessContrastMatrix = ColorMatrix(
               values = floatArrayOf(
                   scale, 0f, 0f, 0f, 0f,
                   0f, scale, 0f, 0f, 0f,
                   0f, 0f, scale, 0f, 0f,
                   0f, 0f, 0f, 1f, 0f
               )
            )


                // Adjust contrast
                val translate = (-0.5f * contrast) + 0.5f
                  val contrast = ColorMatrix(
                      floatArrayOf(
                        1f, 0f, 0f, 0f, translate,
                        0f, 1f, 0f, 0f, translate,
                        0f, 0f, 1f, 0f, translate,
                        0f, 0f, 0f, 1f, 0f
                    ))




            // Apply ambiance

                val ambianceScale = 1f + ambiance
                val ambianceMatrix = ColorMatrix(floatArrayOf(
                    ambianceScale, 0f, 0f, 0f, 0f,
                    0f,  ambianceScale, 0f, 0f, 0f,
                    0f, 0f,  ambianceScale, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                ))



                // Adjust highlights (positive value increases highlights)
                val highlightsScale = 1f + highlights
                // Adjust shadows (positive value increases shadows)
                val shadowsScale = 1f + shadows
                val highlightsShadowsMatrix = ColorMatrix(
                    floatArrayOf(
                    highlightsScale, 0f, 0f, 0f, 0f,
                    0f, highlightsScale, 0f, 0f, 0f,
                    0f, 0f, highlightsScale, 0f, 0f,
                    0f, 0f, 0f, shadowsScale, 0f
                ))


            // Apply warmth

                // Warmth adjustment by changing RGB channels
                val rScale = 1f + warmth
                val gScale = 1f - 0.2f * warmth
                val bScale = 1f - warmth
               val warmthMatrix = ColorMatrix(floatArrayOf(
                    rScale, 0f, 0f, 0f, 0f,
                    0f, gScale, 0f, 0f, 0f,
                    0f, 0f, bScale, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                ))

            colorMatrix = multiplyColorMatrices(
                    warmthMatrix,
                    multiplyColorMatrices(
                        highlightsShadowsMatrix,
                        multiplyColorMatrices(ambianceMatrix,
                            multiplyColorMatrices(
                                contrast, brightnessContrastMatrix
                            )
                        )
                    )
            )

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
        }

        fun multiplyColorMatrices(matrix1: ColorMatrix, matrix2: ColorMatrix): ColorMatrix {
            val resultMatrix = ColorMatrix()

            for (row in 0 until 4) {
                for (col in 0 until 5) {
                    var sum = 0f
                    for (i in 0 until 4) {
                        sum += matrix1[row, i] * matrix2[i, col]
                    }
                    resultMatrix[row, col] = sum
                }
            }

            return resultMatrix
        }
    }
}
