package com.example.fotoeditor.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fotoeditor.R
import com.example.fotoeditor.ui.components.SimpleTopAppBar
import com.example.fotoeditor.ui.utils.HomeMenuDefaults

@Composable
fun HomeRoute() {
    Scaffold(
        content = {
            HomeScreen(modifier = Modifier.padding(it))
        }
    )
}

@Composable
private fun HomeScreen(modifier: Modifier = Modifier) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(
                start = 16.dp,
                end = 16.dp
            )
            .then(modifier),
        contentAlignment = Alignment.TopCenter,
    ) {
        HomeScreenContent({/*todo() -> import photo*/ })

        SimpleTopAppBar(
            navigationIcon = {
                TextButton({/*todo()*/ }) {
                    Text(
                        text = stringResource(id = R.string.open),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            },
            actions = {
                HomeMenuDefaults.menus.forEachIndexed { _, homeMenuItem ->
                    if (homeMenuItem.visible) {
                        Box(
                            Modifier.padding(
                                top = 8.dp,
                                bottom = 8.dp,
                                start = 12.dp,
                            )
                        ) {
                            IconButton({/*todo() -> options menu items */ }) {
                                Icon(
                                    painterResource(id = homeMenuItem.icon),
                                    contentDescription = homeMenuItem.contentDesc,
                                    modifier = Modifier.semantics {
                                        contentDescription = homeMenuItem.contentDesc
                                    }
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun HomeScreenContent(
    importPhoto: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .clickable(
                enabled = true,
                onClick = importPhoto,
                role = Role.Button,
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.padding(
                top = 8.dp, bottom = 8.dp
            )
        ) {
            Icon(
                painterResource(id = R.drawable.icon_add_circle),
                contentDescription = null,
                modifier = Modifier.size(168.dp)
            )
        }

        Box(Modifier.padding(top = 8.dp, bottom = 8.dp)) {
            Text(
                text = stringResource(id = R.string.tap_anywhere_to_open_a_photo),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
@Preview
private fun HomeScreenPreview() {
    HomeScreen()
}