package com.example.fotoeditor.ui.ExportImage

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.InputStream
import java.lang.Exception

class UriToBM {
    object UriToBit{
        fun UriToBitMap(context: Context, uri: Uri): Bitmap?{

            val contentResolver: ContentResolver = context.contentResolver
            var inputStream : InputStream? = null

        try {
            inputStream = contentResolver.openInputStream(uri)
            return BitmapFactory.decodeStream(inputStream)
        }
        catch (e: Exception){
            e.stackTrace
        }
            finally {
                inputStream!!.close()
            }

            return null
        }
    }
}