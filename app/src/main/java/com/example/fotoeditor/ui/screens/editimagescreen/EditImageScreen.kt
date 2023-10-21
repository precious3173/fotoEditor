package com.example.fotoeditor.ui.screens.editimagescreen

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.fotoeditor.FilterColors.SelectFilter
import com.example.fotoeditor.R
import com.example.fotoeditor.ui.components.AutoTune.AutoTune.autoTuneImage
import com.example.fotoeditor.ui.components.CropSheet
import com.example.fotoeditor.ui.components.EditImageBottomBar
import com.example.fotoeditor.ui.components.TuneImageDialog
import com.example.fotoeditor.ui.components.getScreenDimension
import com.example.fotoeditor.ui.nav.Navigator
import com.example.fotoeditor.ui.nav.Screen
import com.example.fotoeditor.ui.screens.homescreen.HomeScreenEvent
import com.example.fotoeditor.ui.screens.homescreen.HomeScreenViewModel
import com.example.fotoeditor.ui.utils.Crops
import com.example.fotoeditor.ui.utils.CropsLibrary
import com.example.fotoeditor.ui.utils.Event
import com.example.fotoeditor.ui.utils.toBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@SuppressLint("UseCompatLoadingForDrawables", "SuspiciousIndentation")
@Composable
fun EditImageRoute(
    toolId: String?,
    homeScreenViewModel: HomeScreenViewModel,
    editImageViewModel: EditImageViewModel,
    navigator: Navigator
) {
    val TAG = "EditImage"
    val uiState by editImageViewModel.uiState.collectAsStateWithLifecycle()
    val homeScreenUiState by homeScreenViewModel.uiState.collectAsStateWithLifecycle()
    val animatedProgress by animateFloatAsState(targetValue = uiState.progress, label = "AnimatedProgress")
    var showCropOption by remember { mutableStateOf( false)}

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->

            if (isGranted) {
                Log.d(ContentValues.TAG, "Access granted")
            } else {
                Log.d(ContentValues.TAG, "Access  not granted")
            }
        }

    val accessStorage: () -> Unit = {
        homeScreenViewModel.onEvent(HomeScreenEvent.AccessStorage{
                hasPermission ->
            if(hasPermission){
                Log.d(ContentValues.TAG, "Access granted")
                // Toast.makeText(context, "Access granted", Toast.LENGTH_SHORT).show()
            }
            else{
                launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                Log.d(ContentValues.TAG, "Access not granted")
            }
        })

    }

    toolId?.let {
        LaunchedEffect(Unit) {
            editImageViewModel.onEvent(EditImageEvent.UpdateToolId(it.toInt()))
            editImageViewModel.onEvent(EditImageEvent.UpdateImagePreview(homeScreenUiState.importedImageUri))
        }
    }



    Scaffold(
        topBar = {
            //PhotoEditProgressIndicator(progress = animatedProgress)
                 },
        content = {

                 EditImageScreen(
                     imageUri = uiState.imagePreview,
                     modifier = Modifier.padding(it),
                     onEvent = editImageViewModel::onEvent,
                     isTuneDialogVisible = uiState.isTuneDialogVisible,
                     uiState = uiState,
                     crops = CropsLibrary.crops,
                     showCropOption = showCropOption,
                     onHomeEvent = homeScreenViewModel::onEvent

                 )




        },
        bottomBar = {
            Box(
                Modifier
                    .background(Color.White)
                    .shadow(elevation = 1.dp)
            ) {
                     val coroutineScope = rememberCoroutineScope()
                        EditImageBottomBar(
                            save = {
                                IconButton(onClick = {
                                    navigator.navigateTo(Screen.HomeScreen.route)

                                    coroutineScope.launch {

                                        delay(4000)
                                        editImageViewModel.onEvent(EditImageEvent.FreeCropActivated(false))
                                    }


                                accessStorage()
                                    homeScreenViewModel.onEvent(HomeScreenEvent.SendEditedUri)
                                    try {
                                        homeScreenViewModel.onEvent(HomeScreenEvent.EditImageColorMatrix(
                                            uiState.editColorMatrix!!
                                        ))
                                    } catch (e: Exception){
                                        e.stackTrace
                                    }
                                    editImageViewModel.onEvent(EditImageEvent.ShouldSendCropped(true))
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.Gray,

                                    )
                                }
                            },
                            abort = {
                                IconButton(onClick = {
                               navigator.navigateTo(Screen.HomeScreen.route)

                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                    )
                                }
                            },
                            content = {
                                EditImageMode(
                                    selectToolId = uiState.selectedToolId,
                                    onEvent = editImageViewModel::onEvent,
                                    showCropOption = showCropOption,
                                    uiState = uiState
                                    )


                            }
                        )

            }
        },

    )


    if (uiState.isTuneDialogVisible){
        val context = LocalContext.current
        val imageUri = uiState.imagePreview!!
        val bitmap = imageUri.toBitmap(LocalContext.current)
   TuneImageDialog(
       visible = uiState.isTuneDialogVisible,
       onDismiss = {
           editImageViewModel.onEvent(EditImageEvent.UpdateTune(false))
       },
       onAdjustmentsChanged = { adjustments ->
           // Handle adjustments here
           println("Adjustments: $adjustments")
       },
       onEvent = editImageViewModel::onEvent,
       homeScreenViewModel = homeScreenViewModel,
       imageUri = imageUri,
       bitmap = bitmap!!

   )
    }

    if (uiState.isAutoTuneDialogVisible){
        val context = LocalContext.current
        val imageUri = uiState.imagePreview!!
        val bitmap = imageUri.toBitmap(LocalContext.current)
        val imageBitmap= loadImageFromUri(context = context, imageUri = imageUri )


        val adjustedImageBitmap = autoTuneImage(1.2f, 1.2f, 0.8f, homeScreenViewModel, imageUri = imageUri, bitmap = bitmap!!)
//         editImageViewModel.onEvent(EditImageEvent.UpdateAutoTuneBitmap(adjustedImageBitmap))
        if (adjustedImageBitmap != null) {
            editImageViewModel.onEvent(EditImageEvent.UpdateColor(adjustedImageBitmap))
        }

    }
}

@Composable
fun EditImageMode(
    selectToolId: Int,
    onEvent: (Event) -> Unit,
    showCropOption: Boolean,
    uiState: EditImageUiState
) {
    when(selectToolId){
        1 -> {
            onEvent(EditImageEvent.shouldShowCropOptions(
                false
            ))
            IconButton(onClick = {
             onEvent(EditImageEvent.UpdateTune(true))
                onEvent(EditImageEvent.UpdateAutoTune(false))
            }) {
                Icon(painterResource(id =  R.drawable.icon_tune_image)
                    , contentDescription = null,
                    tint = Color.Gray
                )
            }
            Spacer(modifier = Modifier.width(15.dp))
            IconButton(onClick = {
                onEvent(EditImageEvent.UpdateAutoTune(true))
                onEvent(EditImageEvent.UpdateTune(false))
            }) {
                Icon(painterResource(id =  R.drawable.baseline_auto_fix_normal_24)
                    , contentDescription = null,
                    tint = Color.Gray)
            }
        }

        2 ->{
            onEvent(EditImageEvent.shouldShowCropOptions(
                false
            ))
        }


        3 ->{


            onEvent(EditImageEvent.shouldShowCropOptions(
                true
            ))
            IconButton(onClick = {

                onEvent(EditImageEvent.shouldShowCropOptions(
                    !uiState.showCropOption
                ))
            }) {
                Icon(painterResource(id =  R.drawable.cropselected)
                    , contentDescription = null,
                    tint = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun EditImageScreen(
    imageUri: Uri?,
    modifier: Modifier = Modifier,
    onEvent: (Event) -> Unit,
    onHomeEvent: (Event) -> Unit,
    isTuneDialogVisible: Boolean,
    uiState: EditImageUiState,
    crops: List<Crops>,
    showCropOption: Boolean,
) {
    val TAG = "EditImage"
    Box(
        Modifier
            .fillMaxSize()
            .clickable {
//                onEvent(EditImageEvent.UpdateTune(false))
            }, contentAlignment = Alignment.Center) {



            imageUri?.let {


                EditImageContent(
                    bitmap = it.toBitmap(LocalContext.current),
                    imageUri = it,
                    uiState = uiState,
                    crops = crops,
                    showCropOption = showCropOption,
                    onEvent = onEvent,
                    onHomeEvent = onHomeEvent
                )








        }
    }

}



@SuppressLint("SuspiciousIndentation")
@Composable
private fun EditImageContent(
    bitmap: Bitmap?,
    imageUri: Uri?,
    modifier: Modifier = Modifier,
    uiState: EditImageUiState,
    crops: List<Crops>,
    showCropOption: Boolean,
    onEvent: (Event) -> Unit,
    onHomeEvent: (Event) -> Unit
) {

    val context = LocalContext.current



    val boxSize = 400.dp
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current.density
    val screenWidthInPixels = (configuration.screenWidthDp * density).dp
    val screenHeightInPixels = (configuration.screenHeightDp * density).dp

    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val cropBoxSizeDimension= getScreenDimension.ScreenDimension.getScreenSmallestDimensionDp(configuration, density)
    var croppedImageUri by remember { mutableStateOf<Uri?>(imageUri) }
    var croppedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var cropBoxSize by remember { mutableStateOf(cropBoxSizeDimension) }
    var imageBitmap by remember {
        mutableStateOf(bitmap)
    }

    var isCropping by remember { mutableStateOf(false) }
    var colorFilter = ColorFilter.colorMatrix(ColorMatrix(SelectFilter(index = 0)))



    if (uiState.editColorMatrix != null) {

        colorFilter = ColorFilter.colorMatrix(uiState.editColorMatrix)
    }



    val cropBorderColor = Color.White

    imageBitmap?.let {
        Box(
            Modifier
                .fillMaxSize()
                .then(modifier)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
//                Box(
//                    modifier =
//                    Modifier.fillMaxSize()
//                        .weight(0.9f)
//                        .background(Color.Black)
//                ) {

                        Box(
                            modifier = Modifier
                                .weight(0.9f)
                                .fillMaxSize()
                                .pointerInput(Unit) {

                                }


                        ){

                            onEvent(EditImageEvent.SendCroppedImage(croppedImageUri))

                            AsyncImage(
                                model = croppedImageUri,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Fit,
                                colorFilter = colorFilter
                            )


                            if (uiState.isFreeCrop){
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer(
                                            scaleX = scale,
                                            scaleY = scale,
                                            translationX = offsetX,
                                            translationY = offsetY
                                        )
                                        .pointerInput(Unit) {
                                            detectTransformGestures { _, pan, _, _ ->
                                                // Update offsetX and offsetY based on user's dragging
                                                offsetX += pan.x
                                                offsetY += pan.y
                                            }
                                        }
                                ){
                                    Box(
                                        modifier = Modifier
                                            .size(cropBoxSize)
                                            .background(Color.White.copy(alpha = 0.6f))
                                            .align(Alignment.Center)

                                    ) {
                                        // Draw the corners of the crop box for resizing
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .offset(0.dp, 0.dp)
                                                .background(Color.Black)
                                                .pointerInput(Unit) {
                                                    detectTransformGestures { _, pan, _, panChange ->
                                                        cropBoxSize += (pan.x - pan.y).dp
                                                        cropBoxSize =
                                                            cropBoxSize.coerceIn(
                                                                getScreenDimension.ScreenDimension.getScreenSmallestDimensionDp(
                                                                    configuration, density
                                                                ),
                                                                getScreenDimension.ScreenDimension.getScreenMaxDimensionDp(
                                                                    it.asImageBitmap(),
                                                                    density
                                                                )
                                                            )

                                                    }
                                                }
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .offset(cropBoxSize - 16.dp, 0.dp)
                                                .background(Color.Red)
                                                .pointerInput(Unit) {
                                                    detectTransformGestures { _, pan, _, _ ->
                                                        cropBoxSize += (pan.x - pan.y).dp
                                                        cropBoxSize =
                                                            cropBoxSize.coerceIn(
                                                                getScreenDimension.ScreenDimension.getScreenSmallestDimensionDp(
                                                                    configuration, density
                                                                ),
                                                                getScreenDimension.ScreenDimension.getScreenMaxDimensionDp(
                                                                    it.asImageBitmap(),
                                                                    density
                                                                )
                                                            )

                                                    }
                                                }
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .offset(0.dp, cropBoxSize - 16.dp)
                                                .background(Color.Yellow)
                                                .pointerInput(Unit) {
                                                    detectTransformGestures { _, pan, _, _ ->

                                                        cropBoxSize += (pan.x + pan.y).dp
                                                        cropBoxSize =
                                                            cropBoxSize.coerceIn(
                                                                getScreenDimension.ScreenDimension.getScreenSmallestDimensionDp(
                                                                    configuration, density
                                                                ),
                                                                getScreenDimension.ScreenDimension.getScreenMaxDimensionDp(
                                                                    it.asImageBitmap(),
                                                                    density
                                                                )
                                                            )
                                                    }
                                                }
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .offset(cropBoxSize - 16.dp, cropBoxSize - 16.dp)
                                                .background(Color.Green)
                                                .pointerInput(Unit) {
                                                    detectTransformGestures { _, pan, _, _ ->
                                                        cropBoxSize += (pan.x + pan.y).dp
                                                        cropBoxSize =
                                                            cropBoxSize.coerceIn(
                                                                getScreenDimension.ScreenDimension.getScreenSmallestDimensionDp(
                                                                    configuration, density
                                                                ),
                                                                getScreenDimension.ScreenDimension.getScreenMaxDimensionDp(
                                                                    it.asImageBitmap(),
                                                                    density
                                                                )
                                                            )
                                                    }
                                                }
                                        )



                                    }
                                }
                            }




                        }


                    if (uiState.showCropOption) {
                        CropSheet(crops = crops) {
                            when (it.id) {
                                1 -> {
                                    Toast.makeText(context, "free crop", Toast.LENGTH_SHORT).show()
                                    onEvent(EditImageEvent.FreeCropActivated(true))
                                }
                            }
                        }

                    }


                    Spacer(modifier = Modifier.weight(0.1f))

                if (uiState.isBitmapCropped) {


                    // Function to crop the image

                        val bitmap = loadCroppedImageFromUri(imageUri, context)
                        val offsetXPixels = (offsetX * density).toInt()
                        val offsetYPixels = (offsetY * density).toInt()
                        val cropBoxSizePixels = (cropBoxSize.value * density).toInt()

                        val croppedBitmap = cropBitmap(
                            bitmap = bitmap,
                            offsetX = offsetXPixels,
                            offsetY = offsetYPixels,
                            cropSize = cropBoxSizePixels
                        )
                        croppedImageUri = bitmapToUri(context, croppedBitmap, onHomeEvent)





//                  val croppedBitmap =  CropAndConvertToBitmap(imageUri,it,density,boxSize, cropBoxSize, offsetX, offsetY, onEvent)

//                        imageBitmap = croppedBitmap
//                    val croppedImageUri = bitmapToUri(context = context, bitmap = croppedBitmap)


                        Toast.makeText(context, "Cropped Uri: $croppedImageUri", Toast.LENGTH_SHORT).show()

                }
                }

            }

        }


//     else if (uiState.autoTuneBitmap != null){
//         Toast.makeText(LocalContext.current, "autotune working", Toast.LENGTH_SHORT).show()
//         Box(
//             Modifier
//                 .fillMaxSize()
//                 .then(modifier)
//         ) {
//
//             Image(
//                 bitmap = uiState.autoTuneBitmap!!,
//                 contentDescription = null,
//                 modifier = Modifier
//                     .fillMaxSize(),
//                 contentScale = ContentScale.Fit,
//             )
//
//         }
//
//     }
//
//    else{
//    bitmap?.let {
//        Box(
//            Modifier
//                .fillMaxSize()
//                .then(modifier)
//        ) {
//            Column( Modifier
//                .fillMaxSize(),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.SpaceBetween,) {
//
//
//                Image(
//                    bitmap = it.asImageBitmap(),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .fillMaxSize(),
//                    contentScale = ContentScale.Fit,
//                )
//                CropSheet(crops = CropsLibrary.crops)
//            }
//
//
//            }
//
//    }
//    }



    }

fun loadCroppedImageFromUri(imageUri: Uri?, context: Context): Bitmap? {
    val contentResolver = context.contentResolver
    val inputStream = contentResolver.openInputStream(imageUri!!)
    return BitmapFactory.decodeStream(inputStream)
}

fun cropBitmap(bitmap: Bitmap?, offsetX: Int, offsetY: Int, cropSize: Int): Bitmap {
    val croppedBitmap = Bitmap.createBitmap(
        cropSize,
        cropSize,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(croppedBitmap)
    canvas.drawBitmap(
        bitmap!!,
        -offsetX.toFloat(),
        -offsetY.toFloat(),
        null
    )
    return croppedBitmap
}

@Composable
fun CropAndConvertToBitmap(imageUri: Uri?, bitmap: Bitmap?,density: Float, boxSize: Dp, cropBoxSize: Dp, offsetX: Float, offsetY:Float, onEvent: (Event) -> Unit): Bitmap {
           val context = LocalContext.current

//    val photoBitmap= bitmap!!.asImageBitmap()
    // Calculate the crop boundaries
    val cropLeft = (cropBoxSize / 2).coerceAtLeast(0.dp)
    val cropTop = (cropBoxSize / 2).coerceAtLeast(0.dp)
    val cropRight = (boxSize - cropBoxSize / 2).coerceAtLeast(0.dp)
    val cropBottom = (boxSize - cropBoxSize / 2).coerceAtLeast(0.dp)


   val photoBitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imageUri!!))
   photoBitmap.asImageBitmap()

//    val cropLeft = (offsetX / density).coerceIn(0f,(photoBitmap.width - cropBoxSize.value)/ density)
//    val cropTop = (offsetY / density).coerceIn(0f, (photoBitmap.height - cropBoxSize.value)/ density)
//    val cropRight = (cropLeft + cropBoxSize.value / density)
//    val cropBottom = (cropTop + cropBoxSize.value / density)
//
//    val croppedWidth = (cropRight - cropLeft).toInt()
//    val croppedHeight = (cropBottom - cropTop).toInt()

//    val croppedBitmap =Bitmap.createBitmap(
//        croppedWidth,
//        croppedHeight,
//        Bitmap.Config.ARGB_8888
//    )

    val croppedBitmap =Bitmap.createBitmap(
        ((cropRight - cropLeft).value * density).toInt(),
        ((cropBottom - cropTop).value * density).toInt(),
        Bitmap.Config.ARGB_8888
    )

//
//    val srcRect: android.graphics.Rect? = Rect(
//        (cropLeft * density).toInt(), (cropTop * density).toInt(),
//        (cropRight * density).toInt(), (cropBottom * density).toInt()
//
//    )

    val srcRect: android.graphics.Rect? = Rect(
        (cropLeft * density).value.toInt(),
        (cropTop * density).value.toInt(),
        (cropRight * density).value.toInt(),
        (cropBottom * density).value.toInt()
    )
//    val destRect = Rect(0, 0, croppedWidth, croppedHeight)
    val destRect = Rect(0, 0, (croppedBitmap.width), (croppedBitmap.height))
    val canvas = android.graphics.Canvas(croppedBitmap)
    canvas.drawBitmap(photoBitmap, srcRect, destRect, null)


    return croppedBitmap


}




private fun loadCroppedImageUri(uri: Uri, context: Context): Bitmap {
    val contentResolver = context.contentResolver
    val inputStream = contentResolver.openInputStream(uri)
    return BitmapFactory.decodeStream(inputStream)
}


@Composable
fun loadImageFromUri(context: Context, imageUri: Uri?): Bitmap? {

        try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(imageUri!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            return bitmap
        } catch (e: Exception) {
            // Handle any exceptions that may occur during image loading
            e.stackTrace
        }
      return null
}


fun bitmapToUri(context: Context, bitmap: Bitmap, onEvent: (Event) -> Unit): Uri? {
    // Create a temporary file to save the Bitmap
    val file = File(context.cacheDir, "image.jpg")
    try {
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
    val croppedImageUri = Uri.fromFile(file)
    // Get the file's Uri

    return croppedImageUri

}