package com.example.fotoeditor.ui.ExportImage

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import java.lang.Exception

class AccessImage() {

    object AccessGallery{

        @SuppressLint("QueryPermissionsNeeded")
        fun AccessImage(context: Context, uri: Uri){

            try {

                val viewImageIntent = Intent().apply {
                    action = Intent.ACTION_VIEW
                    setDataAndType(uri, "image/*")
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }

                if (viewImageIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(viewImageIntent)
                } else {
                    // Handle the case when no app can handle the intent (e.g., no image viewer app installed)
                    // You can show a message or take an appropriate action here
                }
        }
            catch (e: Exception){

                e.stackTrace
            }}
    }

}