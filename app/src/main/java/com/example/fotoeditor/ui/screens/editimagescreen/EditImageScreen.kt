package com.example.fotoeditor.ui.screens.editimagescreen

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
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
import kotlin.math.min

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
    var isCropping by remember { mutableStateOf(false) }

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
                     isCropping = isCropping

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

                                    editImageViewModel.onEvent(EditImageEvent.ShouldSendCropped(!uiState.isBitmapCropped))
                                    editImageViewModel.onEvent(EditImageEvent.IsFreeMode(!uiState.isFreeMode))
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
                                    uiState = uiState,
                                    isCropping = isCropping
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
    uiState: EditImageUiState,
    isCropping: Boolean
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
    isCropping: Boolean
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
                    isCropping = isCropping
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
    isCropping: Boolean
) {

    val context = LocalContext.current
    var cropBoxWidth by remember { mutableStateOf(100) }
    var cropBoxHeight by remember { mutableStateOf(100) }




    val configuration = LocalConfiguration.current
    val density = LocalDensity.current.density
    val screenWidthInPixels = (configuration.screenWidthDp * density).dp
    val screenHeightInPixels = (configuration.screenHeightDp * density).dp

    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    var cropBoxRect by remember { mutableStateOf(Rect(0, 0, 0, 0)) }
    var imageBitmap by remember {
        mutableStateOf(bitmap)
    }
    var imageWidth by remember { mutableStateOf(100) }
    var imageHeight by remember { mutableStateOf(100) }


    var colorFilter = ColorFilter.colorMatrix(ColorMatrix(SelectFilter(index = 0)))

    var boxSize by remember {
        mutableStateOf(max(200.dp, 200.dp))
    }
    if (uiState.editColorMatrix != null) {

        colorFilter = ColorFilter.colorMatrix(uiState.editColorMatrix)
    }
    val aspectRatio = imageBitmap!!.width.toFloat() / imageBitmap!!.height.toFloat()


    val cropBorderColor = Color.White


            Column(
                Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
            ) {
//

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)


                        ){

                            val current = LocalDensity.current
                            imageBitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .onSizeChanged { layoutCoordinates ->
                                        imageWidth = layoutCoordinates.width
                                        imageHeight = layoutCoordinates.height
                                    }
                                    .aspectRatio(aspectRatio)
                                    .fillMaxSize()
                                    .background(Color.Black)
                                    .align(Alignment.Center),
                                contentScale = ContentScale.Fit,
                                colorFilter = colorFilter,


                            )


                            val maxSize = maxOf(imageWidth.dp, imageHeight.dp)

                            if (uiState.isFreeMode) {

                                Log.d(TAG, "the height is $cropBoxHeight and width is $cropBoxWidth")
                                    Box(
                                        modifier = Modifier
                                            .size(cropBoxHeight.dp, cropBoxWidth.dp)
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
                                                        val newCropBoxWidth =
                                                            (cropBoxWidth - pan.x).coerceIn(
                                                                10f,
                                                                imageWidth.toFloat()
                                                            )
                                                        val newCropBoxHeight =
                                                            (cropBoxHeight - pan.y).coerceIn(
                                                                10f,
                                                                imageHeight.toFloat()
                                                            )


                                                        // Ensure the crop box doesn't exceed the image boundaries
                                                        if (newCropBoxWidth <= imageWidth && newCropBoxHeight <= imageHeight
                                                            && newCropBoxWidth >= 10 && newCropBoxHeight >= 10
                                                        ) {
                                                            cropBoxWidth = newCropBoxWidth.toInt()
                                                            cropBoxHeight = newCropBoxHeight.toInt()
                                                            offsetX += (cropBoxWidth  - newCropBoxWidth)/2
                                                            offsetY += (cropBoxHeight - newCropBoxHeight)/2
                                                        }

                                                    }
                                                }
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .offset(cropBoxWidth.dp - 16.dp, 0.dp)
                                                .background(Color.Red)
                                                .pointerInput(Unit) {
                                                    detectTransformGestures { _, pan, _, _ ->
                                                        val newCropBoxWidth =
                                                            (cropBoxWidth - pan.x).coerceIn(
                                                                10f,
                                                                imageWidth.toFloat()
                                                            )
                                                        val newCropBoxHeight =
                                                            (cropBoxHeight - pan.y).coerceIn(
                                                                10f,
                                                                imageHeight.toFloat()
                                                            )


                                                        // Ensure the crop box doesn't exceed the image boundaries
                                                        if (newCropBoxWidth <= imageWidth && newCropBoxHeight <= imageHeight
                                                            && newCropBoxWidth >= 10 && newCropBoxHeight >= 10
                                                        ) {
                                                            cropBoxWidth = newCropBoxWidth.toInt()
                                                            cropBoxHeight = newCropBoxHeight.toInt()
                                                            offsetX += (cropBoxWidth  - newCropBoxWidth)/2
                                                            offsetY += (cropBoxHeight - newCropBoxHeight)/2
                                                        }
                                                    }
                                                }
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .offset(0.dp, cropBoxHeight.dp - 16.dp)
                                                .background(Color.Yellow)
                                                .pointerInput(Unit) {
                                                    detectTransformGestures { _, pan, _, _ ->

                                                        val newCropBoxWidth =
                                                            (cropBoxWidth - pan.x).coerceIn(
                                                                10f,
                                                                imageWidth.toFloat()
                                                            )
                                                        val newCropBoxHeight =
                                                            (cropBoxHeight - pan.y).coerceIn(
                                                                10f, imageHeight.toFloat()
                                                            )


                                                        // Ensure the crop box doesn't exceed the image boundaries
                                                        if (newCropBoxWidth <= imageWidth && newCropBoxHeight <= imageHeight
                                                            && newCropBoxWidth >= 10 && newCropBoxHeight >= 10
                                                        ) {
                                                            cropBoxWidth = newCropBoxWidth.toInt()
                                                            cropBoxHeight = newCropBoxHeight.toInt()
                                                            offsetX += (cropBoxWidth  - newCropBoxWidth)/2
                                                            offsetY += (cropBoxHeight - newCropBoxHeight)/2
                                                        }
                                                    }
                                                }
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .offset(cropBoxWidth.dp - 16.dp, cropBoxHeight.dp - 16.dp)
                                                .background(Color.Green)
                                                .pointerInput(Unit) {
                                                    detectTransformGestures { _, pan, _, _ ->
                                                        val newCropBoxWidth =
                                                            (cropBoxWidth + pan.x).coerceIn(
                                                                10f,
                                                                imageWidth.toFloat()
                                                            )
                                                        val newCropBoxHeight =
                                                            (cropBoxHeight + pan.y).coerceIn(
                                                                10f,
                                                                imageHeight.toFloat()
                                                            )


                                                        // Ensure the crop box doesn't exceed the image boundaries
                                                        if (newCropBoxWidth <= imageWidth && newCropBoxHeight <= imageHeight
                                                            && newCropBoxWidth >= 10 && newCropBoxHeight >= 10
                                                        ) {
                                                            cropBoxWidth = newCropBoxWidth.toInt()
                                                            cropBoxHeight = newCropBoxHeight.toInt()
                                                            offsetX += (cropBoxWidth  - newCropBoxWidth)/2
                                                            offsetY += (cropBoxHeight - newCropBoxHeight)/2
                                                        }
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
                                    onEvent(EditImageEvent.IsFreeMode(!uiState.isFreeMode))
                                }
                            }
                        }

                    }


                    Spacer(modifier = Modifier.weight(0.1f))

                if (uiState.isBitmapCropped) {


                    boxSize = max(400.dp, 400.dp)
                  val croppedBitmap =  CropAndConvertToBitmap(imageUri,imageBitmap,density,boxSize, cropBoxHeight.dp, cropBoxWidth.dp, offsetX, offsetY, cropBoxRect, imageHeight, imageWidth, aspectRatio)

                        imageBitmap = croppedBitmap
                        onEvent(HomeScreenEvent.SendCroppedBitmap(croppedBitmap))
                        Toast.makeText(context, "Cropped Bitmap: $croppedBitmap", Toast.LENGTH_SHORT).show()

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

@SuppressLint("SuspiciousIndentation")
@Composable
fun CropAndConvertToBitmap(
    imageUri: Uri?,
    bitmap: Bitmap?,
    density: Float,
    boxSize: Dp,
    cropBoxHeight: Dp,
    cropBoxWidth: Dp,
    offsetX: Float,
    offsetY: Float,
    cropRect: Rect,
    imageHeight: Int,
    imageWidth: Int,
    aspectRatio: Float
): Bitmap {
    val context = LocalContext.current
//   val photoBitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imageUri!!))
//   photoBitmap.asImageBitmap()
//    val photoBitmap= bitmap!!.asImageBitmap()
    // Calculate the crop boundaries
//    val cropLeft = (cropBoxSize / 2)
//    val cropTop = (cropBoxSize / 2)
//    val cropRight = (boxSize - cropBoxSize / 2)
//    val cropBottom = (boxSize - cropBoxSize / 2)



    val photoBitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imageUri!!))

   val boxWidth = imageWidth/2;                      //    ||                   ||
    val boxHeight = imageHeight/2;

//    // Calculate the left, top, right, and bottom coordinates for the crop rectangle
//    val left = (offsetX / imageWidth.toFloat()).toInt()
//    val top = (offsetY / imageHeight.toFloat()).toInt()
//    val right = left + cropBoxWidth.value
//    val bottom = top + cropBoxHeight.value
//
//
//    // Ensure the crop rectangle stays within the image boundaries
//    val cropLeft = max(0.dp, left.dp)
//    val cropTop = max(0.dp, top.dp)
//    val cropRight = min(imageWidth, right.toInt())
//    val cropBottom = min(imageHeight, bottom.toInt())




//    // Calculate the crop boundaries
//    val left = (offsetX * (imageWidth / boxSize.value)).toInt()
//    val top = (offsetY * (imageHeight / boxSize.value)).toInt()
//    val right = (left + cropBoxWidth.value * (imageWidth / boxSize.value)).toInt()
//    val bottom = (top + cropBoxHeight.value * (imageHeight / boxSize.value)).toInt()

    // Ensure the crop rectangle stays within the image boundaries
//    val cropLeft = max(0.dp, left.dp)
//    val cropTop = max(0.dp, top.dp)
//    val cropRight = min(imageWidth, right)
//    val cropBottom = min(imageHeight, bottom)

//    val cropRect = Rect(
//        cropLeft.value.toInt(),
//        cropTop.value.toInt(),
//        cropBottom,
//        cropRight
//    )

//    val croppedWidth = cropRight - cropLeft.value.toInt()
//    val croppedHeight = cropBottom - cropTop.value.toInt()

    val croppedBitmap = Bitmap.createBitmap(photoBitmap,cropBoxWidth.value.toInt(), cropBoxHeight.value.toInt(), boxWidth, boxHeight, null, false)
    val canvas = Canvas(croppedBitmap)
//    canvas.drawBitmap(
//        photoBitmap,
//        Rect(cropLeft.value.toInt(), cropTop.value.toInt(), cropRight, cropBottom),
//        Rect(0, 0, croppedWidth, croppedHeight),
//        null
//    )

    return croppedBitmap
//    val croppedBitmap = Bitmap.createBitmap(photoBitmap, left, top, right, bottom)
//    val canvas = Canvas(croppedBitmap)
//    canvas.drawBitmap(photoBitmap, matrix, null)
//val bitmaps = bitmap!!.asImageBitmap()

//    val cropX = 0
//    val cropY = 0
//
//    // Ensure that the crop box is within the image boundaries
////    val croppedX = cropX.coerceIn(0f, (bitmaps.width - cropBoxWidth.value))
////    val croppedY = cropY.coerceIn(0f, (bitmaps.height - cropBoxHeight.value))
//if (bitmaps.height == 0 || bitmap.width == 0){
//   Log.d(TAG, "bitmap height")
//}
//    if (cropBoxHeight.value.toInt() == 0){
//        Toast.makeText(context, "cropheight height", Toast.LENGTH_SHORT).show()
//    }
//   val cropBoxRect = Rect(
//        cropX,
//        cropY,
//        (cropX + cropBoxWidth.value).toInt(),
//        (cropY + cropBoxHeight.value).toInt()
//    )
//
//
//
//
//    val croppedBitmap = Bitmap.createBitmap(bitmaps.asAndroidBitmap(), cropBoxRect.left, cropBoxRect.top, cropBoxRect.width(), cropBoxRect.height())
//    // Calculate the position of the crop box within the image
//    var left = max(0.dp, min(offsetX.dp, (bitmap!!.width.dp - cropBoxWidth)))
//    val top = max(0.dp, min(offsetY.dp, (bitmap.height.dp - cropBoxHeight)))
//    val right = min(
//        bitmap.width.dp,
//        left + cropBoxWidth
//    )
//    val bottom = min(
//        bitmap.height.dp,
//        top + cropBoxHeight
//    )
//
//    // Calculate the dimensions of the crop box
//    val cropWidth = right - left
//    val cropHeight = bottom - top
//
//    if (cropWidth > 0.dp && cropHeight > 0.dp) {
//        val sourceRect = Rect(
//            left.value.toInt(),
//            top.value.toInt(),
//            right.value.toInt(),
//            bottom.value.toInt()
//        )
//    }
//
//        Canvas(modifier = Modifier.fillMaxSize()) {
//            drawIntoCanvas { canvas ->
//
//                // Draw the image
//                val scale = size.width / bitmap.width
//                canvas.drawImage(
//                    image = bitmap.asImageBitmap(),
//                    topLeftOffset = Offset.Zero,
//                    paint = Paint()
//                )
//
//                // Calculate the position of the crop box
//                val left = (size.width - cropBoxWidth.toPx()) / 2
//                val top = (size.height - cropBoxHeight.toPx()) / 2
//                val right = left + cropBoxWidth.toPx()
//                val bottom = top + cropBoxHeight.toPx()
//
//                // Draw the crop box
//                val cropBoxColor = Color(0x80000000)
//                drawRect(
//                    color = cropBoxColor,
//                    topLeft = Offset(0f, 0f),
//                    size = Size(size.width, top)
//                )
//                drawRect(
//                    color = cropBoxColor,
//                    topLeft = Offset(0f, top),
//                    size = Size(left, cropBoxHeight.toPx())
//                )
//                drawRect(
//                    color = cropBoxColor,
//                    topLeft = Offset(right, top),
//                    size = Size(size.width - right, cropBoxHeight.toPx())
//                )
//                drawRect(
//                    color = cropBoxColor,
//                    topLeft = Offset(0f, bottom),
//                    size = Size(size.width, size.height - bottom)
//                )
//
//            }
//
//        }
//        val croppedBitmap = bitmap.copy(sourceRect)


//
//    val cropLeft = (boxSize - cropBoxWidth) / 2
//    val cropTop = (boxSize - cropBoxHeight) / 2
//    val cropRight = cropLeft + cropBoxWidth
//    val cropBottom = cropTop + cropBoxHeight
//
//   val photoBitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imageUri!!))
//   photoBitmap.asImageBitmap()
//
//
//    val croppedBitmap =Bitmap.createBitmap(
//        ((cropRight - cropLeft).value * density).toInt(),
//        ((cropBottom - cropTop).value * density).toInt(),
//        Bitmap.Config.ARGB_8888
//    )
//
//    val srcRect: android.graphics.Rect? = Rect(
//        (cropLeft * density).value.toInt(),
//        (cropTop * density).value.toInt(),
//        (cropRight * density).value.toInt(),
//        (cropBottom * density).value.toInt()
//    )
//    val destRect = Rect(0, 0, (croppedBitmap.width), (croppedBitmap.height))
//
//
//    val canvas = android.graphics.Canvas(croppedBitmap)
//    canvas.drawBitmap(photoBitmap, srcRect, destRect, null)

//        return croppedBitmap

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

