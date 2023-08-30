package com.example.fotoeditor.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LooksBottomSheet(
    images: @Composable LazyItemScope.() -> Unit,
) {
    val listState = rememberLazyListState()
    LazyRow(
        state = listState,
        contentPadding = PaddingValues(0.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            Spacer(Modifier.width(16.dp))
            images()
            Spacer(Modifier.width(16.dp))
        }
    }
}

@Composable
@Preview
private fun LooksBottomSheetPreview() {
    LooksBottomSheet(
        images = {
            Text("fjdbfjd", style = MaterialTheme.typography.displaySmall)
            Text("fjdbfjd", style = MaterialTheme.typography.displaySmall)
            Text("fjdbfjd", style = MaterialTheme.typography.displaySmall)
            Text("fjdbfjd", style = MaterialTheme.typography.displaySmall)
            Text("fjdbfjd", style = MaterialTheme.typography.displaySmall)
            Text("fjdbfjd", style = MaterialTheme.typography.displaySmall)
            Text("fjdbfjd", style = MaterialTheme.typography.displaySmall)
        }
    )
}