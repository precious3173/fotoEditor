package com.example.fotoeditor.data

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.fotoeditor.domain.FileHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileHelperImpl @Inject constructor(@ApplicationContext private val context: Context) : FileHelper {
    override fun getMediaStorage(): File =
        File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Saved Images")

    override fun getFileUriByName(name: String): Uri {
        val mediaStorageDir = getMediaStorage()
        val file = File(mediaStorageDir, name)
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    override fun deleteFile(filename: String): Boolean {
        val mediaStorageDir = getMediaStorage()
        val file = File(mediaStorageDir, filename)
        return file.delete()
    }

    override fun getLastModified(filename: String): String {
        val mediaStorageDir = getMediaStorage()
        val file = File(mediaStorageDir, filename)
        val lastModified = file.lastModified()
        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return formatter.format(lastModified)
    }
}