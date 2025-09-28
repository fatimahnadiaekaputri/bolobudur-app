package com.example.bolobudur.ui.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.bolobudur.ui.navigation.Screen

@Composable
fun BottomNavBar(
    navController: NavController
) {
    val items = listOf(
        Screen.Home,
        Screen.Borobudurpedia,
        Screen.Profile
    )

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    Surface (
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp).navigationBarsPadding()
    ){
        NavigationBar (
            containerColor = Color.White
        ){
            items.forEach { screen ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.title,
                            tint = if (currentRoute == screen.route)
                                        Color.White
                                   else Color.Black.copy(alpha = 0.5f)
                        )
                    },
                    label = { Text(screen.title) },
                    selected = currentRoute == screen.route,
                    onClick = {
                        if (currentRoute != screen.route) {
                            navController.navigate(screen.route) {
                                popUpTo(Screen.Home.route)
                                launchSingleTop = true
                            }
                        }
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.Black.copy(alpha = 0.5f),
                        selectedTextColor = Color(0xFF3469CA),
                        unselectedTextColor = Color.Black.copy(alpha = 0.5f),
                        indicatorColor = Color(0xFF3469CA)
                    )
                )
            }
        }
    }
}