package com.fotoEditor.fotoeditor.ui.ExportImage

import android.content.Context
import android.content.Intent
import android.net.Uri

class ExportAs {
    object ExportToDownload{

        fun ExportAs(context: Context, uri: Uri){

            val bitmap = UriToBM.UriToBit.UriToBitMap(context, uri)

            if (bitmap != null){

                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/jpeg"

                intent.putExtra(Intent.EXTRA_TITLE, "fotoeditor_image.jpg")

                context.startActivity(intent)
            }

        }


    }
}