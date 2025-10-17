package com.example.bolobudur.ui.screen.bolofind

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.bolobudur.ui.components.TopBar

@Composable
fun BolofindScreen(navController: NavController) {
    Scaffold(
        topBar = { TopBar(title = "BoloFind", navController = navController) }
    ) { padding ->
        Box(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Selamat datang di Bolofind")
        }
    }
}