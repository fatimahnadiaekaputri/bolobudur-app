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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.bolobudur.R // Assuming your Borobudur image is in drawable/borobudur.png
import com.example.bolobudur.ui.auth.AuthViewModel
import kotlinx.coroutines.launch

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
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()

    // UI State dari ViewModel
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Navigasi otomatis saat login sukses
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

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
            modifier = Modifier.fillMaxSize(),
            alpha = 0.4f
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Logo dan tagline
            Column(
                modifier = Modifier.padding(24.dp)
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
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password*") },
                        placeholder = { Text("Masukkan password Anda") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                viewModel.login(email, password)
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF346CD3) // biru solid
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text("Masuk", fontSize = 16.sp, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Pesan error kalau gagal
                    if (!errorMessage.isNullOrEmpty()) {
                        Text(
                            text = errorMessage ?: "",
                            color = Color.Red,
                            fontSize = 14.sp
                        )
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
