package com.example.fotoeditor.domain

import com.example.fotoeditor.domain.models.ImageFilter
import jp.co.cyberagent.android.gpuimage.GPUImage

interface EditImageMapper {
    fun mapToImageFilters(gpuImage: GPUImage): List<ImageFilter>
}