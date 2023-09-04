package com.example.fotoeditor.ui.screens.editimagescreen

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
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
}

sealed interface EditImageEvent : Event {
    data class UpdateToolId(val id: Int) : EditImageEvent
    data class UpdateImagePreview(val uri: Uri?) : EditImageEvent
    data class UpdateProgress(val progress: Float) : EditImageEvent

}