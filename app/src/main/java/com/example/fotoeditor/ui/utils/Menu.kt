package com.example.fotoeditor.ui.utils

import androidx.annotation.DrawableRes
import com.example.fotoeditor.R

data class HomeMenuItem(
    @DrawableRes val icon: Int = -1,
    val title: String = "",
    val contentDesc: String,
    val visible: Boolean = true,
)

object HomeMenuDefaults {
    val menus = listOf<HomeMenuItem>(
        HomeMenuItem(
            icon = R.drawable.icon_info,
            contentDesc = "menu_info",
            visible = true,
        ),
        HomeMenuItem(
            icon = R.drawable.icon_more_vert,
            contentDesc = "menu_more_items",
            visible = true,
        ),
        HomeMenuItem(
            title = "Settings",
            contentDesc = "menu_more_settings",
            visible = false,
        ),
    )
}