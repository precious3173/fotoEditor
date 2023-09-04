package com.example.fotoeditor.ui.nav

import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fotoeditor.ui.screens.homescreen.HomeRoute
import com.example.fotoeditor.ui.screens.SplashScreen
import com.example.fotoeditor.ui.screens.editimagescreen.EditImageRoute
import com.example.fotoeditor.ui.screens.editimagescreen.EditImageViewModel
import com.example.fotoeditor.ui.screens.homescreen.HomeScreenViewModel

@Composable
fun NavigationController() {
    val TAG = "NavController"
    val navController = rememberNavController()
    val appNavigator = Navigator(navController = navController)

    val homeScreenViewModel = HomeScreenViewModel(LocalContext.current)
    val editImageViewModel = EditImageViewModel(LocalContext.current)

    NavHost(navController = navController, startDestination = Screen.SplashScreen.route) {
        fotoComposable(Screen.SplashScreen.route) {
            SplashScreen(navigator = appNavigator)
        }

        fotoComposable(Screen.HomeScreen.route) {
            HomeRoute(navigator = appNavigator, viewModel = homeScreenViewModel)
        }

        fotoComposable(
            route = Screen.EditImageScreen.route,
            arguments = listOf(navArgument("toolId") {
                type = NavType.StringType
            })
        ) {
            EditImageRoute(
                toolId = it.arguments?.getString("toolId"),
                homeScreenViewModel = homeScreenViewModel,
                editImageViewModel = editImageViewModel,
            )
        }
    }
}


private fun NavGraphBuilder.fotoComposable(
    route: String,
    arguments: List<NamedNavArgument> = listOf(),
    deepLinks: List<NavDeepLink> = listOf(),
    content: @Composable (NavBackStackEntry) -> Unit,
) {
    val offset = 300

    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { offset },
                animationSpec = tween(
                    durationMillis = offset,
                    easing = FastOutSlowInEasing,
                )
            ) + fadeIn(animationSpec = tween(offset))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -offset },
                animationSpec = tween(
                    durationMillis = offset,
                    easing = FastOutSlowInEasing,
                )
            ) + fadeOut(animationSpec = tween(offset))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -offset },
                animationSpec = tween(
                    durationMillis = offset,
                    easing = FastOutSlowInEasing,
                )
            ) + fadeIn(animationSpec = tween(offset))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { offset },
                animationSpec = tween(
                    durationMillis = offset,
                    easing = FastOutSlowInEasing,
                )
            ) + fadeOut(animationSpec = tween(offset))
        }
    ) {
        content.invoke(it)
    }
}