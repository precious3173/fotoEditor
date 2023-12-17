package com.fotoEditor.fotoeditor.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.fotoEditor.fotoeditor.ui.utils.Crops

@Composable
fun CropItem(
    crops: Crops,
    textColor: Color
) {

    Column(modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally){
        Icon(
            painterResource(id = crops.icon),
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.padding(10.dp).height(35.dp).width(35.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = crops.title)
    }
}