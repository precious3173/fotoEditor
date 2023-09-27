package com.example.fotoeditor.ui.screens.Settings

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fotoeditor.R

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SettingScreen() {


    var isDarkMode by remember { mutableStateOf(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) }
   // var darkMode by remember { mutableStateOf(false) }

    val systemBarsColor = if (isSystemInDarkTheme()) {
        Color.DarkGray // Dark theme system bars color
    } else {
        Color.White // Light theme system bars color
    }



    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    Surface( modifier = Modifier
        .fillMaxSize()){


        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.settings)) },

                    )
            },

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Appearance",
                    fontSize = 14.sp,
                    color = Color.Blue,
                    modifier = Modifier
                        .padding(bottom = 5.dp)

                )
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp),) {

                    Text(
                        text = "Dark theme",
                        fontSize = 16.sp,
                        color = Color.White

                    )
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { checked ->
                            isDarkMode = checked
                            val mode = if (checked) AppCompatDelegate.MODE_NIGHT_YES
                            else AppCompatDelegate.MODE_NIGHT_NO
                            AppCompatDelegate.setDefaultNightMode(mode)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Blue,
                            uncheckedThumbColor = Color.White
                        ),

                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Export and Sharing options",
                    fontSize = 14.sp,
                    color = Color.Blue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(bottom = 15.dp)

                )
                Column(modifier = Modifier
                    .clickable {
                        Toast
                            .makeText(context, "hmm", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .padding(bottom = 6.dp)) {
                    Text(
                        text = "Image sizing",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White

                    )
                    Text(
                        text = "Do not resize",
                        fontSize = 14.sp,
                        color = Color.White

                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(id = R.string.image_sizing_text),
                        fontSize = 14.sp,
                        color = Color.White

                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color.White)

                Spacer(modifier = Modifier.height(12.dp))
                Column(modifier = Modifier
                    .clickable {
                        Toast
                            .makeText(context, "hmm", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .padding(bottom = 6.dp)) {
                    Text(
                        text = "Format and quality",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White

                    )
                    Text(
                        text = "JPG 95%",
                        fontSize = 14.sp,
                        color = Color.White

                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(id = R.string.format_text),
                        fontSize = 14.sp,
                        color = Color.White

                    )
                }
            }

        }
    }

}

@Preview
@Composable
fun SettingsScreenPreview() {

    SettingScreen()

}







