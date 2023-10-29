package com.example.fotoeditor.ui.screens.editimagescreen

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Rect
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fotoeditor.FilterColors.SelectFilter
import com.example.fotoeditor.R
import com.example.fotoeditor.ui.components.AutoTune.AutoTune.autoTuneImage
import com.example.fotoeditor.ui.components.CropSheet
import com.example.fotoeditor.ui.components.EditImageBottomBar
import com.example.fotoeditor.ui.components.TuneImageDialog
import com.example.fotoeditor.ui.nav.Navigator
import com.example.fotoeditor.ui.nav.Screen
import com.example.fotoeditor.ui.screens.homescreen.HomeScreenEvent
import com.example.fotoeditor.ui.screens.homescreen.HomeScreenViewModel
import com.example.fotoeditor.ui.utils.Crops
import com.example.fotoeditor.ui.utils.CropsLibrary
import com.example.fotoeditor.ui.utils.Event
import com.example.fotoeditor.ui.utils.toBitmap
import java.io.File
import kotlin.math.roundToInt

@SuppressLint("UseCompatLoadingForDrawables")
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
                     showCropOption = showCropOption

                 )




        },
        bottomBar = {
            Box(
                Modifier
                    .background(Color.White)
                    .shadow(elevation = 1.dp)
            ) {

                        EditImageBottomBar(
                            save = {
                                IconButton(onClick = {
                                    //navigator.navigateTo(Screen.HomeScreen.route)

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
                    onEvent = onEvent
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
    onEvent: (Event) -> Unit
) {

    val context = LocalContext.current
    var cropBoxSize by remember { mutableStateOf(Pair(400f, 400f)) }
    val boxSize = 400.dp

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current.density
    val screenWidthInPixels = (configuration.screenWidthDp * density).dp
    val screenHeightInPixels = (configuration.screenHeightDp * density).dp

    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    var cropBoxPosition by remember { mutableStateOf(Pair(0f, 0f)) }

    var imageBitmap by remember {
        mutableStateOf(bitmap)
    }

    // Get the image's dimensions
    val imageWidth = imageBitmap?.width ?: 400f
    val imageHeight = imageBitmap?.height ?: 400f


// Set the maximum crop box size based on the image dimensions
    val maxCropBoxSize = Pair(imageBitmap?.width?.toFloat() ?: 200f, imageBitmap?.height?.toFloat() ?: 200f)


    var croppedImageUri by remember { mutableStateOf<Uri?>(null) }
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


                        ){



                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(400.dp)
                                    .width(400.dp)
                                    .graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                        translationX = offsetX
                                        translationY = offsetY
                                    },
                                contentScale = ContentScale.Fit,
                                colorFilter = colorFilter
                            )



                            Box(
                                modifier = Modifier
                                    .size(cropBoxSize.first.dp, cropBoxSize.second.dp)
                                    .offset{
                                        IntOffset(cropBoxPosition.first.roundToInt(),
                                            cropBoxPosition.second.roundToInt())
                                    }
                                    .pointerInput(Unit) {
                                        detectTransformGestures { _, pan, zoom, _ ->
                                            // // Update crop box size based on user's input
                                            cropBoxSize = Pair(cropBoxSize.first + pan.x, cropBoxSize.second + pan.y)


// Ensure that the crop box size stays within image boundaries
                                            cropBoxSize = Pair(
                                                cropBoxSize.first.coerceIn(0f, maxCropBoxSize.first),
                                                cropBoxSize.second.coerceIn(0f, maxCropBoxSize.second)
                                            )

                                        }
                                    }
                            ){
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .border(width = 1.dp, color = Color.White)
                                        .background(Color.White.copy(alpha = 0.6f))
                                        .align(Alignment.Center)

                                ) {
                                    // Draw the corners of the crop box for resizing

                                    val cornerSize = 16.dp
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .offset(0.dp, 0.dp)
                                            .background(Color.Black)
                                            .pointerInput(Unit) {
                                                detectTransformGestures { _, pan, _, panChange ->
                                                    cropBoxSize = Pair(cropBoxSize.first - pan.x, cropBoxSize.second - pan.y)
                                                    cropBoxPosition = Pair(cropBoxPosition.first + pan.x, cropBoxPosition.second + pan.y)
// Ensure corner resizing handles don't exceed image boundaries
                                                    cropBoxSize = Pair(
                                                        cropBoxSize.first.coerceIn(0f, maxCropBoxSize.first),
                                                        cropBoxSize.second.coerceIn(0f, maxCropBoxSize.second)
                                                    )


                                                }
                                            }
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .offset(cropBoxSize. 16.dp, 0.dp)
                                            .background(Color.Red)
                                            .pointerInput(Unit) {
                                                detectTransformGestures { _, pan, _, _ ->
                                                    cropBoxSize += (pan.x - pan.y).dp
                                                    cropBoxSize =
                                                        cropBoxSize.coerceIn(50.dp, 350.dp)

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
                                                        cropBoxSize.coerceIn(50.dp, 350.dp)
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
                                                        cropBoxSize.coerceIn(10.dp, 350.dp)
                                                }
                                            }
                                    )



                                }
                            }


                        }


                    if (uiState.showCropOption) {
                        CropSheet(crops = crops) {
                            when (it.id) {
                                1 -> {
                                    Toast.makeText(context, "free crop", Toast.LENGTH_SHORT).show()
                                    isCropping = true
                                }
                            }
                        }

                    }


                    Spacer(modifier = Modifier.weight(0.1f))

                if (uiState.isBitmapCropped) {

                  val croppedBitmap =  CropAndConvertToBitmap(imageUri,it,density,boxSize, cropBoxSize)

                        imageBitmap = croppedBitmap
                        onEvent(HomeScreenEvent.SendCroppedBitmap(croppedBitmap))
                        Toast.makeText(context, "Cropped Bitmap: $croppedBitmap", Toast.LENGTH_SHORT).show()

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

@Composable
fun CropAndConvertToBitmap(imageUri: Uri?, bitmap: Bitmap?,density: Float, boxSize: Dp, cropBoxSize: Dp): Bitmap {
           val context = LocalContext.current

//    val photoBitmap= bitmap!!.asImageBitmap()
    // Calculate the crop boundaries
    val cropLeft = (cropBoxSize / 2).coerceAtLeast(0.dp)
    val cropTop = (cropBoxSize / 2).coerceAtLeast(0.dp)
    val cropRight = (boxSize - cropBoxSize / 2).coerceAtLeast(0.dp)
    val cropBottom = (boxSize - cropBoxSize / 2).coerceAtLeast(0.dp)


   val photoBitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imageUri!!))
   photoBitmap.asImageBitmap()


    val croppedBitmap =Bitmap.createBitmap(
        ((cropRight - cropLeft).value * density).toInt(),
        ((cropBottom - cropTop).value * density).toInt(),
        Bitmap.Config.ARGB_8888
    )

    val srcRect: android.graphics.Rect? = Rect(
        (cropLeft * density).value.toInt(),
        (cropTop * density).value.toInt(),
        (cropRight * density).value.toInt(),
        (cropBottom * density).value.toInt()
    )
    val destRect = Rect(0, 0, (croppedBitmap.width), (croppedBitmap.height))


    val canvas = android.graphics.Canvas(croppedBitmap)
    canvas.drawBitmap(photoBitmap, srcRect, destRect, null)

    return croppedBitmap

//
//    Canvas(
//        modifier = Modifier
//            .background(Color.White)
//            .fillMaxSize()
//    ) {
//        drawIntoCanvas { canvas ->
//            // Calculate the scale factor to convert Dp to pixels
//            val density = density
//
//            // Draw the cropped portion of the source bitmap onto the canvas
//            canvas.nativeCanvas.drawBitmap(
//                photoBitmap.asAndroidBitmap(),
//                srcRect = srcRect,
//                destRect = destRect,
//                null
//            )
//
//        }
//    }

    // Calculate crop coordinates
//    val cropLeft = (cropBoxSize / 2).coerceAtLeast(0.dp)
//    val cropTop = (cropBoxSize / 2).coerceAtLeast(0.dp)
//    val cropRight = (boxSize - cropBoxSize / 2).coerceAtLeast(0.dp)
//    val cropBottom = (boxSize - cropBoxSize / 2).coerceAtLeast(0.dp)

//    // Calculate the width and height of the cropped area
//    val croppedWidth = (cropRight - cropLeft).value.toInt()
//    val croppedHeight = (cropBottom - cropTop).value
//
//    val scale = (photoBitmap.width / croppedWidth)
//
//    val painter = remember {
//        BitmapPainter(
//             image = photoBitmap ,
//            srcOffset = IntOffset((cropLeft * scale).value.toInt(), (cropTop * scale).value.toInt()),
//            srcSize = IntSize(photoBitmap.width.toInt() / scale, photoBitmap.height / scale)
//        )
//    }



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

