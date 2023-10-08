package com.example.fotoeditor.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.fotoeditor.R
import com.example.fotoeditor.ui.screens.homescreen.HomeScreenEvent
import com.example.fotoeditor.ui.screens.homescreen.HomeUiState
import com.example.fotoeditor.ui.screens.homescreen.ImageLookManager
import com.example.fotoeditor.ui.screens.homescreen.ImageLookSelector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation")
@Composable
fun CheckIcons(
    onEvent: (Event) -> Unit,
    coroutineScope: CoroutineScope,
    accessStorage: () -> Unit,
    imageLookManager: ImageLookManager,
    context: Context,
    bitmap: Bitmap?,
    floatArray: FloatArray,
    uiState: HomeUiState,

    ) {

    val floatArrayList = remember { mutableStateListOf<FloatArray>() }

    Icon(
        painter = painterResource(id = R.drawable.cross_23),
        contentDescription = null,
        modifier = Modifier
            .wrapContentSize()
            .padding(top = 20.dp, bottom = 10.dp, start = 30.dp, end = 30.dp)
            .clickable {
                coroutineScope.launch {
                    onEvent(HomeScreenEvent.FilterUnSelected)
                    onEvent(HomeScreenEvent.UpdateFilter(0))
                }

            }
    )

    Icon(
        painter = painterResource(id = R.drawable.check),
        contentDescription = null,
        modifier = Modifier
            .wrapContentSize()
            .padding(top = 20.dp, bottom = 10.dp, start = 30.dp, end = 30.dp)
            .clickable {

                coroutineScope.launch {
                    accessStorage()
                     onEvent(HomeScreenEvent.UpdatedImageCheck)
                    //onEvent(HomeScreenEvent.SendEditedUri)

                      floatArrayList.add(floatArray)

                  val bitmaps =  ImageLookSelector.ImageLook.applyFilter(bitmap!!, floatArrayList, context,
                      uiState.importedImageUri!!
                  )

                    if (uiState.imageLookChecked)onEvent(HomeScreenEvent.updateGetBitmap(bitmaps!!))
//                    imageLookManager.saveBitmapToSharedPreferences(context =context, bitmap = bitmap!!, floatArray = floatArray )
                }
            }
    )


}

