package com.example.fotoeditor.ui.screens.homescreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.material3.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fotoeditor.domain.EditImageRepository
import com.example.fotoeditor.domain.models.ImageFilterState
import com.example.fotoeditor.ui.utils.Event
import com.example.fotoeditor.ui.utils.EventHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: EditImageRepository,
) :
    ViewModel(), EventHandler {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()
    override fun onEvent(event: Event) {
        when (event) {
            is HomeScreenEvent.ImportImage -> onImportImage(event.callback)
            is HomeScreenEvent.AccessStorage -> onAccessStorage(event.access)
            is HomeScreenEvent.LoadImageUri -> onLoadImageUri(event.uri)
            is HomeScreenEvent.ShowImageInfo -> onShowImageInfo(event.navigate)
            is HomeScreenEvent.ToggleLooks -> onToggleLooks()
            is HomeScreenEvent.ToggleTools -> onToggleTools()
            is HomeScreenEvent.ToggleExport -> onToggleExport()
            is HomeScreenEvent.OnExportImage -> onShowExportMenu()
            is HomeScreenEvent.HideExportMenu -> onHideExportMenu()
            is HomeScreenEvent.OpenOptionsMenu -> onOpenOptionsMenu()
            is HomeScreenEvent.HideOptionsMenu -> onHideOptionsMenu()
            is HomeScreenEvent.UpdateFilter -> updateFilter(event.index)
            is HomeScreenEvent.SelectTool -> onSelectTool(event.id)
            is HomeScreenEvent.UpdateFilterOnImage -> updateFilterOnImage(event.bitmap)
            is HomeScreenEvent.LoadImageFilters -> onLoadImageFilters(event.imageBitmap)
            is HomeScreenEvent.SaveFilteredImage -> onSaveFilteredImage()
            is HomeScreenEvent.OnOpenDialog -> onOpenDialog()
        }
    }

    private fun onLoadImageFilters(imageBitmap: Bitmap?) {
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                _uiState.value.imageFilterState?.let { state ->
                    _uiState.update { it.copy(imageFilterState = state.copy(isLoading = true)) }
                }
                val image = getPreviewImage(imageBitmap)
                image?.let { repository.loadImageFilters(it) }
            }.onSuccess { filters ->
                filters?.let { notNullFilters ->
                    _uiState.value.imageFilterState?.let { state ->
                        _uiState.update { it.copy(imageFilterState = state.copy(imageFilters = notNullFilters)) }
                    }
                }
            }.onFailure { throwable ->
                _uiState.value.imageFilterState?.let { state ->
                    _uiState.update { it.copy(imageFilterState = state.copy(error = throwable.message.toString())) }
                }
            }
        }
    }

    private fun onSaveFilteredImage() {
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                _uiState.value.imageFilterState?.let { state ->
                    _uiState.update { it.copy(imageFilterState = state.copy(isLoading = true)) }
                }
                _uiState.value.filteredImageBitmap?.let {
                    repository.saveFilteredImage(it)
                }
            }.onSuccess {
                _uiState.value.imageFilterState?.let { state ->
                    _uiState.update { it.copy(imageFilterState = state.copy(isLoading = false)) }
                }
            }
                .onFailure { throwable ->
                    _uiState.value.imageFilterState?.let { state ->
                        _uiState.update {
                            it.copy(
                                imageFilterState = state.copy(
                                    isLoading = false,
                                    error = throwable.message.toString()
                                )
                            )
                        }
                    }
                }
        }
    }

    private fun updateFilterOnImage(newBitmap: Bitmap?) {
        _uiState.update { it.copy(filteredImageBitmap = newBitmap) }
    }

    private fun onImportImage(callback: (Boolean) -> Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.READ_EXTERNAL_STORAGE,
            ) -> callback(true)

            else -> callback(false)
        }
    }

    private fun onAccessStorage (accessStorage: (Boolean) -> Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            ) -> accessStorage(true)

            else -> accessStorage(false)
        }
    }

    private fun onLoadImageUri(uri: Uri) {
        kotlin.runCatching {
            _uiState.update {
                it.copy(
                    hasPhotoImported = true,
                    importedImageUri = uri,
                    shouldExpandLooks = true,
                    imageFilterState = ImageFilterState(),
                )
            }
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
                    selectedToolId = -1,
                )
            }
        }
    }

    private fun onOpenDialog(){
        _uiState.update {
            it.copy(
                openDialog = !uiState.value.openDialog
            )
        }
    }

    private fun onToggleExport() {
        if (_uiState.value.hasPhotoImported) {
            _uiState.update {
                it.copy(
                    shouldExpandLooks = false,
                    shouldExpandExport = !uiState.value.shouldExpandExport,
                )
            }
        }
    }

    private fun onShowImageInfo(navigate: () -> Unit) {
        navigate()
    }

    private fun onSelectTool(id: Int) {
        _uiState.update { it.copy(selectedToolId = id) }
    }

    private fun getPreviewImage(originalImage: Bitmap?): Bitmap? {
        return kotlin.runCatching {
            val previewWidth = checkNotNull(originalImage?.width)
            val previewHeight = checkNotNull(originalImage?.height)
            originalImage?.let {
                Bitmap.createScaledBitmap(originalImage, previewWidth, previewHeight, false)
            }
        }.getOrDefault(originalImage)
    }
}


sealed interface HomeScreenEvent : Event {
    data class ImportImage(
        val callback: (Boolean) -> Unit,
    ) : HomeScreenEvent

     data class AccessStorage(
         val access : (Boolean) -> Unit,
     ): HomeScreenEvent
    data class LoadImageUri(val uri: Uri) : HomeScreenEvent
    data class ShowImageInfo(val navigate: () -> Unit) : HomeScreenEvent
    object OpenEditMenu : HomeScreenEvent
    object OpenOptionsMenu : HomeScreenEvent
    object HideOptionsMenu : HomeScreenEvent
    object ToggleLooks : HomeScreenEvent
    object ToggleTools : HomeScreenEvent
    object ToggleExport : HomeScreenEvent

    object OnOpenDialog: HomeScreenEvent
    object OnExportImage : HomeScreenEvent
    object HideExportMenu : HomeScreenEvent
    data class UpdateFilter(val index: Int) : HomeScreenEvent
    data class SelectTool(val id: Int) : HomeScreenEvent
    data class UpdateFilterOnImage(val bitmap: Bitmap?) : HomeScreenEvent
    data class LoadImageFilters(val imageBitmap: Bitmap?) : HomeScreenEvent
    object SaveFilteredImage: HomeScreenEvent
}