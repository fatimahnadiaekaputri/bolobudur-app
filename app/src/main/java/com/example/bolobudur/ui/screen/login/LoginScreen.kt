package com.example.bolobudur.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.bolobudur.R // Assuming your Borobudur image is in drawable/borobudur.png

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewLoginScreen() {
    com.example.bolobudur.ui.theme.BolobudurTheme {
        // navController dummy untuk preview
        val fakeNavController = rememberNavController()
        LoginScreen(navController = fakeNavController)
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Brush.verticalGradient(
            colors = listOf(Color(0xFF071228), Color(0xFF346CD3))
        )),
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.borobudur_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Logo dan tagline
            Column(
                modifier = Modifier
                    .padding(24.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bolobudur_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Bergabung Seperti Lainnya!",
                    fontSize = 20.sp,
                    color = Color.White
                )
                Text(
                    text = "Jadikan Setiap Kunjungan Bermakna",
                    fontSize = 14.sp,
                    color = Color.White
                )
            }

            // Card putih untuk form
            Surface(
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email*") },
                        placeholder = { Text("Masukkan alamat email Anda") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password*") },
                        placeholder = { Text("Masukkan password Anda") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            // TODO: logika login
                            navController.navigate("bluetooth") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF346CD3) // biru solid
                        )
                    ) {
                        Text("Masuk", fontSize = 16.sp, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(onClick = {
                        navController.navigate("register")
                    }) {
                        Text(
                            "Belum punya akun? Daftar",
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
