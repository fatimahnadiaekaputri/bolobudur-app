package com.example.bolobudur.ui.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.bolobudur.ui.components.BottomNavBar
import com.example.bolobudur.ui.screen.profile.component.ProfileHeader
import com.example.bolobudur.ui.screen.profile.component.ProfileMenuItem
import com.example.bolobudur.ui.screen.profile.ProfileViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bolobudur.ui.auth.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val userProfile by viewModel.userProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Header Profil (nama dan email)
            ProfileHeader(
                name = userProfile?.name ?: "Guest",
                email = userProfile?.email ?: "-"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column {
                    ProfileMenuItem(
                        icon = Icons.Default.Person,
                        title = "Edit Profile",
                        onClick = { /* TODO */ }
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.Lock,
                        title = "Change Password", showDivider = false,
                        onClick = { /* TODO */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Menu Logout
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                ProfileMenuItem(
                    title = "Keluar",
                    icon = Icons.Default.Lock,
                    textColor = Color.Red,
                    onClick = {
                        scope.launch {
                            viewModel.logout {
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        }
                    }
                )
            }

            if (isLoading) {
                CircularProgressIndicator(
                    color = Color(0xFF346CD3),
                    modifier = Modifier.padding(16.dp)
                )
            }

            if (!errorMessage.isNullOrEmpty()) {
                Text(
                    text = errorMessage ?: "",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
