package com.example.fotoeditor.ui.screens.Settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fotoeditor.R
import com.example.fotoeditor.ui.screens.homescreen.HomeScreenViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun SettingScreen(viewModel: HomeScreenViewModel) {


    val themeManager = ThemeManager(LocalContext.current)
    val imageSizeManager = ImageSizeManager(LocalContext.current)
    val formatManager = FormatManager(LocalContext.current)

val selectedImageSize = imageSizeManager.getSelectedImageSize()
    val selectedFormat = formatManager.getSelectedFormat()

    var setImageSize:String? = null

    if (selectedImageSize.isNullOrEmpty()) {
        setImageSize = "Do not resize"
    } else {
        setImageSize = selectedImageSize
    }


    var setFormat:String? = null

    if (selectedFormat.isNullOrEmpty()) {
        setFormat= "JPG 100%"
    } else {
        setFormat = selectedFormat
    }

     var textColor: Color
     var isDialog by remember {
         mutableStateOf(false)
     }

    var isFormatDialog by remember {
        mutableStateOf(false)
    }
    var isDarkTheme  by remember { mutableStateOf(themeManager.getSelectedTheme() == ThemeManager.THEME_DARK) }


    val context = LocalContext.current
    AppTheme(
        isDarkTheme = isDarkTheme,
        content = {
            textColor = if (isDarkTheme) Color.White
            else Color.Black

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.settings)) },
                    backgroundColor = MaterialTheme.colors.background,
                    contentColor = textColor
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
                    fontWeight = FontWeight.Bold,
                    color = Color.Blue,
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier
                        .padding(bottom = 5.dp)

                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                ) {

                    Text(
                        text = "Dark theme",
                        fontSize = 16.sp,
                        color = textColor

                    )
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { checked ->
                            isDarkTheme = checked
                            val newTheme = if (checked) ThemeManager.THEME_DARK else ThemeManager.THEME_LIGHT
                            themeManager.setSelectedTheme(newTheme)

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
                        isDialog = !isDialog
                    }
                    .padding(bottom = 6.dp)) {
                    Text(
                        text = "Image sizing",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor

                    )
                    Text(
                        text = setImageSize,
                        fontSize = 14.sp,
                        color = textColor

                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(id = R.string.image_sizing_text),
                        fontSize = 14.sp,
                        color = textColor

                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = textColor)

                Spacer(modifier = Modifier.height(12.dp))
                Column(modifier = Modifier
                    .clickable {
                       isFormatDialog = !isFormatDialog
                    }
                    .padding(bottom = 6.dp)) {
                    Text(
                        text = "Format and quality",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor

                    )
                    Text(
                        text = setFormat,
                        fontSize = 14.sp,
                        color = textColor

                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(id = R.string.format_text),
                        fontSize = 14.sp,
                        color = textColor

                    )
                }
            }

        }
    })

    if (isDialog){
       DialogImageSizing(onDismiss = {isDialog = !isDialog},
         isDarkTheme
           )
    }

    if (isFormatDialog){
        DialogFormat(onDismiss = {isFormatDialog = !isFormatDialog},
            isDarkTheme
        )
    }
}


@Preview
@Composable
fun SettingsScreenPreview() {

    //SettingScreen(viewModel)

}







