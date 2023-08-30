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
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.fotoeditor.DropDownMenu.setDropDownSetting
import com.example.fotoeditor.R
import com.example.fotoeditor.ui.components.SimpleTopAppBar
import com.example.fotoeditor.ui.nav.Navigator
import com.example.fotoeditor.ui.utils.Event
import com.example.fotoeditor.ui.utils.HomeMenuDefaults

@Composable
fun HomeRoute(navigator: Navigator, viewModel: HomeScreenViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        content = {
            HomeScreen(
                modifier = Modifier.padding(it),
                onEvent = viewModel::onEvent,
                hasPhotoImported = uiState.hasPhotoImported,
                importedImageUri = uiState.importedImageUri,
            )
        }
    )
}

@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    onEvent: (Event) -> Unit,
    hasPhotoImported: Boolean,
    importedImageUri: Uri?,
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    if (expanded) {
        setDropDownSetting()
    }


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
                onEvent(HomeScreenEvent.LoadImageUri(it))
            }

            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }

        }

    val importPhoto: () -> Unit = {
        onEvent(HomeScreenEvent.ImportImage { hasPermission ->
            if (hasPermission) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        })
    }

    Box(
        Modifier
            .fillMaxSize()
            .padding(
                start = 16.dp,
                end = 16.dp
            )
            .then(modifier),
        contentAlignment = Alignment.TopCenter,
    ) {
        HomeScreenContent(
            importPhoto = importPhoto,
            hasPhotoImported = hasPhotoImported,
            importedImageUri = importedImageUri,
        )

        SimpleTopAppBar(
            navigationIcon = {
                TextButton({ importPhoto() }) {
                    Text(
                        text = stringResource(id = R.string.open),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            },
            actions = {
                HomeMenuDefaults.menus.forEachIndexed { _, homeMenuItem ->
                    if (homeMenuItem.visible) {
                        Box(
                            Modifier.padding(
                                top = 8.dp,
                                bottom = 8.dp,
                                start = 12.dp,
                            )
                        ) {
                            val context = LocalContext.current
                            IconButton(onClick = {
                                if (homeMenuItem.contentDesc == "menu_more_items") {
                                    expanded = true

                                } else if (homeMenuItem.contentDesc == "menu_info") {
                                    Toast.makeText(context, "info icon", Toast.LENGTH_LONG).show()
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
    }
}

@Composable
private fun HomeScreenContent(
    importPhoto: () -> Unit,
    hasPhotoImported: Boolean,
    importedImageUri: Uri?,
) {
    AnimatedContent(hasPhotoImported, label = "ImportedPhotoAnimation") { targetState ->
        when (targetState) {
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

            true -> {
                Box(Modifier.fillMaxSize()
                    .padding(
                        top = 58.dp,
                        bottom = 32.dp,
                    ), contentAlignment = Alignment.Center) {
                    importedImageUri?.let {
                        AsyncImage(
                            model = it,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
            }
        }
    }

}