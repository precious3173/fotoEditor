package com.example.fotoeditor.ui.screens.Settings

import androidx.compose.material.Colors
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun AppTheme (
    content: @Composable () -> Unit,
    isDarkTheme: Boolean,
){



    val CustomColorScheme = Colors(
        primary = Color.White,
        primaryVariant = Color.White,
        secondary = Color.Cyan,
        secondaryVariant = Color.Cyan,
        background = Color.White,
        surface = Color.White,
        error = Color.Red,
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.Black,
        onSurface = Color.Black,
        onError = Color.White,
        isLight = true
    )

    val CustomColorSchemeDark = Colors(
        primary = Color.Black,
        primaryVariant = Color.Black,
        secondary = Color.Black,
        secondaryVariant = Color.Black,
        background = Color.Black,
        surface = Color.Black,
        error = Color.Black,
        onPrimary = Color.Black,
        onSecondary =Color.Black,
        onBackground = Color.Black,
        onSurface = Color.Black,
        onError = Color.Black,
        isLight = false
    )

    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colors.primary) {

    MaterialTheme(
        colors = if (isDarkTheme) CustomColorSchemeDark else CustomColorScheme,
        typography = Typography(),
        shapes = Shapes(),
        content = content)
}}

