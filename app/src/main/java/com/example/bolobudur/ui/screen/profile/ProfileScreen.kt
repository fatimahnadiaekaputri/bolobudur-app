package com.example.bolobudur.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.bolobudur.ui.components.BottomNavBar
import com.example.bolobudur.ui.screen.profile.component.ProfileHeader
import com.example.bolobudur.ui.screen.profile.component.ProfileMenuItem

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    val navController = rememberNavController()
    ProfileScreen(navController = navController)
}

@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            BottomNavBar(navController)
        }
    ) {
            padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(padding)
        ){
            // Header
            ProfileHeader(
                name = "Hanifah",
                email = "hanifahputriarani@mail.ugm.ac.id"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Card menu
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
                        title = "Change Password",
                        showDivider = false,
                        onClick = { /* TODO */ }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            //logout
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                ProfileMenuItem(
                    icon = Icons.Default.Lock,
                    title = "Logout",
                    showDivider = false,
                    onClick = {
                        navController.navigate("login") {
                            popUpTo("profile") { inclusive = true }
                        }
                    }
                )
            }
        }

//        Box(
//            modifier = Modifier.padding(padding).fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            Text("Selamat datang di Profile")
//        }
    }
}