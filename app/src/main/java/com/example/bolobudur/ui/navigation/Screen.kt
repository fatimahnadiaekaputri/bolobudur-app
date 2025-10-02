package com.example.bolobudur.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.FeatherIcons
import compose.icons.feathericons.BookOpen

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Borobudurpedia: Screen("borobudurpedia", "BOROBUDURpedia", FeatherIcons.BookOpen)
    object  Profile: Screen("profile", "Profil", Icons.Default.Person)
}