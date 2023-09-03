package com.example.fotoeditor.ui.screens.homescreen


import android.net.Uri

data class HomeUiState(
    val hasPhotoImported: Boolean = false,
    val shouldExpandLooks: Boolean = hasPhotoImported,
    val shouldExpandTools: Boolean = false,
    val shouldShowExportMenu: Boolean = false,
    val shouldShowOptionsMenu: Boolean = false,
    var importedImageUri: Uri? = null,
    val selectedFilter: Int = 0,
)