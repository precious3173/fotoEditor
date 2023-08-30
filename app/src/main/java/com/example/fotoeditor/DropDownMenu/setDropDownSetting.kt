package com.example.fotoeditor.DropDownMenu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun OptionsMenu(
    state: Boolean,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopEnd)
            .absolutePadding(top = 45.dp, right = 20.dp)
    ) {
        DropdownMenu(
            expanded = state,
            onDismissRequest = onDismiss,
        )
        {
            DropdownMenuItem(
                text = { Text("Settings") },
                onClick = onDismiss
            )

            DropdownMenuItem(
                text = { Text("Tutorials") },
                onClick = onDismiss
            )
            DropdownMenuItem(
                text = { Text("Help & Feedback") },
                onClick = onDismiss
            )
        }
    }
}
