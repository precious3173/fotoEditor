package com.example.fotoeditor.ui.nav

sealed class Screen(val route: String) {
    object SplashScreen : Screen("splash_screen")
    object HomeScreen : Screen("home_screen")
    object SettingsScreen : Screen("settings_screen")
    object ImageDetailScreen : Screen("image_detail_screen")
    object EditImageScreen : Screen("edit_image_screen/{toolId}")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route.replace("/{toolId}", ""))
            args.forEachIndexed { _, s -> append("/$s") }
        }
    }
}