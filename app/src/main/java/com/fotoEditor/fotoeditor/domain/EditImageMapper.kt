package com.fotoEditor.fotoeditor.domain


import com.fotoEditor.fotoeditor.domain.models.ImageFilter
import jp.co.cyberagent.android.gpuimage.GPUImage

interface EditImageMapper {
    fun mapToImageFilters(gpuImage: GPUImage): List<ImageFilter>
}