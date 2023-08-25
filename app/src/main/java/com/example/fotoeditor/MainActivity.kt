package com.example.fotoeditor

import android.os.Bundle
import android.window.SplashScreen
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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

@Composable
fun NavigationController() {
    val  navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash_screen"){

     composable("splash_screen"){
         splashScreen()
     }
        composable("main_screen"){
            
        }
    }
}

@Composable
fun splashScreen() {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

       Image(painter = painterResource(id = R.drawable.ic_baseline_photo_camera_24),
           contentDescription = "Photo Editor", modifier = Modifier.width(100.dp)
               .height(100.dp)

       )
     Text(text = "4to Editor", fontWeight = FontWeight.Medium, fontSize = 20.sp,
     color =  Color.Black, fontFamily = FontFamily.Default
     )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FotoEditorTheme {
       NavigationController()
    }
}