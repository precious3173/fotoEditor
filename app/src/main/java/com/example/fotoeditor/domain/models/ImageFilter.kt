package com.example.fotoeditor.domain.models

import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
data class ImageFilter(
    val id: Int = 1,
    val name: String = "",
    val filter: GPUImageFilter,
    val filterPreview: Bitmap,
)