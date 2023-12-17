package com.fotoEditor.fotoeditor.ui.screens.Settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Colors
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Shapes
import androidx.compose.material.TextButton
import androidx.compose.material.Typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DialogFormat(
    onDismiss: () -> Unit,
    isDarkTheme: Boolean,
) {
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
        primary = Color.DarkGray,
        primaryVariant = Color.DarkGray,
        secondary = Color.DarkGray,
        secondaryVariant = Color.DarkGray,
        background = Color.DarkGray,
        surface = Color.DarkGray,
        error = Color.DarkGray,
        onPrimary = Color.DarkGray,
        onSecondary = Color.DarkGray,
        onBackground = Color.DarkGray,
        onSurface = Color.DarkGray,
        onError = Color.DarkGray,
        isLight = false
    )

    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colors.primary) {

        MaterialTheme(
            colors = if (isDarkTheme) CustomColorSchemeDark else CustomColorScheme,
            typography = Typography(),
            shapes = Shapes(),
            content = {


                val textColor = if (isDarkTheme) Color.White
                else Color.Black
                var selectedOption = remember { mutableStateOf(0) }
                val backgroundColor = if (isDarkTheme) CustomColorSchemeDark else CustomColorScheme
                val formatManager = FormatManager(LocalContext.current)





                val options = listOf(
                    "JPG 100%",
                    "JPG 95%",
                    "JPG 80%",
                    "PNG"
                )
                AlertDialog(onDismissRequest = { onDismiss() },
                    backgroundColor = backgroundColor.background,
                    title = {
                        Column {


                            Text(
                                text = "Format and quality",
                                fontSize = 20.sp,
                                color = textColor,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            options.forEachIndexed{ index, item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                )
                                {
                                    RadioButton(
                                        selected = index == selectedOption.value,
                                        onClick = {selectedOption.value = index
                                            if (selectedOption.value == index){
                                                formatManager.setSelectedFormat(item)
                                                //   viewModel.onEvent(HomeScreenEvent.ImageSizingUpdate(item))
                                                onDismiss()
                                            }
                                        },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = Color.Blue,
                                            unselectedColor = textColor
                                        ),


                                        )

                                    Text(
                                        text = item,
                                        fontSize = 16.sp,
                                        color = textColor
                                    )
                                }

                            }
                        }
                    },
                    buttons = {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.Absolute.Right
                        ) {
                            TextButton(
                                onClick = { onDismiss() },
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text(text = "Cancel",
                                    color = Color.Blue,
                                    fontWeight = FontWeight.Bold)
                            }
                        }

                    })



            })
    }
}