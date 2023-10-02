package com.example.fotoeditor.ui.screens.Settings

import android.graphics.ColorMatrix


class ColorMatrixTuneImage {

    object ColorMatrice{

        fun createColorMatrix(
            brightness: Float,
            contrast: Float,
            ambiance: Float,
            highlights: Float,
            shadows: Float,
            warmth: Float
        ): ColorMatrix {
            val colorMatrix = ColorMatrix()

            // Adjust brightness and contrast
            val brightnessContrastMatrix = ColorMatrix().apply {
                val scale = 1f + brightness
                set(floatArrayOf(
                    scale, 0f, 0f, 0f, 0f,
                    0f, scale, 0f, 0f, 0f,
                    0f, 0f, scale, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                ))

                // Adjust contrast
                val translate = (-0.5f * contrast) + 0.5f
                postConcat(ColorMatrix().apply {
                    set(floatArrayOf(
                        1f, 0f, 0f, 0f, translate,
                        0f, 1f, 0f, 0f, translate,
                        0f, 0f, 1f, 0f, translate,
                        0f, 0f, 0f, 1f, 0f
                    ))
                })
            }

            // Apply ambiance
            val ambianceMatrix = ColorMatrix().apply {
                val scale = 1f + ambiance
                set(floatArrayOf(
                    scale, 0f, 0f, 0f, 0f,
                    0f, scale, 0f, 0f, 0f,
                    0f, 0f, scale, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                ))
            }

            // Apply highlights and shadows
            val highlightsShadowsMatrix = ColorMatrix().apply {
                // Adjust highlights (positive value increases highlights)
                val highlightsScale = 1f + highlights
                // Adjust shadows (positive value increases shadows)
                val shadowsScale = 1f + shadows
                set(floatArrayOf(
                    highlightsScale, 0f, 0f, 0f, 0f,
                    0f, highlightsScale, 0f, 0f, 0f,
                    0f, 0f, highlightsScale, 0f, 0f,
                    0f, 0f, 0f, shadowsScale, 0f
                ))
            }

            // Apply warmth
            val warmthMatrix = ColorMatrix().apply {
                // Warmth adjustment by changing RGB channels
                val rScale = 1f + warmth
                val gScale = 1f - 0.2f * warmth
                val bScale = 1f - warmth
                set(floatArrayOf(
                    rScale, 0f, 0f, 0f, 0f,
                    0f, gScale, 0f, 0f, 0f,
                    0f, 0f, bScale, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                ))
            }

            // Combine the matrices
            colorMatrix.postConcat(brightnessContrastMatrix)
            colorMatrix.postConcat(ambianceMatrix)
            colorMatrix.postConcat(highlightsShadowsMatrix)
            colorMatrix.postConcat(warmthMatrix)

            return colorMatrix
        }
    }
}