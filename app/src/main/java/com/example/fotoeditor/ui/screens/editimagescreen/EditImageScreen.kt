package com.example.fotoeditor.ui.screens.editimagescreen

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fotoeditor.ui.components.EditImageBottomBar
import com.example.fotoeditor.ui.screens.homescreen.HomeScreenViewModel
import com.example.fotoeditor.ui.utils.PhotoEditProgressIndicator
import com.example.fotoeditor.ui.utils.toBitmap
import kotlinx.coroutines.delay

@Composable
fun EditImageRoute(
    toolId: String?,
    homeScreenViewModel: HomeScreenViewModel,
    editImageViewModel: EditImageViewModel
) {
    val TAG = "EditImage"
    val uiState by editImageViewModel.uiState.collectAsStateWithLifecycle()
    val homeScreenUiState by homeScreenViewModel.uiState.collectAsStateWithLifecycle()
    val animatedProgress by animateFloatAsState(targetValue = uiState.progress, label = "AnimatedProgress")

    Log.e(TAG, "EditImageRoute: todoid is $toolId")
    toolId?.let {
        LaunchedEffect(Unit) {
            editImageViewModel.onEvent(EditImageEvent.UpdateToolId(it.toInt()))
            editImageViewModel.onEvent(EditImageEvent.UpdateImagePreview(homeScreenUiState.importedImageUri))
        }
    }

    val isIncreasing = remember { mutableStateOf(true) }

    LaunchedEffect(uiState.progress, isIncreasing.value) {
        if (isIncreasing.value) {
            if (uiState.progress == 1.0f) {
                isIncreasing.value = false
            }
            editImageViewModel.onEvent(EditImageEvent.UpdateProgress(uiState.progress.plus(0.01f)))
            delay(1000)
        }

        if(!isIncreasing.value) {
            if (uiState.progress == -1.0f) {
                isIncreasing.value = true
            }
            editImageViewModel.onEvent(EditImageEvent.UpdateProgress(uiState.progress.minus(0.01f)))
            delay(1000)
        }
    }

    if(isIncreasing.value) {
        LaunchedEffect(uiState.progress, isIncreasing.value) {
            if (uiState.progress >= 1.0f) {
                isIncreasing.value = false
                editImageViewModel.onEvent(EditImageEvent.UpdateProgress(uiState.progress.minus(0.08f)))
            }
            editImageViewModel.onEvent(EditImageEvent.UpdateProgress(uiState.progress.plus(0.08f)))
            delay(1000)
        }
    }

    if(!isIncreasing.value) {
        LaunchedEffect(uiState.progress, isIncreasing.value) {
            if (uiState.progress <= -1.0f) {
                isIncreasing.value = true
                editImageViewModel.onEvent(EditImageEvent.UpdateProgress(uiState.progress.plus(0.08f)))
            }
            editImageViewModel.onEvent(EditImageEvent.UpdateProgress(uiState.progress.minus(0.08f)))
            delay(1000)
        }
    }

    Scaffold(
        topBar = { PhotoEditProgressIndicator(progress = animatedProgress) },
        content = {
            EditImageScreen(
                imageUri = uiState.imagePreview,
                modifier = Modifier.padding(it),
            )
        },
        bottomBar = {
            Box(
                Modifier
                    .background(Color.White)
                    .shadow(elevation = 1.dp)
            ) {
                EditImageBottomBar(
                    save = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.Gray,
                            )
                        }
                    },
                    abort = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = Color.Gray,
                            )
                        }
                    },
                    content = {}
                )
            }
        }
    )
}

@Composable
private fun EditImageScreen(
    imageUri: Uri?,
    modifier: Modifier = Modifier,
) {
    imageUri?.let {
        EditImageContent(
            modifier = modifier,
            bitmap = it.toBitmap(LocalContext.current),
        )
    }
}

@Composable
private fun EditImageContent(
    bitmap: Bitmap?,
    modifier: Modifier = Modifier,
) {
    bitmap?.let {
        Box(
            Modifier
                .fillMaxSize()
                .then(modifier)
        ) {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.FillBounds,
            )
        }
    }
}