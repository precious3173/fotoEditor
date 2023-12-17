package com.fotoEditor.fotoeditor.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun EditImageBottomBar(
    modifier: Modifier = Modifier,
    abort: @Composable () -> Unit,
    save: @Composable () -> Unit,
    content: @Composable (RowScope.() -> Unit)? = null,
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(contentAlignment = Alignment.Center) {
            abort()
        }
        content?.let {
            Row(
                Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                it()
            }
        }

        Box(contentAlignment = Alignment.Center) {
            save()
        }
    }
}

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

@Composable
@Preview
private fun EditImageBottomBarPreview() {
    EditImageBottomBar(
        save = {Text("save")},
        abort = {Text("Abort")},
        content = {
            Text("content1")
            Text("content2")
        }
    )
}