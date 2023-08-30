package com.example.fotoeditor

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.BottomAppBar
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.Surface
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.fotoeditor.ui.nav.NavigationController
import com.example.fotoeditor.ui.theme.FotoEditorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FotoEditorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NavigationController()
                }
            }
        }
    }
}



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun BottomBar() {
    var context = LocalContext.current

    Scaffold(
        floatingActionButton = { FabCompose(context) },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = { BAB(context) }) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color(0xFFFFFCF3))
        ) {
        }
    }
}

@Composable
fun FabCompose(context: Context) {

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->

            if (isGranted) {
                Log.d(TAG, "Access granted")
            } else {
                Log.d(TAG, "Access  not granted")
            }
        }

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the

            imageUri = uri
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    FloatingActionButton(
        onClick = {

            when (PackageManager.PERMISSION_GRANTED) {

                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.READ_EXTERNAL_STORAGE
                ) -> {

                    // Launch the photo picker and let the user choose only images.
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }

                else -> {
                    launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }

        },
        interactionSource = remember {
            MutableInteractionSource()
        },
        containerColor = colorResource(id = R.color.orange),
        modifier = Modifier,
        shape = CircleShape,

        ) {
        Icon(Icons.Default.Add, contentDescription = null)
    }
}


@Composable
fun BAB(context: Context) {
    val selected = remember { mutableStateOf(BABIcons.MENU) }
    BottomAppBar(
        modifier = Modifier,
        backgroundColor = Color(0xFFFFFFFF),
        contentColor = contentColorFor(backgroundColor),
        cutoutShape = RoundedCornerShape(50),
        elevation = AppBarDefaults.BottomAppBarElevation,
        contentPadding = AppBarDefaults.ContentPadding,
        content = {
            Row(
                modifier = Modifier.fillMaxWidth(1f),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row() {
                    IconButton(onClick = {
                        selected.value = BABIcons.HOME
                    })
                    {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = null
                        )
                    }

                }
                Row() {
                    IconButton(onClick = {
                        selected.value = BABIcons.MENU
                    })
                    {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_menu_24),
                            contentDescription = null
                        )
                    }

                }
            }
        }
    )

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FotoEditorTheme {
        NavigationController()
    }

}