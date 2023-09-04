package com.example.fotoeditor.ui.screens.homescreen


import android.graphics.Bitmap
import android.net.Uri
import com.example.fotoeditor.domain.models.ImageFilterState

data class HomeUiState(
    val hasPhotoImported: Boolean = false,
    val shouldExpandLooks: Boolean = hasPhotoImported,
    val shouldExpandTools: Boolean = false,
    val shouldShowExportMenu: Boolean = false,
    val shouldShowOptionsMenu: Boolean = false,
    var importedImageUri: Uri? = null,
    val selectedFilter: Int = 0,
    val selectedToolId: Int = -1,
    val imageFilterState: ImageFilterState? = null,
    val filteredImageBitmap: Bitmap? = null,
)