package com.example.fotoeditor.ui.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.runtime.mutableStateOf

fun Uri.toBitmap(context: Context): Bitmap? {
    val bitmap = mutableStateOf<Bitmap?>(null)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(context.contentResolver, this)
        bitmap.value = ImageDecoder.decodeBitmap(source)
    } else {
        bitmap.value = MediaStore.Images.Media.getBitmap(context.contentResolver, this)
    }
    return bitmap.value
}

fun String.toJpeg(): String{
    return this.plus(".jpg")
}