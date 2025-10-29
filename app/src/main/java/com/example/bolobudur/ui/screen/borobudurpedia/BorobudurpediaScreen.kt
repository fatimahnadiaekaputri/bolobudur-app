package com.example.bolobudur.ui.screen.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bolobudur.ui.components.BottomNavBar
import com.example.bolobudur.ui.navigation.Screen
import com.example.bolobudur.ui.screen.bluetooth.BluetoothScreen

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun BorobudurpediaScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            BottomNavBar(navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Selamat datang di Borobudurpedia")
            Spacer(modifier = Modifier.height(16.dp))
//            BluetoothScreen()
        }
    }
}
