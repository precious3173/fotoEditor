package com.example.fotoeditor.ui.utils

import android.accounts.AuthenticatorDescription
import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.fotoeditor.R

data class Exports (
    val id: Int,
    val title: String,
    val description: String,
    @DrawableRes val icon: Int,
    )

object ExportLibrary {
    val exports = listOf(
        Exports(
            id = 1,
            title  = "Share",
            description = """
                Share your image with people or open it in another
                 app.
            """,
            icon = R.drawable.share,
        ),
        Exports(
            id = 2,
            title = "Save",
            description = "Create a copy of your photo",
            icon = R.drawable.save,
        ),
        Exports(
            id = 3,
            title = "Export",
            description = """
                Create a copy of your photo. Sizing, format 
                and quality can be changed in the settings menu.
            """,
            icon = R.drawable.jpeg,
        ),
        Exports(
            id = 4,
            title = "Export as",
            description = "Create a copy to selected folder",
            icon = R.drawable.export,
        )
    )
}