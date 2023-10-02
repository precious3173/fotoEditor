package com.example.fotoeditor.ui.components

import android.annotation.SuppressLint
import android.content.ContentResolver
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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import com.example.fotoeditor.ui.screens.Settings.ColorMatrixTuneImage.ColorMatrice.createColorMatrix
import com.example.fotoeditor.ui.screens.editimagescreen.EditImageEvent
import com.example.fotoeditor.ui.screens.editimagescreen.EditImageViewModel
import com.example.fotoeditor.ui.screens.editimagescreen.ImageAdjustments
import com.example.fotoeditor.ui.utils.Event
import java.io.InputStream

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TuneImageDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onAdjustmentsChanged: (adjustments: ImageAdjustments) -> Unit,
    onEvent: (Event) -> Unit,
) {
    var offsetY by remember { mutableStateOf(0f) }
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val screenHeight = 500.dp
    val cardHeight = 400.dp
    val cardWidth = 300.dp
    val cardBackgroundAlpha = 0.7f // Adjust transparency as needed

    val cardModifier = Modifier
        .height(cardHeight)
        .width(cardWidth)
        .offset(y = offsetY.dp)
        .pointerInput(Unit) {
            detectVerticalDragGestures { _, dragAmount ->
                offsetY += dragAmount

                // Ensure the card stays within the screen bounds
                offsetY = offsetY.coerceIn(0f, (screenHeight - cardHeight).toPx())
            }
        }

    var brightness by remember { mutableStateOf(0.0f) }
    var contrast by remember { mutableStateOf(0.0f) }
    var ambiance by remember { mutableStateOf(0.0f) }
    var highlights by remember { mutableStateOf(0.0f) }
    var shadows by remember { mutableStateOf(0.0f) }
    var warmth by remember { mutableStateOf(0.0f) }

    val colorMatrix = remember(brightness, contrast, ambiance, highlights, shadows, warmth) {
        createColorMatrix(
            brightness = brightness,
            contrast = contrast,
            ambiance = ambiance,
            highlights = highlights,
            shadows = shadows,
            warmth = warmth
        )
    }



    if (visible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        if (offset.x < 0 || offset.x > 1 || offset.y < 0 || offset.y > 1) {
                            onDismiss()
                        }
                    }
                }
        ) {
            Box (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ){
            Card(
                modifier = cardModifier,
                elevation = 8.dp,
                backgroundColor = Color.White.copy(alpha = cardBackgroundAlpha) // Adjust transparency here
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(
                            enabled = true,
                            state = scrollState
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    SliderValue(
                        label = "Brightness",
                        value = brightness,
                        onValueChange = { newValue -> brightness = newValue }
                    )

                    SliderValue(
                        label = "Contrast",
                        value = contrast,
                        onValueChange = { newValue -> contrast = newValue }
                    )

                    SliderValue(
                        label = "Ambiance",
                        value = ambiance,
                        onValueChange = { newValue -> ambiance = newValue }
                    )

                    SliderValue(
                        label = "Highlights",
                        value = highlights,
                        onValueChange = { newValue -> highlights = newValue }
                    )

                    SliderValue(
                        label = "Shadows",
                        value = shadows,
                        onValueChange = { newValue -> shadows = newValue }
                    )

                    SliderValue(
                        label = "Warmth",
                        value = warmth,
                        onValueChange = { newValue -> warmth = newValue }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val adjustments = ImageAdjustments(
                                brightness = brightness,
                                contrast = contrast,
                                ambiance = ambiance,
                                highlights = highlights,
                                shadows = shadows,
                                warmth = warmth
                            )
                            onAdjustmentsChanged(adjustments)
                            onDismiss()
                        }
                    ) {

                        Text(
                            text = "Apply Changes",
                            modifier = Modifier.clickable {
                                if (colorMatrix == null){
                                    Toast.makeText(context, "It is null", Toast.LENGTH_SHORT).show()
                                    onDismiss()
                                }
                                else{
                                    onEvent(EditImageEvent.UpdateColor(colorMatrix))
                                    onDismiss()
                                }

                            })
                    }
                }
            }
        }
        }
    }
}

@Composable
fun SliderValue(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column {
        Text(text = label, style = MaterialTheme.typography.caption)
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = -1f..1f,
            steps = 100,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = value.toString(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp)
        )
    }
}

fun loadBitmapFromUri(contentResolver: ContentResolver, uri: Uri): ImageBitmap? {
    var inputStream: InputStream? = null
    return try {
        inputStream = contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
    } catch (e: Exception) {
        // Handle any exceptions here
        null
    } finally {
        inputStream?.close()
    }
}

