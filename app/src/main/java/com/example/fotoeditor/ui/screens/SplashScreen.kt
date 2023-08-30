package com.example.fotoeditor.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.fotoeditor.R
import com.example.fotoeditor.ui.nav.Navigator
import com.example.fotoeditor.ui.nav.Screen
import com.example.fotoeditor.ui.utils.SPLASH_SCREEN_DELAY

@Composable
fun SplashScreen(navigator: Navigator) {
    navigator.NavigateWithDelay(
        route = Screen.HomeScreen.route,
        delay = SPLASH_SCREEN_DELAY,
        pop = true
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_baseline_photo_camera_24),
            contentDescription = "Photo Editor", modifier = Modifier
                .width(80.dp)
                .height(80.dp)
        )
//        Text(
//            text = "4to Editor", fontWeight = FontWeight.Medium, fontSize = 30.sp,
//            color = Color.Black, fontFamily = FontFamily.Default
//        )
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

