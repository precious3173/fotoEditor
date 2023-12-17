@file:OptIn(ExperimentalMaterial3Api::class)
@file:Suppress("UNUSED_EXPRESSION")

package com.fotoEditor.fotoeditor.ui.screens.homescreen

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
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
import com.fotoEditor.fotoeditor.DropDownMenu.OptionsMenu
import com.fotoEditor.fotoeditor.FilterColors.SelectFilter
import com.fotoEditor.fotoeditor.domain.models.ImageFilter
import com.fotoEditor.fotoeditor.ui.ExportImage.AccessImage
import com.fotoEditor.fotoeditor.ui.ExportImage.ExportAs
import com.fotoEditor.fotoeditor.ui.components.BottomBar
import com.fotoEditor.fotoeditor.ui.components.ExportBottomSheet
import com.fotoEditor.fotoeditor.ui.components.ExportItem
import com.fotoEditor.fotoeditor.ui.components.LooksBottomSheet
import com.fotoEditor.fotoeditor.ui.components.SimpleTopAppBar
import com.fotoEditor.fotoeditor.ui.components.ToolItem
import com.fotoEditor.fotoeditor.ui.components.ToolsBottomSheet
import com.fotoEditor.fotoeditor.ui.nav.Navigator
import com.fotoEditor.fotoeditor.ui.nav.Screen
import com.fotoEditor.fotoeditor.ui.screens.Settings.ThemeManager
import com.fotoEditor.fotoeditor.ui.screens.editimagescreen.EditImageUiState
import com.fotoEditor.fotoeditor.ui.screens.editimagescreen.EditImageViewModel
import com.fotoEditor.fotoeditor.ui.utils.Event
import com.fotoEditor.fotoeditor.ui.utils.ExportLibrary
import com.fotoEditor.fotoeditor.ui.utils.HomeMenuDefaults
import com.fotoEditor.fotoeditor.ui.utils.ToolLibrary
import com.fotoEditor.fotoeditor.ui.utils.toBitmap
import com.fotoEditor.fotoeditor.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.Exception


@SuppressLint("Recycle", "IntentReset")
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    navigator: Navigator,
    viewModel: HomeScreenViewModel,
    editImageViewModel: EditImageViewModel
) {
    val context = LocalContext.current
    var backgroundColor: Color? = null
    var textColor: Color? = null

    val themeManager = ThemeManager(LocalContext.current)
    val isDarkTheme by remember { mutableStateOf(themeManager.getSelectedTheme() == ThemeManager.THEME_DARK) }
    if (!isDarkTheme){
        backgroundColor = MaterialTheme.colorScheme.background
        textColor = Color.Black
    }
    else {
        backgroundColor = MaterialTheme.colorScheme.onBackground
        textColor = Color.White
    }
    var isVisible by remember { mutableStateOf(false) }
    var isUiState by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val editImageUiState by editImageViewModel.uiState.collectAsStateWithLifecycle()
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

    val accessStorage: () -> Unit = {
        viewModel.onEvent(HomeScreenEvent.AccessStorage{
            hasPermission ->
            if(hasPermission){
               Log.d(TAG, "Access granted")
               // Toast.makeText(context, "Access granted", Toast.LENGTH_SHORT).show()
            }
            else{
                launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                Toast.makeText(context, "Access not granted", Toast.LENGTH_SHORT).show()
            }
        })

    }



    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Scaffold(
            modifier = Modifier.padding(0.dp),
            backgroundColor = backgroundColor,
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
                                    color = textColor
                                ),
                            )
                        }
                    },
                    actions = {
                        HomeMenuDefaults.isDarkTheme(isDarkTheme).forEachIndexed { _, homeMenuItem ->
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
                                                Toast.makeText(
                                                    context,
                                                    "info icon",
                                                    Toast.LENGTH_LONG
                                                )
                                                    .show()
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
                                            tint = homeMenuItem.color,
                                            modifier = Modifier.semantics {
                                                contentDescription = homeMenuItem.contentDesc
                                            },

                                        )
                                    }
                                }
                            }
                        }
                    }
                )
            },
            content = {
                val context = LocalContext.current
                HomeScreen(
                    modifier = Modifier.padding(it),
                    onEvent = viewModel::onEvent,
                    hasPhotoImported = uiState.hasPhotoImported,
                    importedImageUri = uiState.importedImageUri,
                    shouldShowOptionsMenu = uiState.shouldShowOptionsMenu,
                    shouldExpandLooks = uiState.shouldExpandLooks,
                    importPhoto = importPhoto,
                    selectedFilter = uiState.selectedFilter,
                    shouldExpandExport = uiState.shouldExpandExport,
                    openDialog = uiState.openDialog,
                    isVisbile = isVisible,
                    editedImage = uiState.filterSelectedForUSe,
                    shouldSendEditedImageUri = uiState.shouldSendEditedImageUri,
                    isUiState = isUiState,
                    navigator = navigator,
                    textColor = textColor,
                    isDarkTheme = isDarkTheme,
                    shouldExpandTools = uiState.shouldExpandTools,
                    backgroundColor = backgroundColor,
                    uiState = uiState,
                    editImageUiState = editImageUiState

                    )
            },
            bottomBar = {
                val coroutineScope = rememberCoroutineScope()

                AnimatedVisibility(visible = uiState.hasPhotoImported) {
                    BottomBar {
                        if (uiState.filterSelected){
                            Icon(
                                painterResource(id = R.drawable.cross_23),
                                contentDescription = null,
                                tint = textColor,
                                modifier = Modifier
                                    .wrapContentSize()
                                    .padding(top = 20.dp, bottom = 10.dp)
                                    .clickable {
                                        viewModel.onEvent(HomeScreenEvent.FilterUnSelected)

//                                        viewModel.onEvent(
//                                            HomeScreenEvent.UpdateFilter(
//                                                0, bitmap
//                                            )
//                                        )
//
                                    }
                            )

                            Icon(
                                painterResource(id = R.drawable.check),
                                contentDescription = null,
                                tint = textColor,
                                modifier = Modifier
                                    .wrapContentSize()
                                    .padding(top = 20.dp, bottom = 10.dp)
                                    .clickable {

                                        accessStorage()
                                        viewModel.onEvent(HomeScreenEvent.SendEditedUri)


                                    }
                            )
                        }
                        else{

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
                                val exportTextColor by animateColorAsState(
                                    targetValue =
                                    if (uiState.shouldExpandExport){
                                        Color.Blue.copy(0.4f)
                                    }
                                    else Color.Gray, label = "ExportTextColor"
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

                                                    2 -> viewModel.onEvent(HomeScreenEvent.ToggleExport)
                                                    else -> Unit
                                                }
                                            },
                                            role = Role.Button,
                                        ), contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        Modifier
                                            .padding(vertical = 12.dp)
                                        ,
                                        contentAlignment = Alignment.Center

                                    ) {
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                color = when (index) {
                                                    0 -> looksTextColor
                                                    1 -> toolsTextColor
                                                    2 -> exportTextColor
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
            }
        )



        val sheetState = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()



        if (uiState.shouldExpandExport) {
            val context = LocalContext.current
            val snackDuration by remember { mutableStateOf(4000L)}
                ExportBottomSheet(
                    background = backgroundColor,
                onDismissRequest = { viewModel.onEvent(HomeScreenEvent.ToggleExport) },
                visible = uiState.shouldExpandExport,
                sheetState = sheetState,
                content = {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .heightIn(max = 800.dp)

                    ) {
                        LazyVerticalGrid (
                            columns = GridCells.Fixed(count = 1),
                            state = rememberLazyGridState(),
                            contentPadding = PaddingValues(5.dp),
                        ) {
                            items(ExportLibrary.exports){

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 2.dp)
                                        .clickable(
                                            enabled = true,
                                            onClick = {
                                                when (it.title) {

                                                    "Share" -> {

                                                        if (uiState.shouldSendEditedImageUri) {
                                                            viewModel.onEvent(
                                                                HomeScreenEvent.FilterSelectedForUse(
                                                                    uiState.savedImageUri,
                                                                    bitmap = uiState.savedImageBitmap!!,
                                                                    uiState.savedColorArray!!
                                                                )
                                                            )

                                                            scope.launch {
                                                                delay(2000L)
                                                                viewModel.onEvent(HomeScreenEvent.SaveImage)
                                                                shareFile(
                                                                    context,
                                                                    uiState.filterSelectedForUSe
                                                                )
                                                            }
                                                        }

                                                    }

                                                    "Save" -> {
                                                        accessStorage()
                                                        viewModel.onEvent(HomeScreenEvent.SaveImage)
                                                        viewModel.onEvent(HomeScreenEvent.ToggleExport)
                                                        viewModel.onEvent(HomeScreenEvent.ToggleLooks)
                                                        viewModel.onEvent(HomeScreenEvent.OnOpenDialog)
                                                        try {
                                                            if (uiState.shouldSendEditedImageUri) {
                                                                viewModel.onEvent(
                                                                    HomeScreenEvent.FilterSelectedForUse(
                                                                        uiState.savedImageUri,
                                                                        bitmap = uiState.savedImageBitmap!!,
                                                                        uiState.savedColorArray!!
                                                                    )
                                                                )
                                                            }

//                                                            SaveImage.SaveImageToGallery.saveToGallery(
//                                                                context,
//                                                                uiState.filterSelectedForUSe!!
//                                                            )

//                                                            viewModel.onEvent(
//                                                                HomeScreenEvent.LoadEditedImageUri(
//                                                                    uiState.filterSelectedForUSe!!
//                                                                )
//                                                            )
                                                        } catch (e: Exception) {
                                                            e.stackTrace
                                                        }

                                                        isVisible = true
                                                        CoroutineScope(Dispatchers.Default).launch {
                                                            delay(snackDuration)
                                                            isVisible = false
                                                        }


                                                    }

                                                    "Export" -> {
                                                        accessStorage()
                                                        viewModel.onEvent(HomeScreenEvent.SaveImage)
                                                        viewModel.onEvent(HomeScreenEvent.ToggleExport)
                                                        viewModel.onEvent(HomeScreenEvent.ToggleLooks)
                                                        viewModel.onEvent(HomeScreenEvent.OnOpenDialog)
                                                        try {
                                                            if (uiState.shouldSendEditedImageUri) {
                                                                viewModel.onEvent(
                                                                    HomeScreenEvent.FilterSelectedForUse(
                                                                        uiState.savedImageUri,
                                                                        bitmap = uiState.savedImageBitmap!!,
                                                                        uiState.savedColorArray!!
                                                                    )
                                                                )
                                                            }

//                                                            SaveImage.SaveImageToGallery.saveToGallery(
//                                                                context,
//                                                                uiState.filterSelectedForUSe!!
//                                                            )

//                                                            viewModel.onEvent(
//                                                                HomeScreenEvent.LoadEditedImageUri(
//                                                                    uiState.filterSelectedForUSe!!
//                                                                )
//                                                            )
                                                        } catch (e: Exception) {
                                                            e.stackTrace
                                                        }

                                                        isVisible = true
                                                        CoroutineScope(Dispatchers.Default).launch {
                                                            delay(snackDuration)
                                                            isVisible = false
                                                        }


                                                    }

                                                    "Export as" -> {
                                                        viewModel.onEvent(HomeScreenEvent.SaveImage)
                                                        try {
                                                            if (uiState.shouldSendEditedImageUri) {
                                                                viewModel.onEvent(
                                                                    HomeScreenEvent.FilterSelectedForUse(
                                                                        uiState.savedImageUri,
                                                                        bitmap = uiState.savedImageBitmap!!,
                                                                        uiState.savedColorArray!!
                                                                    )
                                                                )
                                                            }

                                                        } catch (e: Exception) {
                                                            e.stackTrace
                                                        }
                                                        ExportAs.ExportToDownload.ExportAs(
                                                            context,
                                                            uiState.filterSelectedForUSe!!
                                                        )
                                                    }
                                                }
                                            }
                                        )
                                ) {
                                    ExportItem(exports = it, textColor)
                                }
                            }
                        }
                    }
                }
            )

        }




//            {
//
//                Row (Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.Center){
//
//                    Button(
//                        // Note: If you provide logic outside of onDismissRequest to remove the sheet,
//                        // you must additionally handle intended state cleanup, if any.
//                        onClick = {
//                            scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
//                                if (!bottomSheetState.isVisible) {
//                               uiState.shouldExpandExport
//                                }
//                            }
//                        }
//                    ) {
//                        Text("Hide Bottom Sheet")
//                    }
//                }
//            }

        }
    }



fun shareFile(context:Context, uri: Uri?) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, uri )
        type = "image/jpeg"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
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
    selectedFilter: Int?,
    shouldExpandExport: Boolean,
    openDialog: Boolean,
    isVisbile: Boolean,
    editedImage: Uri?,
    shouldSendEditedImageUri: Boolean,
    isUiState: Boolean,
    navigator: Navigator,
    textColor: Color,
    isDarkTheme: Boolean,
    shouldExpandTools: Boolean,
    backgroundColor: Color,
    uiState: HomeUiState,
    editImageUiState: EditImageUiState

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
            navigator = navigator
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
            openDialog = openDialog,
            isVisible = isVisbile,
            editedImage = editedImage,
            shouldSendEditedImageUri = shouldSendEditedImageUri,
            isUiState = isUiState,
            textColor = textColor,
            shouldExpandTools = shouldExpandTools,
            navigator = navigator,
            backgroundColor = backgroundColor,
            uiState = uiState,
            editImageUistate = editImageUiState
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("CoroutineCreationDuringComposition", "SuspiciousIndentation")
@Composable
private fun HomeScreenContent(
    importPhoto: () -> Unit,
    hasPhotoImported: Boolean,
    importedImageUri: Uri?,
    shouldExpandLooks: Boolean,
    selectedFilter: Int?,
    onEvent: (Event) -> Unit,
    imageFilters: List<ImageFilter> = listOf(),
    openDialog: Boolean,
    isVisible: Boolean,
    editedImage: Uri?,
    shouldSendEditedImageUri: Boolean,
    isUiState: Boolean,
    textColor: Color,
    shouldExpandTools: Boolean,
    navigator: Navigator,
    backgroundColor: Color,
    uiState: HomeUiState,
    editImageUistate: EditImageUiState


) {
    AnimatedContent(hasPhotoImported, label = "ImportedPhotoAnimation") { targetState ->
       var bitmap: Bitmap? = null
        var savedColorMatrix: ColorMatrix =  ColorMatrix(SelectFilter(selectedFilter!!))

       if (importedImageUri !=null){
               bitmap= importedImageUri.toBitmap(LocalContext.current)

       }

        try {
            if (uiState.savedColorMatrix != null) savedColorMatrix = uiState.savedColorMatrix!!

        } catch (e: Exception) {
            e.stackTrace
        }



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
                            tint = textColor,
                            modifier = Modifier.size(168.dp)
                        )
                    }

                    Box(Modifier.padding(top = 8.dp, bottom = 8.dp)) {
                        Text(
                            text = stringResource(id = R.string.tap_anywhere_to_open_a_photo),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = textColor
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
                        //the preview
                        importedImageUri?.let {

                            AsyncImage(
                                model = it,
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .animateContentSize()
                                    .rotate(editImageUistate.rotateImageValue)
                                    .weight(1f),
                                colorFilter = ColorFilter.colorMatrix(
                                    savedColorMatrix
//                                    ColorMatrix(SelectFilter(selectedFilter!!))
                                )
                            )
                        }
                        //isVisible
                   if (isVisible){

                       Snackbar(
                           modifier = Modifier.padding(2.dp),

                           action = {

                               TextButton(onClick = {
                                   if (editedImage != null) {
                                       AccessImage.AccessGallery.AccessImage(context = context, editedImage)

                                   }
                                   else{
                                       Toast.makeText(context, "image is empty", Toast.LENGTH_SHORT).show()
                                   }
                               },


                               ) {
                                   Text(text = "View")


                               }

                           },





                       ) {
                           Text(text = "Photo Saved")

                       }

                   }



                        AnimatedVisibility(visible = shouldExpandLooks) {
                           var coroutineScope = rememberCoroutineScope()
//                            var sharedUri by remember { mutableStateOf<Uri?>(null) }
                            LooksBottomSheet {
//                                imageFilters.map { filter ->
//                                    Box(
//                                        Modifier.padding(4.dp),
//                                        contentAlignment = Alignment.Center
//                                    ) {
//                                        val toolColor by animateColorAsState(
//                                            targetValue = if (index == selectedFilter) Color.Blue.copy(
//                                                0.4f
//                                            ) else Color.Transparent,
//                                            label = "ToolColor"
//                                        )
//
//                                        Column(
//                                            modifier = Modifier.fillMaxWidth(),
//                                            horizontalAlignment = Alignment.CenterHorizontally
//                                        ) {
//                                            Box(Modifier
//                                                .border(
//                                                    width = 1.8.dp,
//                                                    shape = RectangleShape,
//                                                    color = toolColor,
//                                                )
//                                                .selectable(
//                                                    selected = true,
//                                                    onClick = {
//                                                        onEvent(
//                                                            HomeScreenEvent.UpdateFilter(
//                                                                index
//                                                            )
//                                                        )
//                                                    }
//                                                ), contentAlignment = Alignment.Center) {
//                                                importedImageUri?.let {
//                                                    val bitmap = it.toBitmap(LocalContext.current)
//                                                    bitmap?.let { image ->
//                                                        Image(
//                                                            bitmap = image.asImageBitmap(),
//                                                            contentDescription = null,
//                                                            contentScale = ContentScale.Crop,
//                                                            modifier = Modifier
//                                                                .width(77.dp)
//                                                                .height(90.dp),
//                                                            colorFilter = ColorFilter.colorMatrix(
//                                                                ColorMatrix(
//                                                                    SelectFilter(index)
//                                                                )
//                                                            )
//                                                        )
//
//                                                        LaunchedEffect(Unit) {
//                                                            onEvent(
//                                                                HomeScreenEvent.LoadImageFilters(
//                                                                    image
//                                                                )
//                                                            )
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                            Text(
//                                                text = filterName,
//                                                style = MaterialTheme.typography.labelSmall
//                                            )
//
//                                        }
//
//                                    }
//                                }
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

                                    val selectFilters: FloatArray =   SelectFilter(
                                        index = index )

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
                                                            HomeScreenEvent.UpdateFilterIndex(
                                                                index
                                                            )
                                                        )
                                                        coroutineScope.launch {
                                                            delay(500L)
                                                            onEvent(
                                                                HomeScreenEvent.UpdateFilter(
                                                                    bitmap = bitmap,
                                                                    uri = importedImageUri,
                                                                    colorFilterArray = selectFilters
                                                                )

                                                            )
                                                        }

                                                        onEvent(HomeScreenEvent.FilterSelected)


                                                    }
                                                ), contentAlignment = Alignment.Center) {


                                                    val colorMatrix =     ColorMatrix(
                                                        SelectFilter(index)
                                                    )
                                                    val colorFilter = ColorFilter.colorMatrix(
                                                        colorMatrix
                                                    )
                                                    bitmap?.let { image ->
                                                        Image(
                                                            bitmap = image.asImageBitmap(),
                                                            contentDescription = null,
                                                            contentScale = ContentScale.Crop,
                                                            modifier = Modifier
                                                                .width(77.dp)
                                                                .height(90.dp),
                                                            colorFilter = colorFilter
                                                        )




                                                    }

//                                                    onEvent(HomeScreenEvent.FilterSelectedForUse(importedImageUri,
//                                                        bitmap = bitmap!!, SelectFilter(
//                                                            index = selectedFilter!!)))






                                            }
                                            Text(
                                                text = filterName,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = textColor
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
    if (shouldExpandTools) {
        ToolsBottomSheet(
            background = backgroundColor,
            onDismissRequest = { onEvent(HomeScreenEvent.ToggleTools) },
            visible = shouldExpandTools,
            content = {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    LazyVerticalGrid (
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
                                            onEvent(HomeScreenEvent.SelectTool(it.id))
                                            onEvent(HomeScreenEvent.ToggleTools)
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
                                ToolItem(it, textColor)
                            }
                        }
                    }
                }
            }
        )

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