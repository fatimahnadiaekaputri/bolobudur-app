package com.example.bolobudur.ui.screen.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bolobudur.R
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.bolobudur.ui.components.TopBar
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfileScreen(
    navController: NavController,
    viewModel: UpdateProfileViewModel = hiltViewModel(),
    onProfileUpdated: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(uiState.name, uiState.email) {
        name = uiState.name
        email = uiState.email
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Edit Profile",
                navController = navController
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🖼 FOTO PROFILE
            val imageModel = when {
                selectedImageUri != null -> selectedImageUri
                uiState.imageProfileUrl != null -> uiState.imageProfileUrl
                else -> R.drawable.default_profile
            }

            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data(imageModel)
                        .crossfade(true)
                        .build()
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable { launcher.launch("image/*") },
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(12.dp))
            Text(
                "Ganti Foto",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    launcher.launch("image/*")
                }
            )

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.updateProfile(
                        context = context,
                        name = name,
                        email = email,
                        imageUri = selectedImageUri
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Simpan Perubahan")
            }

            if (uiState.isLoading) {
                Spacer(Modifier.height(16.dp))
                CircularProgressIndicator()
            }

            if (uiState.isSuccess) {
                LaunchedEffect(Unit) {
                    onProfileUpdated()
                }
            }

            uiState.errorMessage?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

