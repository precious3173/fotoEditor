package com.example.fotoeditor.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fotoeditor.ui.utils.Crops

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsBottomSheet(
    background: Color,
    onDismissRequest: () -> Unit,
    visible: Boolean,
    content: @Composable () -> Unit,
) {
    Box(contentAlignment = Alignment.BottomCenter) {
        Scrim(
            color = BottomSheetDefaults.ScrimColor,
            onDismissRequest = onDismissRequest,
            visible = visible
        )
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight + 400 },
                animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = 0.8f)
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight + 400 },
                animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = 0.8f)
            )
        ) {

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)

            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(background), contentAlignment = Alignment.BottomCenter) {
                    content()
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ExportBottomSheet(
    background: Color,
    onDismissRequest: () -> Unit,
    visible: Boolean,
    sheetState: SheetState,
    content: @Composable () -> Unit,
){

    Box(contentAlignment = Alignment.BottomCenter) {
        Scrim(
            color = BottomSheetDefaults.ScrimColor,
            onDismissRequest = onDismissRequest,
            visible = visible
        )
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight + 400 },
                animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = 0.8f)
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight + 400 },
                animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = 0.8f)
            )
        ) {

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(background), contentAlignment = Alignment.BottomCenter) {
                    content()
                }
            }
        }
    }
}
@Composable
private fun Scrim(
    color: Color,
    onDismissRequest: () -> Unit,
    visible: Boolean,
) {
    if (color.isSpecified) {
        val alpha by animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = com.example.fotoeditor.ui.utils.TweenSpec(), label = "AnimateScrim"
        )

        val dismissSheet = if (visible) {
            Modifier
                .pointerInput(onDismissRequest) {
                    detectTapGestures { onDismissRequest() }
                }
                .clearAndSetSemantics { }
        } else Modifier

        Canvas(
            Modifier
                .fillMaxSize()
                .then(dismissSheet)
        ) {
            drawRect(color = color, alpha = alpha)
        }
    }
}
@Composable
fun CropSheet(crops: List<Crops> ) {
    val listState = rememberLazyListState()
    LazyRow(
        state = listState,
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
          items(crops){
             crop ->
              Spacer(modifier = Modifier.width(10.dp))
              CropItem(crops = crop, textColor = Color.Gray )
              Spacer(modifier = Modifier.width(10.dp))
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