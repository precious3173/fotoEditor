package com.example.fotoeditor.FilterColors

import androidx.compose.runtime.Composable

@Composable
fun SelectFilter (index: Int): FloatArray {

    val portrait = floatArrayOf(
        1.1f, 0f, 0f, 0f, 0.02f,
        0f, 1.1f, 0f, 0f, 0.02f,
        0f, 0f, 1.1f, 0f, 0.02f,
        0f, 0f, 0f, 1.1f, 0f
    )

    val smooth = floatArrayOf(
        1f, 0f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f, 0f,
        0f, 0f, 1f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )

    val pop = floatArrayOf(
        1f, 0f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f, 0f,
        0f, 0f, 1f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )

    val accentuate = floatArrayOf(
        1.2f, 0f, 0f, 0f, 0f,
        0f, 1.2f, 0f, 0f, 0f,
        0f, 0f, 1.2f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )

    val fadedGlow = floatArrayOf(
        1.2f, 0f, 0f, 0f, 0f,
        0f, 1.2f, 0f, 0f, 0f,
        0f, 0f, 0.8f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )

    val morning = floatArrayOf(
        1.2f, 0f, 0f, 0f, 25f,
        0f, 1.2f, 0f, 0f, 25f,
        0f, 0f, 1.2f, 0f, 25f,
        0f, 0f, 0f, 1f, 0f
    )

    val bright = floatArrayOf(
        1.5f, 0f, 0f, 0f, 0f,
        0f, 1.5f, 0f, 0f, 0f,
        0f, 0f, 1.5f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )
    val fineArt = floatArrayOf(
        0.33f, 0.33f, 0.33f, 0f, 0f,
        0.33f, 0.33f, 0.33f, 0f, 0f,
        0.33f, 0.33f, 0.33f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )


    val push = floatArrayOf(
        0.4f, 0.4f, 0.4f, 0f, 0f,       // Red channel (grayscale with a dark tint)
        0.4f, 0.4f, 0.4f, 0f, 0f,       // Green channel (grayscale with a dark tint)
        0.4f, 0.4f, 0.4f, 0f, 0f,       // Blue channel (grayscale with a dark tint)
        0f, 0f, 0f, 1f, 0f
    )

    val structure = floatArrayOf(
        0.3f, 0.3f, 0.3f, 0f, 0f,    // Red channel (grayscale)
        0.3f, 0.3f, 0.3f, 0f, 0f,    // Green channel (grayscale)
        0.3f, 0.3f, 0.3f, 0f, 0f,    // Blue channel (grayscale)
        0f, 0f, 0f, 1f, 0f
    )
    val silhouette = floatArrayOf(
        0.7f, 0.7f, 0.7f, 0f, -50f,    // Red channel (reduced brightness with dark background)
        0.7f, 0.7f, 0.7f, 0f, -50f,    // Green channel (reduced brightness with dark background)
        0.7f, 0.7f, 0.7f, 0f, -50f,    // Blue channel (reduced brightness with dark background)
        0f, 0f, 0f, 1f, 0f
    )

    val noEffect = floatArrayOf(
        1f, 0f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f, 0f,
        0f, 0f, 1f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )

    return when (index) {
        1 -> portrait
        2 -> smooth
        3 -> pop
        4 -> accentuate
        5 -> fadedGlow
        6 -> morning
        7-> bright
        8 -> fineArt
        9 -> push
        10 -> structure
        11 -> silhouette
        else -> noEffect
    }

}