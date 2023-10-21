package com.example.fotoeditor.ui.screens.editimagescreen

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import com.example.fotoeditor.ui.screens.homescreen.HomeScreenEvent
import com.example.fotoeditor.ui.utils.Event
import com.example.fotoeditor.ui.utils.EventHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class EditImageViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel(), EventHandler {
    private val _uiState = MutableStateFlow(EditImageUiState())
    val uiState = _uiState.asStateFlow()

    override fun onEvent(event: Event) {
        when (event) {
            is EditImageEvent.UpdateToolId -> onUpdateToolId(event.id)
            is EditImageEvent.UpdateImagePreview -> onUpdateImagePreview(event.uri)
            is EditImageEvent.UpdateProgress -> onUpdateProgress(event.progress)
            is EditImageEvent.UpdateTune -> onUpdateTune(event.boolean)
            is EditImageEvent.UpdateAutoTune -> onUpdateAutoTune(event.boolean)
            is EditImageEvent.UpdateColor -> onUpdateColor(event.colorMatrix)
            is EditImageEvent.UpdateAutoTuneBitmap -> onAutoTuneBitmap(event.imageBitmap)
            is EditImageEvent.shouldShowCropOptions -> shouldShowCropOption(event.boolean)
            is EditImageEvent.ShouldSendCropped -> shouldSendCroppedBitmap(event.boolean)
            is EditImageEvent.FreeCropActivated -> freeCropActivated(event.boolean)
            is EditImageEvent.SendCroppedImage -> sendCroppedImage(event.croppedImage)
        }
    }

    private fun onUpdateToolId(id: Int) {
        _uiState.update { it.copy(selectedToolId = id) }
    }

    private fun onUpdateImagePreview(uri: Uri?) {
        _uiState.update { it.copy(imagePreview = uri) }
    }

    private fun onUpdateProgress(progress: Float) {
        _uiState.update { it.copy(progress = progress) }
    }

    private fun onUpdateTune(boolean: Boolean) {
        _uiState.update { it.copy(
            isTuneDialogVisible = boolean
        ) }
    }

    private fun onUpdateAutoTune(boolean: Boolean) {
        _uiState.update { it.copy(
            isAutoTuneDialogVisible = boolean
        ) }
    }

    private fun onUpdateColor(colorMatrix: ColorMatrix){
     _uiState.update {
         it.copy(
             editColorMatrix = colorMatrix
         )
     }
    }

    private fun onAutoTuneBitmap(imageBitmap: ImageBitmap){
        _uiState.update {
            it.copy(
                autoTuneBitmap = imageBitmap
            )
        }
    }

    private fun shouldShowCropOption(boolean: Boolean){
        _uiState.update {
            it.copy(
                showCropOption = boolean
            )
        }
    }

    private fun shouldSendCroppedBitmap(boolean: Boolean){
        _uiState.update {
            it.copy(
                isBitmapCropped = boolean
            )
        }
    }

    private fun freeCropActivated(boolean: Boolean){
        _uiState.update {
            it.copy(
                isFreeCrop = boolean
            )
        }
    }

    private fun sendCroppedImage(croppedImage:Uri?){
        _uiState.update {
            it.copy(
                croppedImageUri = croppedImage
            )
        }
    }
}

sealed interface EditImageEvent : Event {
    data class UpdateToolId(val id: Int) : EditImageEvent
    data class UpdateImagePreview(val uri: Uri?) : EditImageEvent
    data class UpdateProgress(val progress: Float) : EditImageEvent
    data class UpdateTune(val boolean: Boolean): EditImageEvent

    data class UpdateAutoTune(val boolean: Boolean): EditImageEvent
    data class UpdateColor(val colorMatrix: ColorMatrix): EditImageEvent

    data class UpdateAutoTuneBitmap(val imageBitmap: ImageBitmap): EditImageEvent
    data class shouldShowCropOptions(val boolean: Boolean): EditImageEvent
    data class ShouldSendCropped(val boolean: Boolean): EditImageEvent
    data class FreeCropActivated(val boolean: Boolean): EditImageEvent
    data class SendCroppedImage(val croppedImage: Uri?): EditImageEvent
}