package com.example.fotoeditor.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SimpleTopAppBar(
    navigationIcon: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box { navigationIcon() }
        Row(
            Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            actions()
        }
    }
}

@Composable
@Preview
private fun SimpleTopAppBarPreview() {
    SimpleTopAppBar(
        navigationIcon = {
            Text("Go back")
        },
        actions = {
            repeat(3) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null
                )
            }
        }
    )
}