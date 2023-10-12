package com.example.fotoeditor.ui.components

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SliderColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.fotoeditor.ui.screens.Settings.ColorMatrixTuneImage.colorMatrix.createColorMatrix
import com.example.fotoeditor.ui.screens.editimagescreen.EditImageEvent
import com.example.fotoeditor.ui.screens.editimagescreen.EditImageViewModel
import com.example.fotoeditor.ui.screens.editimagescreen.ImageAdjustments
import com.example.fotoeditor.ui.screens.homescreen.HomeScreenViewModel
import com.example.fotoeditor.ui.utils.Event
import java.io.InputStream

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun  TuneImageDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onAdjustmentsChanged: (adjustments: ImageAdjustments) -> Unit,
    onEvent: (Event) -> Unit,
    homeScreenViewModel: HomeScreenViewModel,
    imageUri: Uri,
    bitmap: Bitmap
) {
    var offsetY by remember { mutableStateOf(0f) }
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val screenHeight = 300.dp
    val cardHeight = 250.dp
    val cardWidth = 300.dp
    val cardBackgroundAlpha = 0.7f // Adjust transparency as needed
    var slidersChanged by remember { mutableStateOf(false) }

    val cardModifier = Modifier
        .height(cardHeight)
        .width(cardWidth)
        .offset(y = offsetY.dp)
//        .pointerInput(Unit) {
//            detectVerticalDragGestures { _, dragAmount ->
//                offsetY += dragAmount
//
//                // Ensure the card stays within the screen bounds
//                offsetY = offsetY.coerceIn(0f, (screenHeight - cardHeight).toPx())
//            }
//        }

    var brightness by remember { mutableStateOf(0.0f) }
    var contrast by remember { mutableStateOf(0.0f) }
    var ambiance by remember { mutableStateOf(0.0f) }
    var highlights by remember { mutableStateOf(0.0f) }
    var shadows by remember { mutableStateOf(0.0f) }
    var warmth by remember { mutableStateOf(0.0f) }




    if (visible) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
          properties = DialogProperties(dismissOnClickOutside = true),
            modifier = Modifier.fillMaxSize(),
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                if (offset.x < 0 || offset.x > 1 || offset.y < 0 || offset.y > 1) {
                                    onDismiss()
                                }
                            }
                        },
                    contentAlignment = Alignment.BottomCenter
                ) {

                    Card(
                        modifier = cardModifier,
                        elevation = 2.dp,
                        backgroundColor = Color.Transparent // Adjust transparency here
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(
                                    enabled = true,
                                    state = scrollState
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            SliderValue(
                                label = "Brightness",
                                value = brightness,
                                onValueChange = { newValue -> brightness = newValue
                                    slidersChanged = true
                                                },
                            )

                            SliderValue(
                                label = "Contrast",
                                value = contrast,
                                onValueChange = { newValue -> contrast = newValue
                                    slidersChanged = true
                                }

                            )

                            SliderValue(
                                label = "Ambiance",
                                value = ambiance,
                                onValueChange = { newValue -> ambiance = newValue
                                    slidersChanged = true
                                }
                            )

                            SliderValue(
                                label = "Highlights",
                                value = highlights,
                                onValueChange = { newValue -> highlights = newValue
                                    slidersChanged = true
                                }
                            )

                            SliderValue(
                                label = "Shadows",
                                value = shadows,
                                onValueChange = { newValue -> shadows = newValue
                                    slidersChanged = true
                                }
                            )

                            SliderValue(
                                label = "Warmth",
                                value = warmth,
                                onValueChange = { newValue -> warmth = newValue
                                    slidersChanged = true
                                }
                            )


                        }

                    }
                }
            }

        )
        if (slidersChanged){
        LaunchedEffect(brightness, contrast, ambiance, highlights, shadows, warmth) {
            val adjustments = ImageAdjustments(
                brightness = brightness,
                contrast = contrast,
                ambiance = ambiance,
                highlights = highlights,
                shadows = shadows,
                warmth = warmth
            )
            val colorMatrix = createColorMatrix(
                brightness = brightness,
                contrast = contrast,
                ambiance = ambiance,
                highlights = highlights,
                shadows = shadows,
                warmth = warmth,
                homeScreenViewModel = homeScreenViewModel,
                imageUri = imageUri,
                bitmap = bitmap
            )
            onAdjustmentsChanged(adjustments)
            if (colorMatrix != null) {
                onEvent(EditImageEvent.UpdateColor(colorMatrix))
            }
        }
        }
    }
}





@Composable
fun SliderValue(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box{
            Text(text = label, style = MaterialTheme.typography.caption.copy(color = Color.White))
        }
      Box { Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = -1f..1f,
            steps = 100,
            colors = SliderDefaults.colors(
                activeTrackColor = Color.White,
                thumbColor = Color.Transparent,
                activeTickColor = Color.Transparent,
                disabledActiveTickColor = Color.Transparent
            )

        )
      }

//
//        Text(
//            text = value.toInt().toString(),
//            color = Color.White
//        )
    }
}


