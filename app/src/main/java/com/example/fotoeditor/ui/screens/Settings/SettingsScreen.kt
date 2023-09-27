package com.example.fotoeditor.ui.screens.Settings

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import com.example.fotoeditor.R

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun SettingScreen() {

   // var isDarkTheme by remember { mutableStateOf(false) }
    val isSystemInDarkTheme = isSystemInDarkTheme()
     var textColor: Color
    var isDarkTheme  by remember { mutableStateOf(isSystemInDarkTheme) }


    val context = LocalContext.current
    AppTheme(
        isDarkTheme = isDarkTheme,
        content = {
            if (isDarkTheme) textColor = Color.White
            else textColor = Color.Black

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
                        color = textColor

                    )
                    Text(
                        text = "Do not resize",
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
                        Toast
                            .makeText(context, "hmm", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .padding(bottom = 6.dp)) {
                    Text(
                        text = "Format and quality",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor

                    )
                    Text(
                        text = "JPG 95%",
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
}


@Preview
@Composable
fun SettingsScreenPreview() {

    SettingScreen()

}







