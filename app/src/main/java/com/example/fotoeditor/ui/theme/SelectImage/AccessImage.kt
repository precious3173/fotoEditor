package com.example.fotoeditor.ui.theme.SelectImage

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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