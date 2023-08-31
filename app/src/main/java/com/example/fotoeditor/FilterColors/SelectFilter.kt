package com.example.fotoeditor.FilterColors

import androidx.compose.runtime.Composable

@Composable
fun SelectFilter (index: Int): FloatArray {

    val portrait = floatArrayOf(
        1f, 1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f, 0f,
        0f, 0f, 1f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )

    val smooth = floatArrayOf(
        1f, 0f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f, 0f,
        0f, 1f, 1f, 0f, 0f,
        0f, 0f, 0f, 0f, 1f
    )

    val pop = floatArrayOf(
        1f, 1f, 2f, 0f, 0f,
        1f, 1f, 0f, 0f, 0f,
        0f, 1f, 1f, 1f, 1f,
        0f, 0f, 0f, 1f, 1f
    )


    val lighten = floatArrayOf(
        1.5f, 0f, 0f, 0f, 0f,
        0f, 1.5f, 0f, 0f, 0f,
        0f, 0f, 1.5f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )
    val gray = floatArrayOf(
        1f, 0f, 0f, 0f, 0f,
        1f, 0f, 0f, 0f, 0f,
        1f, 0f, 0f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )

    val darken = floatArrayOf(
        .5f, 0f, 0f, 0f, 0f,
        0f, .5f, 0f, 0f, 0f,
        0f, 0f, .5f, 0f, 0f,
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
        4 -> lighten
        5 -> gray
        6 -> darken
        else -> noEffect
    }

}