package com.example.fotoeditor.ui.utils

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.fotoeditor.R

data class HomeMenuItem(
    @DrawableRes val icon: Int = -1,
    val title: String = "",
    val contentDesc: String,
    val visible: Boolean = true,
   val  color: Color
)

object HomeMenuDefaults {
    fun isDarkTheme(isDarkTheme:Boolean): List<HomeMenuItem>{
        val iconColor = if (isDarkTheme) Color.White else Color.Black // Determine icon color based on theme
      val menus =
       if (isDarkTheme) {
            listOf(
               HomeMenuItem(
                   icon = R.drawable.icon_info,
                   contentDesc = "menu_info",
                   visible = true,
                   color = iconColor

               ),
               HomeMenuItem(
                   icon = R.drawable.icon_more_vert,
                   contentDesc = "menu_more_items",
                   visible = true,
                   color = iconColor
               ),
               HomeMenuItem(
                   title = "Settings",
                   contentDesc = "menu_more_settings",
                   visible = false,
                   color = iconColor
               ),
           )
       }
        else{
            listOf(
               HomeMenuItem(
                   icon = R.drawable.icon_info_white,
                   contentDesc = "menu_info",
                   visible = true,
                   color = iconColor
               ),
               HomeMenuItem(
                   icon = R.drawable.icon_more_vert_white,
                   contentDesc = "menu_more_items",
                   visible = true,
                   color = iconColor
               ),
               HomeMenuItem(
                   title = "Settings",
                   contentDesc = "menu_more_settings",
                   visible = false,
                   color = iconColor
               ),
           )
        }

        return menus
    }


}