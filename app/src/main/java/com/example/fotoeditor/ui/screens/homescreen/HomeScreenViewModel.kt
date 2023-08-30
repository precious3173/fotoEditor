package com.example.fotoeditor.ui.screens.homescreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.fotoeditor.ui.utils.Event
import com.example.fotoeditor.ui.utils.EventHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeScreenViewModel(@SuppressLint("StaticFieldLeak") private val context: Context) :
    ViewModel(), EventHandler {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()
    override fun onEvent(event: Event) {
        when (event) {
            is HomeScreenEvent.ImportImage -> onImportImage(event.callback)
            is HomeScreenEvent.LoadImageUri -> onLoadImageUri(event.uri)
        }
    }

    private fun onImportImage(callback: (Boolean) -> Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.READ_EXTERNAL_STORAGE,
            ) -> callback(true)

            else -> callback(false)
        }
    }

    private fun onLoadImageUri(uri: Uri) {
        _uiState.update {
            it.copy(
                hasPhotoImported = true,
                importedImageUri = uri,
            )
        }
    }

}


sealed interface HomeScreenEvent : Event {
    data class ImportImage(
        val callback: (Boolean) -> Unit,
    ) : HomeScreenEvent

    data class LoadImageUri(val uri: Uri) : HomeScreenEvent
    object ShowImageInfo : HomeScreenEvent
    object OpenEditMenu : HomeScreenEvent
    object OpenOptionsMenu : HomeScreenEvent
    object ToggleLooks : HomeScreenEvent
    object ToggleTools : HomeScreenEvent
    object OnExportImage : HomeScreenEvent

}