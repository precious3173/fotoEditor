package com.example.fotoeditor.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.example.fotoeditor.R
import com.example.fotoeditor.ui.utils.Tool

@Composable
fun ToolItem(
    tool: Tool,
) {
    Column(
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.height(96.dp).width(96.dp).padding(8.dp),
    ) {
        Icon(
            painterResource(id = tool.icon),
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.padding(4.dp),
        )
        Text(
            text = tool.name,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.Gray,
            ),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
@Preview
private fun ToolItemPreview() {
    ToolItem(
        Tool(
            id = 1,
            name = "tool",
            icon = R.drawable.icon_noir,
        )
    )
}