package com.example.fotoeditor.domain

import android.net.Uri
import java.io.File
interface FileHelper {
    fun getMediaStorage(): File

    fun getFileUriByName(name: String): Uri

    fun deleteFile(filename: String): Boolean

    fun getLastModified(filename: String): String
}