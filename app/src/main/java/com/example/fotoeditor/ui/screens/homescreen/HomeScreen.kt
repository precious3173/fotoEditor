package com.example.fotoeditor.ui.screens.homescreen

import android.Manifest
import android.content.ContentValues
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.fotoeditor.DropDownMenu.OptionsMenu
import com.example.fotoeditor.FilterColors.SelectFilter
import com.example.fotoeditor.R
import com.example.fotoeditor.ui.components.BottomBar
import com.example.fotoeditor.ui.components.LooksBottomSheet
import com.example.fotoeditor.ui.components.SimpleTopAppBar
import com.example.fotoeditor.ui.components.ToolItem
import com.example.fotoeditor.ui.components.ToolsBottomSheet
import com.example.fotoeditor.ui.nav.Navigator
import com.example.fotoeditor.ui.nav.Screen
import com.example.fotoeditor.ui.utils.Event
import com.example.fotoeditor.ui.utils.HomeMenuDefaults
import com.example.fotoeditor.ui.utils.ToolLibrary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeRoute(navigator: Navigator, viewModel: HomeScreenViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->

            if (isGranted) {
                Log.d(ContentValues.TAG, "Access granted")
            } else {
                Log.d(ContentValues.TAG, "Access  not granted")
            }
        }

    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // imageUri = uri
            uri?.let {
                viewModel.onEvent(HomeScreenEvent.LoadImageUri(it))
            }

            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }

        }

    val importPhoto: () -> Unit = {
        viewModel.onEvent(HomeScreenEvent.ImportImage { hasPermission ->
            if (hasPermission) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        })
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Scaffold(
            modifier = Modifier.padding(0.dp),
            topBar = {
                SimpleTopAppBar(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    navigationIcon = {
                        TextButton(
                            onClick = { importPhoto() },
                            shape = MaterialTheme.shapes.small,
                        ) {
                            Text(
                                text = stringResource(id = R.string.open),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.Black
                                ),
                            )
                        }
                    },
                    actions = {
                        HomeMenuDefaults.menus.forEachIndexed { _, homeMenuItem ->
                            val enabled = when (homeMenuItem.contentDesc) {
                                "menu_info" -> uiState.hasPhotoImported
                                else -> true
                            }

                            if (homeMenuItem.visible) {
                                Box(
                                    Modifier.padding(
                                        top = 8.dp,
                                        bottom = 8.dp,
                                        start = 12.dp,
                                    )
                                ) {
                                    val context = LocalContext.current
                                    IconButton(
                                        enabled = enabled,
                                        onClick = {
                                            if (homeMenuItem.contentDesc == "menu_more_items") {
                                                viewModel.onEvent(HomeScreenEvent.OpenOptionsMenu)
                                            } else if (homeMenuItem.contentDesc == "menu_info") {
                                                Toast.makeText(
                                                    context,
                                                    "info icon",
                                                    Toast.LENGTH_LONG
                                                )
                                                    .show()
                                            }

                                        }) {
                                        Icon(
                                            painterResource(id = homeMenuItem.icon),
                                            contentDescription = homeMenuItem.contentDesc,
                                            modifier = Modifier.semantics {
                                                contentDescription = homeMenuItem.contentDesc
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
            },
            content = {
                HomeScreen(
                    modifier = Modifier.padding(it),
                    onEvent = viewModel::onEvent,
                    hasPhotoImported = uiState.hasPhotoImported,
                    importedImageUri = uiState.importedImageUri,
                    shouldShowOptionsMenu = uiState.shouldShowOptionsMenu,
                    shouldExpandLooks = uiState.shouldExpandLooks,
                    importPhoto = importPhoto,
                    selectedFilter = uiState.selectedFilter,
                )
            },
            bottomBar = {
                AnimatedVisibility(visible = uiState.hasPhotoImported) {
                    BottomBar {
                        BottomBarDefaults.items.forEachIndexed { index, item ->
                            val looksTextColor by animateColorAsState(
                                targetValue =
                                if (uiState.shouldExpandLooks) {
                                    Color.Blue.copy(0.4f)
                                } else Color.Gray, label = "LooksTextColor"
                            )
                            val toolsTextColor by animateColorAsState(
                                targetValue =
                                if (uiState.shouldExpandTools) {
                                    Color.Blue.copy(0.4f)
                                } else Color.Gray, label = "ToolsTextColor"
                            )

                            Box(
                                Modifier
                                    .weight(1f)
                                    .clickable(
                                        enabled = true,
                                        onClick = {
                                            when (index) {
                                                0 -> viewModel.onEvent(HomeScreenEvent.ToggleLooks)

                                                1 -> viewModel.onEvent(HomeScreenEvent.ToggleTools)
                                                else -> Unit
                                            }
                                        },
                                        role = Role.Button,
                                    ), contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    Modifier.padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = when (index) {
                                                0 -> looksTextColor
                                                1 -> toolsTextColor
                                                else -> Color.Gray
                                            },
                                        ),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        )

        if (uiState.shouldExpandTools) {
            ToolsBottomSheet(
                onDismissRequest = { viewModel.onEvent(HomeScreenEvent.ToggleTools) },
                visible = uiState.shouldExpandTools,
                content = {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .heightIn(max = 800.dp)
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(count = 4),
                            state = rememberLazyGridState(),
                            contentPadding = PaddingValues(12.dp),
                        ) {
                            items(ToolLibrary.tools) {
                                Box(
                                    Modifier
                                        .clip(CircleShape)
                                        .clickable(
                                            enabled = true,
                                            onClick = {
                                                viewModel.onEvent(HomeScreenEvent.SelectTool(it.id))
                                                navigator.navigateTo(
                                                    route = Screen.EditImageScreen.withArgs(
                                                        "${it.id}"
                                                    )
                                                )
                                            },
                                            role = Role.Button,
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    ToolItem(it)
                                }
                            }
                        }
                    }
                }
            )
        }

    }
}

@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    onEvent: (Event) -> Unit,
    hasPhotoImported: Boolean,
    importedImageUri: Uri?,
    shouldShowOptionsMenu: Boolean,
    shouldExpandLooks: Boolean,
    importPhoto: () -> Unit,
    selectedFilter: Int?
) {
    val offset = 20
    AnimatedVisibility(
        visible = shouldShowOptionsMenu,
        enter = slideInVertically(
            initialOffsetY = { offset },
            animationSpec = tween(
                durationMillis = offset,
                easing = FastOutSlowInEasing,
            )
        ),
        exit = slideOutVertically(
            targetOffsetY = { -offset },
            animationSpec = tween(
                durationMillis = offset,
                easing = FastOutSlowInEasing,
            )
        )
    ) {
        OptionsMenu(
            onDismiss = { onEvent(HomeScreenEvent.HideOptionsMenu) },
            state = shouldShowOptionsMenu,
        )
    }

    Box(
        Modifier
            .fillMaxSize()
            .then(modifier),
        contentAlignment = Alignment.TopCenter,
    ) {
        HomeScreenContent(
            importPhoto = importPhoto,
            hasPhotoImported = hasPhotoImported,
            importedImageUri = importedImageUri,
            shouldExpandLooks = shouldExpandLooks,
            selectedFilter = selectedFilter,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun HomeScreenContent(
    importPhoto: () -> Unit,
    hasPhotoImported: Boolean,
    importedImageUri: Uri?,
    shouldExpandLooks: Boolean,
    selectedFilter: Int?,
    onEvent: (Event) -> Unit,
) {
    AnimatedContent(hasPhotoImported, label = "ImportedPhotoAnimation") { targetState ->
        when (targetState) {
            //when no image has been imported
            false -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .clickable(
                            enabled = true,
                            onClick = importPhoto,
                            role = Role.Button,
                        ),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        Modifier.padding(
                            top = 8.dp, bottom = 8.dp
                        )
                    ) {
                        Icon(
                            painterResource(id = R.drawable.icon_add_circle),
                            contentDescription = null,
                            modifier = Modifier.size(168.dp)
                        )
                    }

                    Box(Modifier.padding(top = 8.dp, bottom = 8.dp)) {
                        Text(
                            text = stringResource(id = R.string.tap_anywhere_to_open_a_photo),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            //with image imported
            true -> {
                val context = LocalContext.current
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                    Column(
                        Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        //the preview image
                        importedImageUri?.let {
                            AsyncImage(
                                model = it,
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .animateContentSize()
                                    .weight(1f),
                                colorFilter = ColorFilter.colorMatrix(
                                    ColorMatrix(SelectFilter(selectedFilter!!))
                                )
                            )
                        }

                        AnimatedVisibility(visible = shouldExpandLooks) {
                            LooksBottomSheet {
                                repeat(12) { index ->
                                    val filterName = when (index) {
                                        0 -> "Current"
                                        1 -> "Portrait"
                                        2 -> "Smooth"
                                        3 -> "Pop"
                                        4 -> "Accentuate"
                                        5 -> "Faded Glow"
                                        6 -> "Morning"
                                        7 -> "Bright"
                                        8 -> "Fine Art"
                                        9 -> "Push"
                                        10 -> "Structure"
                                        11 -> "Silhouette"
                                        else -> ""
                                    }


                                    Box(
                                        Modifier.padding(4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val toolColor by animateColorAsState(
                                            targetValue = if (index == selectedFilter) Color.Blue.copy(
                                                0.4f
                                            ) else Color.Transparent,
                                            label = "ToolColor"
                                        )

                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Box(Modifier
                                                .border(
                                                    width = 1.8.dp,
                                                    shape = RectangleShape,
                                                    color = toolColor,
                                                )
                                                .selectable(
                                                    selected = true,
                                                    onClick = {
                                                        onEvent(
                                                            HomeScreenEvent.UpdateFilter(
                                                                index
                                                            )
                                                        )
                                                    }
                                                ), contentAlignment = Alignment.Center) {
                                                importedImageUri?.let {
                                                    AsyncImage(
                                                        model = it,
                                                        contentDescription = null,
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier
                                                            .width(77.dp)
                                                            .height(90.dp),
                                                        colorFilter = ColorFilter.colorMatrix(
                                                            ColorMatrix(
                                                                SelectFilter(index)
                                                            )
                                                        )
                                                    )
                                                }
                                            }
                                            Text(
                                                text = filterName,
                                                style = MaterialTheme.typography.labelSmall
                                            )

                                        }

                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }

}


data class BottomBarItem(
    val title: String,
)

object BottomBarDefaults {
    val items = listOf(
        BottomBarItem("LOOKS"),
        BottomBarItem("TOOLS"),
        BottomBarItem("EXPORT"),
    )
}