package com.fotoEditor.fotoeditor.ui.screens.editimagescreen

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import com.fotoEditor.fotoeditor.ui.utils.Event
import com.fotoEditor.fotoeditor.ui.utils.EventHandler
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
            is EditImageEvent.IsFreeMode -> isFreeMode(event.boolean)
            is EditImageEvent.IsNotFreeMode -> isNotFreeMode(event.boolean)
            is EditImageEvent.ShouldRotateImage -> shouldRotateImage( event.rotateImageValue)
            is EditImageEvent.SaveImageBitmap -> saveImageBitmap(event.bitmap)
            is EditImageEvent.SaveRotatedImage -> saveRotatedImage(event.boolean)
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

    private fun shouldRotateImage( rotateImageValue: Float){
        _uiState.update {
            it.copy(
                rotateImageValue = rotateImageValue

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
    private fun isFreeMode (boolean: Boolean){
        _uiState.update {
            it.copy(
                isFreeMode = boolean,
                isNotFreeMode = false

            )
        }
    }
    private fun isNotFreeMode (boolean: Boolean){
        _uiState.update {
            it.copy(
                isNotFreeMode = boolean,
                isFreeMode = false
            )
        }
    }

    private fun saveImageBitmap(bitmap: Bitmap){
_uiState.update {
    it.copy(
        getBitmap = bitmap
    )
}
    }

    private fun saveRotatedImage(boolean: Boolean){
        _uiState.update {
            it.copy(
                rotateImage = boolean
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
    data class IsFreeMode(val boolean: Boolean): EditImageEvent
    data class IsNotFreeMode(val boolean: Boolean): EditImageEvent
    data class ShouldRotateImage(val rotateImageValue: Float): EditImageEvent
    data class SaveImageBitmap(val bitmap: Bitmap): EditImageEvent
    data class  SaveRotatedImage(val boolean: Boolean): EditImageEvent


}