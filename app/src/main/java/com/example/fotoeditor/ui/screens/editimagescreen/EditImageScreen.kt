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
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageView
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
import com.example.fotoeditor.ui.screens.homescreen.HomeUiState
import com.example.fotoeditor.ui.utils.Crops
import com.example.fotoeditor.ui.utils.CropsLibrary
import com.example.fotoeditor.ui.utils.Event
import com.example.fotoeditor.ui.utils.toBitmap
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.roundToInt


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
                     isCropping = isCropping,
                     homeUiState = homeScreenUiState,
                     homeScreenViewModel = homeScreenViewModel

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
                                    coroutineScope.launch {
                                        delay(100)

                                    navigator.navigateTo(Screen.HomeScreen.route)
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

                                    editImageViewModel.onEvent(EditImageEvent.ShouldSendCropped(!uiState.isBitmapCropped))
                                    editImageViewModel.onEvent(EditImageEvent.IsFreeMode(false))
                                //    editImageViewModel.onEvent(EditImageEvent.IsFreeMode(!uiState.isNotFreeMode))


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
                                    editImageViewModel.onEvent(EditImageEvent.IsFreeMode(false))

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
    var rotationState by remember { mutableStateOf(0f) }
    var bitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    val context = LocalContext.current

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

        4 -> {
            onEvent(EditImageEvent.shouldShowCropOptions(
                false
            ))

//            IconButton(onClick = {
//
//            }) {
//                Icon(painterResource(id =  R.drawable.baseline_flip_24)
//                    , contentDescription = null,
//                    tint = Color.Gray
//                )
//            }
//
//            Spacer(modifier = Modifier.width(15.dp))
            IconButton(onClick = {
                rotationState += 45f
                onEvent(EditImageEvent.ShouldRotateImage(
                     rotationState
                ))

                try {
                    val imageBitmap = uiState.imagePreview!!.toBitmap(context)
                    bitmap = SaveRotateBitmap.SaveRotateBitmap.rotateBitmap(
                        context, uiState.rotateImageValue,
                        imageBitmap!!
                    )
                    onEvent(EditImageEvent.SaveImageBitmap(bitmap
                    !!))
                }catch (e: Exception){
                  e.stackTrace
                }


            }) {
                Icon(painterResource(id =  R.drawable.icon_rotate)
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
    isCropping: Boolean,
    homeUiState: HomeUiState,
    homeScreenViewModel: HomeScreenViewModel
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
                    isCropping = isCropping,
                    homeUiState = homeUiState,
                    homeScreenViewModel = homeScreenViewModel
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
    isCropping: Boolean,
    homeUiState: HomeUiState,
    homeScreenViewModel: HomeScreenViewModel
) {

    val context = LocalContext.current
    var cropBoxWidth by remember { mutableStateOf(100) }
    var cropBoxHeight by remember { mutableStateOf(100) }

    val cropImageView = remember { CropImageView(context) }
    val coroutineScope = rememberCoroutineScope()

    var cropImage: ManagedActivityResultLauncher<CropImageContractOptions, CropImageView.CropResult>? = null


    val configuration = LocalConfiguration.current
    val density = LocalDensity.current.density
    val screenWidthInPixels = (configuration.screenWidthDp * density).dp
    val screenHeightInPixels = (configuration.screenHeightDp * density).dp
    var isCroppingFreeMode by remember {
        mutableStateOf(false)
    }

    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    var cropBoxRect by remember { mutableStateOf(Rect(0, 0, 0, 0)) }
    var imageBitmap by remember {
        mutableStateOf(bitmap)
    }
    var aspectRatio by remember {
        mutableStateOf(Pair(0, 0))
    }
    var rotationState by remember { mutableStateOf(0f) }
    var imageWidth by remember { mutableStateOf(100) }
    var imageHeight by remember { mutableStateOf(100) }

    var cropPosition by remember {
        mutableStateOf(0)
    }


    var colorFilter = ColorFilter.colorMatrix(ColorMatrix(SelectFilter(index = 0)))

    var boxSize by remember {
        mutableStateOf(max(200.dp, 200.dp))
    }
    if (uiState.editColorMatrix != null) {

        colorFilter = ColorFilter.colorMatrix(uiState.editColorMatrix)
    }
//    val aspectRatio = imageBitmap!!.width.toFloat() / imageBitmap!!.height.toFloat()


//    val cropBorderColor = Color.White


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



                            if (uiState.isFreeMode) {


                                Box(modifier =
                                Modifier.fillMaxSize()){
                                    AndroidView(
                                        modifier = Modifier
                                            .align(Alignment.Center),
                                        factory = {
                                                context ->

                                            cropImageView.apply {

                                                isAutoZoomEnabled = true
                                                setImageUriAsync(imageUri)
                                                setOnCropImageCompleteListener {
                                                        _, result ->


                                                }
                                                when(cropPosition){

                                                    3 ->{
                                                        setAspectRatio(1, 1)
                                                    }
                                                    4 ->{
                                                        setAspectRatio(1.4142.roundToInt(), 1)
                                                    }
                                                    5 ->{
                                                        setAspectRatio(3, 2)
                                                    }
                                                    6 ->{
                                                        setAspectRatio(5, 4)
                                                    }
                                                    7 ->{
                                                        setAspectRatio(7, 5)
                                                    }
                                                    8 ->{
                                                        setAspectRatio(16, 9)
                                                    }
                                                }

                                            }
                                        }
                                    )
                                }




                                val maxSize = maxOf(imageWidth.dp, imageHeight.dp)

                                cropImage = rememberLauncherForActivityResult(CropImageContract()) { result ->
                                    if (result.isSuccessful) {
                                        // Use the returned uri.
                                        val uriContent = result.uriContent
                                        val uriFilePath = result.getUriFilePath(context) // optional usage
                                    } else {
                                        // An error occurred.
                                        val exception = result.error
                                    }



                                    }


                            }
                                else{

                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .align(Alignment.Center)
                                        .background(Color.Black)
                                        .graphicsLayer(rotationZ = uiState.rotateImageValue),
                                    contentScale = ContentScale.Fit,
                                    colorFilter = colorFilter,


                                    )
                                }
                            }
                        }


                    if (uiState.showCropOption) {
                        CropSheet(crops = crops) {
                            cropPosition = it.id
                            when (it.id) {
                                1 -> {
                                    onEvent(EditImageEvent.IsFreeMode(!uiState.isFreeMode))


                                }
                                2 -> {

                                    onEvent(EditImageEvent.IsFreeMode(!uiState.isFreeMode))

                                }
                                3 -> {
                                    onEvent(EditImageEvent.IsFreeMode(!uiState.isFreeMode))
//                                    onEvent(EditImageEvent.IsNotFreeMode(!uiState.isNotFreeMode))

//                                    aspectRatio=(Pair(1, 1))

                                }
                                4 -> {
                                    onEvent(EditImageEvent.IsFreeMode(!uiState.isFreeMode))
//                                    onEvent(EditImageEvent.IsNotFreeMode(!uiState.isNotFreeMode))
//
//                                    aspectRatio = (Pair(1.4142.roundToInt(), 1))

                                }
                                5 -> {
                                    onEvent(EditImageEvent.IsFreeMode(!uiState.isFreeMode))
//                                    onEvent(EditImageEvent.IsNotFreeMode(!uiState.isNotFreeMode))


//                                    aspectRatio = (Pair(3, 2))

                                }
                                6 -> {
                                    onEvent(EditImageEvent.IsFreeMode(!uiState.isFreeMode))
//                                    onEvent(EditImageEvent.IsNotFreeMode(!uiState.isNotFreeMode))
//                                    aspectRatio = (Pair(5, 4))
                                }
                                7 -> {
                                    onEvent(EditImageEvent.IsFreeMode(!uiState.isFreeMode))
//                                    onEvent(EditImageEvent.IsNotFreeMode(!uiState.isNotFreeMode))
//                                    aspectRatio = (Pair(7, 5))
                                }
                                8 -> {
                                    onEvent(EditImageEvent.IsFreeMode(!uiState.isFreeMode))
//                                    onEvent(EditImageEvent.IsNotFreeMode(!uiState.isNotFreeMode))
//                                    aspectRatio = (Pair(16, 9))
                                }
                            }
                        }

                    }



                    Spacer(modifier = Modifier.weight(0.1f))





                if (uiState.isBitmapCropped) {
                    val croppedBitmap = cropImageView.getCroppedImage()
                    var savedColorArray: FloatArray? = null
                    if (homeUiState.savedColorArray == null){
                        savedColorArray = SelectFilter(
                            index = 0 )
                    }


                    try {
                        if (croppedBitmap != null){ imageBitmap = croppedBitmap
                        }
                        else{
                            imageBitmap = uiState.getBitmap
                        }

                        val uri =convertToUri(imageBitmap!!, context)
                       // onEvent(HomeScreenEvent.SendCroppedBitmap(imageBitmap))
                        if (uri != null
                        ){
                            homeScreenViewModel.onEvent(HomeScreenEvent.LoadImageUri(uri))
                            homeScreenViewModel.onEvent(HomeScreenEvent.updateEditColorFilterArray(savedColorArray!!,uri, imageBitmap))
//                       homeUiState.importedImageUri = uri
                            Log.d(TAG, "uri is not null")
                        }
                        else{
                            Log.d(TAG, "uri is null")
                        }

                    }
                    catch (e: Exception){
                        e.stackTrace
                    }




                }
                }
//
//    if (uiState.rotateImage){
//        Toast.makeText(context, "$rotationState", Toast.LENGTH_SHORT).show()
//        LaunchedEffect(key1 ="" ,){
//            coroutineScope.launch {
//                rotationState  += 45f
//                delay(1000)
//                !uiState.rotateImage
//            }
//        }


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


fun convertToUri(bitmap: Bitmap, context: Context): Uri?{

    val tempFile = File(context.cacheDir, "temp_image.jpg");
    try {
        val out = FileOutputStream(tempFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.flush();
        out.close();

        return Uri.fromFile(tempFile)
    } catch (e: IOException) {
        e.printStackTrace();

    }



    return null

}