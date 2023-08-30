package com.example.fotoeditor.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun BottomBar(
    actions: @Composable RowScope.() -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        Divider()
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            actions()
        }
    }
}

@Composable
@Preview
private fun BottomBarPreview() {
    BottomBar(
        actions = {
            Button(
                onClick = {}
            ) {
                Text("Click Me")
            }
            Button(
                onClick = {}
            ) {
                Text("Click Me")
            }
            Button(
                onClick = {}
            ) {
                Text("Click Me")
            }
        }
    )
}