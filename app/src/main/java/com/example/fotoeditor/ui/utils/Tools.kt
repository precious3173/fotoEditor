package com.example.fotoeditor.ui.utils

import androidx.annotation.DrawableRes
import com.example.fotoeditor.R

data class Tool(
    val id: Int,
    val name: String,
    @DrawableRes val icon: Int,
)

object ToolLibrary {
    val tools = listOf(
        Tool(
            id = 1,
            name = "Tune Image",
            icon = R.drawable.icon_tune_image,
        ),
        Tool(
            id = 2,
            name = "Details",
            icon = R.drawable.icon_details,
        ),
//        Tool(
//            id = 3,
//            name = "Curves",
//            icon = R.drawable.icon_curves,
//        ),
//        Tool(
//            id = 4,
//            name = "White Balance",
//            icon = R.drawable.icon_white_balance,
//        ),
        Tool(
            id = 3,
            name = "Crop",
            icon = R.drawable.icon_crop,
        ),
        Tool(
            id = 4,
            name = "Rotate",
            icon = R.drawable.icon_rotate,
        ),

        Tool(
            id = 5,
            name = "Vintage",
            icon = R.drawable.icon_vintage,
        ),
        Tool(
            id = 6,
            name = "Lens Blur",
            icon = R.drawable.icon_lens_blur,
        ),
        Tool(
            id = 7,
            name = "Text",
            icon = R.drawable.text,
        ),
        Tool(
            id = 8,
            name = "Frames",
            icon = R.drawable.frame,
        ),
//        Tool(
//            id = 7,
//            name = "Perspective",
//            icon = R.drawable.icon_perspective,
//        ),
//        Tool(
//            id = 8,
//            name = "Expand",
//            icon = R.drawable.icon_expand,
//        ),
//        Tool(
//            id = 9,
//            name = "Selective",
//            icon = R.drawable.icon_selective,
//        ),
//        Tool(
//            id = 10,
//            name = "Brush",
//            icon = R.drawable.icon_brush,
//        ),
//        Tool(
//            id = 11,
//            name = "Healing",
//            icon = R.drawable.icon_healing,
//        ),
//        Tool(
//            id = 12,
//            name = "HDR Scape",
//            icon = R.drawable.icon_hdr_scape,
//        ),
//        Tool(
//            id = 13,
//            name = "Glamour Glow",
//            icon = R.drawable.icon_glamour_glow,
//        ),
//        Tool(
//            id = 14,
//            name = "Tonal Contrast",
//            icon = R.drawable.icon_tonal_constrast,
//        ),
//        Tool(
//            id = 15,
//            name = "Drama",
//            icon = R.drawable.icon_drama,
//        ),

//        Tool(
//            id = 17,
//            name = "Grainy Film",
//            icon = R.drawable.icon_grainy_film,
//        ),
//        Tool(
//            id = 18,
//            name = "Retrolux",
//            icon = R.drawable.icon_retrolux,
//        ),
//        Tool(
//            id = 19,
//            name = "Grunge",
//            icon = R.drawable.icon_grunge,
//        ),
//        Tool(
//            id = 20,
//            name = "Black & White",
//            icon = R.drawable.icon_black_n_white,
//        ),
//        Tool(
//            id = 21,
//            name = "Noir",
//            icon = R.drawable.icon_noir,
//        ),
//        Tool(
//            id = 22,
//            name = "Portrait",
//            icon = R.drawable.icon_portrait,
//        ),
//        Tool(
//            id = 23,
//            name = "Head Pose",
//            icon = R.drawable.icon_head_pose,
//        ),

    )
}