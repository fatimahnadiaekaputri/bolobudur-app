package com.example.bolobudur.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.FeatherIcons
import compose.icons.feathericons.BookOpen
import compose.icons.feathericons.Home
import compose.icons.feathericons.User

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", FeatherIcons.Home)
    object Borobudurpedia: Screen("borobudurpedia", "BOROBUDURpedia", FeatherIcons.BookOpen)
    object  Profile: Screen("profile", "Profil", FeatherIcons.User)

    object Category : Screen("category/{categoryName}", "Category", FeatherIcons.BookOpen)
}