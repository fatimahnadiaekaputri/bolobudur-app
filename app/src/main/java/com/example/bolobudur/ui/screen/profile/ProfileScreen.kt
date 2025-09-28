package com.example.bolobudur.ui.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.bolobudur.ui.components.BottomNavBar
import com.example.bolobudur.ui.navigation.Screen

@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            BottomNavBar(navController)
        }
    ) {
            padding ->
        Box(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Selamat datang di Profile")
        }
    }
}