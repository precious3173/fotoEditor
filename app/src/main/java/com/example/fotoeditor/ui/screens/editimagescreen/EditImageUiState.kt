package com.example.fotoeditor.ui.screens.editimagescreen

import android.graphics.ColorMatrix
import android.net.Uri

data class EditImageUiState(
    val selectedToolId: Int = -1,
    val imagePreview: Uri? = null,
    val progress: Float = 0f,
    val isTuneDialogVisible:Boolean = false,
    val editColorMatrix: ColorMatrix? = null
)