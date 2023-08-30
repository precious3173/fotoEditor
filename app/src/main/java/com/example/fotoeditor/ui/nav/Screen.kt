package com.example.fotoeditor.ui.nav

sealed class Screen(val route: String) {
    object SplashScreen : Screen("splash_screen")
    object HomeScreen : Screen("home_screen")
    object SettingsScreen : Screen("settings_screen")
    object ImageDetailScreen : Screen("image_detail_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEachIndexed { _, s -> append("/$s") }
        }
    }
}