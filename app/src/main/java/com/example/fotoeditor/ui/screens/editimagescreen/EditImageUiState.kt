package com.example.fotoeditor.ui.screens.editimagescreen


import android.net.Uri
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ImageBitmap

data class EditImageUiState(
    val selectedToolId: Int = -1,
    val imagePreview: Uri? = null,
    val progress: Float = 0f,
    var isTuneDialogVisible:Boolean = false,
    val editColorMatrix: ColorMatrix? = null,
    var isAutoTuneDialogVisible:Boolean = false,
    var autoTuneBitmap: ImageBitmap?= null
)