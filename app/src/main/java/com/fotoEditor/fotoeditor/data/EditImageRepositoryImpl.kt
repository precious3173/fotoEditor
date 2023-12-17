package com.fotoEditor.fotoeditor.data

import android.content.Context
import android.graphics.Bitmap
import com.fotoEditor.fotoeditor.domain.EditImageMapper
import com.fotoEditor.fotoeditor.domain.EditImageRepository
import com.fotoEditor.fotoeditor.domain.FileHelper
import com.fotoEditor.fotoeditor.domain.models.ImageFilter
import com.fotoEditor.fotoeditor.ui.utils.toJpeg
import dagger.hilt.android.qualifiers.ApplicationContext
import jp.co.cyberagent.android.gpuimage.GPUImage
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EditImageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mapper: EditImageMapper,
    private val fileHelper: FileHelper,
): EditImageRepository {
    override fun loadImageFilters(image: Bitmap): List<ImageFilter> {
        val gpuImage = GPUImage(context).apply {
            setImage(image)
        }
        return mapper.mapToImageFilters(gpuImage)
    }

    override suspend fun saveFilteredImage(filteredBitmap: Bitmap): String? {
        return try {
            val mediaStorageDir = fileHelper.getMediaStorage()
            if (!mediaStorageDir.exists()){
                mediaStorageDir.mkdirs()
            }
            val fileExt = ".jpg"
            val fileName = "JE_IMG_${System.currentTimeMillis()}".toJpeg()
            val file = File(mediaStorageDir, fileName)
            saveFile(file = file, bitmap = filteredBitmap)
            fileName.removeSuffix(fileExt)
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    private fun saveFile(file: File, bitmap: Bitmap){
        with(FileOutputStream(file)){
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, this)
            flush()
            close()
        }
    }
}