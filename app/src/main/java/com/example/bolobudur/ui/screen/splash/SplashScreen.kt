package com.example.bolobudur.ui.screen.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bolobudur.R

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    navController: NavController
) {
    val navigate by viewModel.navigateToNext.collectAsState()

    // pindah ke home setelah 2 detik
    LaunchedEffect(navigate) {
        if (navigate) {
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF071228), Color(0xFF346CD3))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.borobudur_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop, // auto crop tengah
            alpha = 0.4f // atur opacity sesuai kebutuhan
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.bolobudur_logo),
                contentDescription = "Logo Bolobudur",
                modifier = Modifier.size(125.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Lebih dari sekedar teman eksplorasi",
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}
