package com.example.fotoeditor.ui.screens.editimagescreen

import android.net.Uri

data class EditImageUiState(
    val selectedToolId: Int = -1,
    val imagePreview: Uri? = null,
)