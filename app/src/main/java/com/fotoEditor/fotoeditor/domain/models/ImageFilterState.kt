package com.fotoEditor.fotoeditor.domain.models

data class ImageFilterState(
    val isLoading: Boolean = false,
    val imageFilters: List<ImageFilter> = emptyList(),
    val error: String? = null
)