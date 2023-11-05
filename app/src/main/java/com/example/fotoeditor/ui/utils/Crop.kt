package com.example.fotoeditor.ui.utils

import androidx.annotation.DrawableRes
import com.example.fotoeditor.R

data class Crops (
    val id: Int,
    val title: String,
    @DrawableRes val icon: Int
)

object CropsLibrary{
    val crops = listOf(
        Crops(
            id = 1,
            title = "Free",
            icon = R.drawable.free_crop
        ),
        Crops(
            id = 2,
            title = "Original",
            icon = R.drawable.original
        ),
        Crops(
            id = 3,
            title = "Square",
            icon = R.drawable.crop_square
        ),
        Crops(
            id = 4,
            title = "Din",
            icon = R.drawable.crop_din
        ),
        Crops(
            id = 5,
            title = "3:2",
            icon = R.drawable.crop_3_2
        ),
        Crops(
            id = 6,
            title = "5:4",
            icon = R.drawable.crop_5_4
        ),
        Crops(
            id = 7,
            title = "7:5",
            icon = R.drawable.crop_7_5
        ),
        Crops(
            id = 8,
            title = "16:9",
            icon = R.drawable.crop_16_9
        )
    )

}