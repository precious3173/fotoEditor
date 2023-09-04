package com.example.fotoeditor.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.IntOffset
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsBottomSheet(
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
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(350, 20, easing = LinearEasing)
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(350, 20, easing = LinearEasing)
            )
        ) {

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                Box(Modifier.fillMaxWidth().background(Color.White), contentAlignment = Alignment.BottomCenter) {
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