package com.example.fotoeditor.ui.screens.homescreen

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.ui.graphics.Canvas
import android.graphics.Paint
import android.media.MediaScannerConnection
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fotoeditor.domain.EditImageRepository
import com.example.fotoeditor.domain.models.ImageFilterState
import com.example.fotoeditor.ui.utils.Event
import com.example.fotoeditor.ui.utils.EventHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
            is HomeScreenEvent.OnOpenDialog -> onAlertDialog()
            is HomeScreenEvent.OnCloseDialog -> onCloseDialog()
            is HomeScreenEvent.LoadEditedImageUri -> onLoadEditedImageUri(event.uri)
            is HomeScreenEvent.FilterSelected  -> onFilterSelected()
            is HomeScreenEvent.FilterUnSelected  -> onFilterUnSelected()
            is HomeScreenEvent.FilterSelectedForUse -> onFilterSelectedForUSe(event.uri, event.bitmap, event.colorFilter)
            is HomeScreenEvent.SendEditedUri -> onSendEditedUri()


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

    @SuppressLint("SuspiciousIndentation", "Recycle")
    private fun onFilterSelectedForUSe (uriImport: Uri?, bitmap: Bitmap?, colorFilterArray: FloatArray) {
        if (_uiState.value.shouldSendEditedImageUri) {

            val filteredBitmap = bitmap!!.copy(Bitmap.Config.ARGB_8888, true)
            val canvas = android.graphics.Canvas(filteredBitmap)
            val paint = Paint()

            val colorMatrix = android.graphics.ColorMatrix(colorFilterArray)

            paint.colorFilter = android.graphics.ColorMatrixColorFilter(colorMatrix)

            canvas.drawBitmap(filteredBitmap, 0f, 0f, paint)


            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val outputFileName = "filtered_image_$timeStamp.jpg"

            val storageDirectory = File(Environment.getExternalStorageDirectory(), "fotoeditor")
            storageDirectory.mkdirs() // Create the directory if it doesn't exist

            val outputFilePath = File(storageDirectory, outputFileName).absolutePath


            try {
                val outputStream = FileOutputStream(outputFilePath)
                filteredBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.close()

                val imageFile = File(outputFilePath)
               // val uri = Uri.fromFile(imageFile)
                val uri = FileProvider.getUriForFile(
                    context, "com.example.fotoEditor.provider",
                    imageFile
                )


                if (uri != null) {

              MediaScannerConnection.scanFile(
                  context,
                  arrayOf(outputFilePath),
                  arrayOf("image/jpeg")
              ) { _, uri2 ->
                  if (uri2 != null) {
                      // The image has been successfully scanned
                      Toast.makeText(
                          context,
                          "Image saved and scanned successfully",
                          Toast.LENGTH_SHORT
                      ).show()
                  } else {
                      // Failed to scan the image
                      Toast.makeText(
                          context,
                          "Failed to scan the image",
                          Toast.LENGTH_SHORT
                      ).show()
                  }
              }

                    _uiState.update { it.copy(filterSelectedForUSe = uri) }
                    _uiState.update {
                        it.copy(filterSelected = false)
                    }
                    Toast.makeText(context, "uri is not empty", Toast.LENGTH_SHORT).show()
                } else {
                    _uiState.update { it.copy(filterSelectedForUSe = uriImport) }
                    Toast.makeText(context, "uri is empty", Toast.LENGTH_SHORT).show()
                }


            } catch (e: Exception) {
                Toast.makeText(context, "Error saving the image.", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }


    private fun updateFilterOnImage(newBitmap: Bitmap?) {
        _uiState.update { it.copy(filteredImageBitmap = newBitmap) }
    }

    private fun onImportImage(callback: (Boolean) -> Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_EXTERNAL_STORAGE,
            ) -> callback(true)

            else -> callback(false)
        }
    }

    private fun onAccessStorage (accessStorage: (Boolean) -> Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_EXTERNAL_STORAGE,
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

    private fun onLoadEditedImageUri(uri: Uri) {
        kotlin.runCatching {
            _uiState.update {
                it.copy(
                    editedImageUri = uri
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

    private fun onAlertDialog(){

        if (_uiState.value.hasPhotoImported){

                    _uiState.value.openDialog = !_uiState.value.openDialog
                    CoroutineScope(Dispatchers.Default).launch {
                        delay(_uiState.value.snackDuration)
                        _uiState.value.openDialog = !_uiState.value.openDialog
                    }


            }
        }



    private fun onCloseDialog(){

        if (_uiState.value.openDialog){

            _uiState.update {
                it.copy(
                    closeDialog = _uiState.value.openDialog
                )

            }
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

    private fun onFilterSelected(){
            _uiState.update {
                it.copy(
                    filterSelected = true
                )
            }
    }

    private fun onFilterUnSelected(){
        _uiState.update {
            it.copy(
                filterSelected = false
            )
        }
    }
    private fun  onSendEditedUri(){
        _uiState.update {
            it.copy(
                shouldSendEditedImageUri = true

            )
        }
        CoroutineScope(Dispatchers.IO).launch {

            delay(1000L)
            _uiState.update {
                it.copy(
                    shouldSendEditedImageUri = false

                )
            }
        }
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
    data class LoadEditedImageUri(val uri: Uri) : HomeScreenEvent
    data class ShowImageInfo(val navigate: () -> Unit) : HomeScreenEvent
    object OpenEditMenu : HomeScreenEvent
    object OpenOptionsMenu : HomeScreenEvent
    object HideOptionsMenu : HomeScreenEvent
    object ToggleLooks : HomeScreenEvent
    object ToggleTools : HomeScreenEvent
    object ToggleExport : HomeScreenEvent
    object OnExportImage : HomeScreenEvent
    object HideExportMenu : HomeScreenEvent
    object OnOpenDialog : HomeScreenEvent

    object  OnCloseDialog : HomeScreenEvent
    data class UpdateFilter(val index: Int) : HomeScreenEvent
    data class SelectTool(val id: Int) : HomeScreenEvent
    data class UpdateFilterOnImage(val bitmap: Bitmap?) : HomeScreenEvent
    data class LoadImageFilters(val imageBitmap: Bitmap?) : HomeScreenEvent

    data class FilterSelectedForUse(val uri: Uri?, val bitmap: Bitmap, var colorFilter: FloatArray) : HomeScreenEvent
    object SaveFilteredImage: HomeScreenEvent

    object FilterSelected:  HomeScreenEvent
    object FilterUnSelected:  HomeScreenEvent

    object SendEditedUri: HomeScreenEvent

}