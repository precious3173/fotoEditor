package com.example.fotoeditor.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

open class Navigator(private val navController: NavHostController) {
    @Composable
    open fun Navigate(route: String, pop: Boolean = false) {
        LaunchedEffect(true) {
            withContext(Dispatchers.Main) {
                with(navController) {
                    if (pop) popBackStack()
                    navigate(route = route)
                }
            }
        }
    }

    open fun navigateTo(route: String, pop: Boolean = false) {
        with(navController) {
            if (pop) popBackStack()
            navigate(route = route)
        }
    }

    @Composable
    open fun NavigateWithDelay(route: String, delay: Long, pop: Boolean = false) {
        LaunchedEffect(true) {
            withContext(Dispatchers.Main) {
                with(navController) {
                    delay(delay)
                    if (pop) popBackStack()
                    navigate(route = route)
                }
            }
        }
    }

    open fun pop() {
        navController.popBackStack()
    }
}