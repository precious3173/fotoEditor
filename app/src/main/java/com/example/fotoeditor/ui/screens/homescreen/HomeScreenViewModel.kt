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
            is HomeScreenEvent.ShowImageInfo -> onShowImageInfo(event.navigate)
            is HomeScreenEvent.ToggleLooks -> onToggleLooks()
            is HomeScreenEvent.ToggleTools -> onToggleTools()
            is HomeScreenEvent.OnExportImage -> onShowExportMenu()
            is HomeScreenEvent.HideExportMenu -> onHideExportMenu()
            is HomeScreenEvent.OpenOptionsMenu -> onOpenOptionsMenu()
            is HomeScreenEvent.HideOptionsMenu -> onHideOptionsMenu()
            is HomeScreenEvent.UpdateFilter -> updateFilter(event.index)
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
                shouldExpandLooks = true,
            )
        }
    }

    private fun updateFilter(index: Int) {
        _uiState.update {
            it.copy(
               selectedFilter = index,
            )
        }
    }

    private fun onOpenOptionsMenu() {
        _uiState.update { it.copy(shouldShowOptionsMenu = true) }
    }

    private fun onHideOptionsMenu() {
        _uiState.update { it.copy(shouldShowOptionsMenu = false) }
    }

    private fun onShowExportMenu() {
        if (_uiState.value.hasPhotoImported) {
            _uiState.update { it.copy(shouldShowExportMenu = true) }
        }
    }

    private fun onHideExportMenu() {
        if (_uiState.value.hasPhotoImported) {
            _uiState.update { it.copy(shouldShowExportMenu = false) }
        }
    }

    private fun onToggleLooks() {
        if (_uiState.value.hasPhotoImported) {
            _uiState.update {
                it.copy(
                    shouldExpandTools = false,
                    shouldExpandLooks = !uiState.value.shouldExpandLooks,
                )
            }
        }
    }

    private fun onToggleTools() {
        if (_uiState.value.hasPhotoImported) {
            _uiState.update {
                it.copy(
                    shouldExpandLooks = false,
                    shouldExpandTools = !_uiState.value.shouldExpandTools,
                )
            }
        }
    }

    private fun onShowImageInfo(navigate: () -> Unit) {
        navigate()
    }

}


sealed interface HomeScreenEvent : Event {
    data class ImportImage(
        val callback: (Boolean) -> Unit,
    ) : HomeScreenEvent

    data class LoadImageUri(val uri: Uri) : HomeScreenEvent
    data class ShowImageInfo(val navigate: () -> Unit) : HomeScreenEvent
    object OpenEditMenu : HomeScreenEvent
    object OpenOptionsMenu : HomeScreenEvent
    object HideOptionsMenu : HomeScreenEvent
    object ToggleLooks : HomeScreenEvent
    object ToggleTools : HomeScreenEvent
    object OnExportImage : HomeScreenEvent
    object HideExportMenu : HomeScreenEvent
    data class UpdateFilter(val index: Int) : HomeScreenEvent

}