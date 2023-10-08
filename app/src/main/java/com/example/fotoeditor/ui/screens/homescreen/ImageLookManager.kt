package com.example.fotoeditor.ui.screens.homescreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.net.Uri
import android.util.Base64
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import com.google.gson.Gson
class ImageLookManager(private val context: Context) {

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("image_look", Context.MODE_PRIVATE)
    private val gson = Gson()
    companion object {
        private const val PREF_BITMAP = "bitmap_key"
        private const val PREF_INT = "int_key"
        private const val PREF_ARRAY = "floatArray_Key"
    }

    @SuppressLint("SuspiciousIndentation")
    fun getSavedFilteredUri(): Uri?{

        val uriString = sharedPrefs.getString(PREF_BITMAP, null)

            try {

                return uriString?.let { Uri.parse(it) }
//
//                val colorFilterArray = gson.fromJson(retrieveColorFilterArray, FloatArray::class.java)
//
//                if (saveImageLook!= null) {
//                    val byteArray = Base64.decode(saveImageLook, Base64.DEFAULT)
//                    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)





            } catch (e: Exception) {
                e.printStackTrace()
            }
        return null


    }

    fun getSavedInt(): Int {

        return sharedPrefs.getInt(PREF_INT, 0)
    }

    @SuppressLint("CommitPrefEdits")
    fun saveBitmapToSharedPreferences(context: Context, bitmap: Bitmap, floatArray: FloatArray) {


        try {
            val filteredBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            val canvas = android.graphics.Canvas(filteredBitmap)
            val paint = Paint()

            val colorMatrix = android.graphics.ColorMatrix(floatArray)

            paint.colorFilter = android.graphics.ColorMatrixColorFilter(colorMatrix)

            canvas.drawBitmap(filteredBitmap, 0f, 0f, paint)
            val cacheDir = context.cacheDir
            // Create a temporary file in the cache directory
            val tempFile = File.createTempFile("temp_image", ".jpg", cacheDir)

            // Write the bitmap to the temporary file
            val outputStream = FileOutputStream(tempFile)
            filteredBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            // Create a content URI for the temporary file
            val uri = FileProvider.getUriForFile(
                context,
                "com.example.fotoEditor.provider",
                tempFile
            )

            sharedPrefs.edit()
                .putString(PREF_BITMAP, uri.toString())
                .apply()

        } catch (e: Exception) {
            e.stackTrace
        }

    }

}