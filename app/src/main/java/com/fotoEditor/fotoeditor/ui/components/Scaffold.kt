package com.fotoEditor.fotoeditor.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun Scaffold(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    topAppBar: @Composable (() -> Unit)? = null,
    bottomBar: @Composable (() -> Unit)? = null,
) {
    Column(
        Modifier.fillMaxSize().then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        topAppBar?.let {
            Box(Modifier.fillMaxWidth()) {
                it()
            }
        }

        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
            content()
        }

        bottomBar?.let {
            Box(Modifier.fillMaxWidth()) { it() }
        }
    }
}