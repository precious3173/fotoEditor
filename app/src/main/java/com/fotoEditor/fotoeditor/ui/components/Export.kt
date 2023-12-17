package com.fotoEditor.fotoeditor.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fotoEditor.fotoeditor.R
import com.fotoEditor.fotoeditor.ui.utils.Exports


@Composable
fun ExportItem(
    exports: Exports,
    textColor: Color
) {
    Row(
      modifier = Modifier
          .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space = 15.dp)
    ) {
        Icon(
            painterResource(id = exports.icon),
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.padding(10.dp),
        )
        Column (modifier = Modifier.fillMaxWidth()
            .padding(10.dp)){
            Text(
                text = exports.title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = textColor,
                    fontSize = 16.sp,

                ),
                textAlign = TextAlign.Start,
            )
            Text(
                text = exports.description,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = textColor,
                    fontSize = 12.sp,
                ),
                textAlign = TextAlign.Start,
            )
        }

    }
}

@Composable
@Preview
private fun ExportItemPreview() {
    ExportItem(
        Exports(
            id = 1,
            title = "tool",
            description = "helllo",
            icon = R.drawable.icon_noir,
        ),
        Color.Gray
    )
}
