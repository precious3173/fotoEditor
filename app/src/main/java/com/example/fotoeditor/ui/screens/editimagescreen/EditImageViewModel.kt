package com.example.fotoeditor.ui.screens.editimagescreen

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.fotoeditor.ui.utils.Event
import com.example.fotoeditor.ui.utils.EventHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class EditImageViewModel(private val context: Context) : ViewModel(), EventHandler {
    private val _uiState = MutableStateFlow(EditImageUiState())
    val uiState = _uiState.asStateFlow()

    override fun onEvent(event: Event) {
        when (event) {
            is EditImageEvent.UpdateToolId -> onUpdateToolId(event.id)
            is EditImageEvent.UpdateImagePreview -> onUpdateImagePreview(event.uri)
        }
    }

    private fun onUpdateToolId(id: Int) {
        _uiState.update { it.copy(selectedToolId = id) }
    }

    private fun onUpdateImagePreview(uri: Uri?) {
        _uiState.update { it.copy(imagePreview = uri) }
    }
}

sealed interface EditImageEvent : Event {
    data class UpdateToolId(val id: Int) : EditImageEvent
    data class UpdateImagePreview(val uri: Uri?): EditImageEvent

}