package com.example.fotoeditor.ui.screens.editimagescreen


import android.net.Uri
import androidx.compose.ui.graphics.ColorMatrix

data class EditImageUiState(
    val selectedToolId: Int = -1,
    val imagePreview: Uri? = null,
    val progress: Float = 0f,
    var isTuneDialogVisible:Boolean = false,
    val editColorMatrix: ColorMatrix? = null
)