package com.example.fotoeditor.ui.screens.homescreen


import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AlertDialog
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
    val shouldExpandExport: Boolean = false,
    var openDialog: Boolean = false,
    var closeDialog: Boolean = true,
    var  snackDuration: Long = 3000L,
    var editedImageUri: Uri? = null,
    var filterSelected: Boolean = false
)