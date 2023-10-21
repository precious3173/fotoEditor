package com.example.fotoeditor.ui.components

import android.content.res.Configuration
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.Dp

class getScreenDimension {
    object ScreenDimension{

        fun getScreenSmallestDimensionDp(configuration: Configuration, density: Float): Dp {
            val screenWidthDp = Dp(configuration.screenWidthDp.toFloat() / density)
            val screenHeightDp = Dp(configuration.screenHeightDp.toFloat() / density)
            return minOf(screenWidthDp, screenHeightDp)
        }

        fun getScreenMaxDimensionDp(imageBitmap: ImageBitmap, density: Float): Dp {
//            val screenWidthDp = Dp(configuration.screenWidthDp.toFloat() / density)
//            val screenHeightDp = Dp(configuration.screenHeightDp.toFloat() / density)
            val imageWidthDp = Dp(imageBitmap.width.toFloat() / density)
            val imageHeightDp = Dp(imageBitmap.height.toFloat() / density)
            return maxOf(imageWidthDp, imageHeightDp)
        }
    }
}