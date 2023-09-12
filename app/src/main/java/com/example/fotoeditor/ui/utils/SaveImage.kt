package com.example.fotoeditor.ui.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date

class SaveImage() {

    object SaveImageToGallery{
       fun saveToGallery(context: Context, uriImport: Uri){
           val resolver =
               context.contentResolver
           val contentValues = ContentValues()
           var outputStream: OutputStream? = null
           var imageUri: Uri? = null
           var imagesDir: File? = null
           var imageFile: File? = null
           val timeStamp: String =
               SimpleDateFormat("yyyyMMdd_HHmmss").format(
                   Date()
               )
           try {
               val bitmap =
                   MediaStore.Images.Media.getBitmap(
                       context.contentResolver,
                       uriImport
                   )
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                   contentValues.put(
                       MediaStore.MediaColumns.DISPLAY_NAME,
                       "fotoEditor.jpeg"
                   )
                   contentValues.put(
                       MediaStore.MediaColumns.MIME_TYPE,
                       "image/jpeg"
                   )
                   contentValues.put(
                       MediaStore.MediaColumns.RELATIVE_PATH,
                       Environment.DIRECTORY_PICTURES + File.separator + "fotoEditor"
                   )

                   imageUri = resolver.insert(
                       MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                       contentValues
                   )
                   val newUri =
                       resolver.insert(
                           imageUri!!,
                           contentValues
                       )

                   outputStream =
                       resolver.openOutputStream(
                           (newUri!!)
                       )
               } else {
                   imagesDir = File(
                       Environment
                           .getExternalStorageDirectory()
                           .toString() + File.separator + "fotoEditor"
//                                                                    Environment
//                                                                        .getExternalStoragePublicDirectory(
//                                                                            Environment.DIRECTORY_PICTURES
//                                                                        )
//                                                                        .toString() + File.separator + "fotoEditor"
                   )

                   if (!imagesDir.exists()) imagesDir.mkdir()

                   imageFile = File(
                       imagesDir,
                       "JPEG_${timeStamp}_" + ".jpg"
                   )
                   outputStream =
                       FileOutputStream(imageFile)

               }
               if (!bitmap.compress(
                       Bitmap.CompressFormat.PNG, 100,
                       outputStream!!
                   )
               )
                   throw IOException("Failed to save bitmap.");
               outputStream.flush();

//
//                                                            val intent =
//                                                                Intent(Intent.ACTION_VIEW, imageUri)
//                                                            context.startActivity(intent)


//               MediaScannerConnection.scanFile(
//                   context,
//                   arrayOf(arrayOf(imageFile!!.absoluteFile).toString()), null
//               ){
//                       path, uri -> val intent = Intent(Intent.ACTION_VIEW, uri)
//                   context.startActivity(
//                       Intent.createChooser(
//                           intent, "View Image"
//                       )
//                   )
//               }



               Toast
                   .makeText(
                       context,
                       "Image Saved",
                       Toast.LENGTH_SHORT
                   )
                   .show()

           } catch (e: Exception) {
               Toast
                   .makeText(
                       context,
                       "Image Not Not  Saved: \n $e",
                       Toast.LENGTH_SHORT
                   )
                   .show()
               e.printStackTrace()
           } finally {
               if (outputStream != null)
                   outputStream.close();
           }

       }
    }



}