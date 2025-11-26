package com.example.bolobudur.ui.screen.borobudurpedia

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.bolobudur.ui.components.Loader
import com.example.bolobudur.ui.components.TopBar
import compose.icons.FeatherIcons
import compose.icons.feathericons.ChevronLeft
import compose.icons.feathericons.ChevronRight

@Composable
fun CulturalSiteScreen(
    navController: NavController,
    viewModel: BorobudurpediaViewModel = hiltViewModel(),
    siteId: Int? = null
) {
    val site by viewModel.site.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val context = LocalContext.current

    val savedStateHandle = navController.previousBackStackEntry?.savedStateHandle
    val initialDescription = savedStateHandle?.get<String>("description") ?: "Tidak ada deskripsi."

    val description by remember(currentPage) {
        derivedStateOf {
            viewModel.getCurrentDescription()
        }
    }

    val title = savedStateHandle?.get<String>("title")
    val imageUrl = savedStateHandle?.get<String>("imageUrl")

//    LaunchedEffect(true) {
//        viewModel.loadDummyData()
//    }

    LaunchedEffect(siteId) {
        // Load data yang dikirim dari Bolofind
        viewModel.loadSite(
            name = title ?: "Tanpa Judul",
            description = initialDescription,
            imageUrl = imageUrl
        )
    }

    site?.let { siteData ->
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(context)
                .data(siteData.imageUrl)
                .crossfade(true)
                .build()
        )
        val painterState = painter.state

        Box(modifier = Modifier.fillMaxSize()) {
            // Background image (selalu dirender)
            Image(
                painter = painter,
                contentDescription = siteData.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Overlay placeholder/error di tengah
            if (painterState is coil.compose.AsyncImagePainter.State.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Loader()
                }
            } else if (painterState is coil.compose.AsyncImagePainter.State.Error) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(android.R.drawable.ic_menu_report_image),
                        contentDescription = "Error",
                        modifier = Modifier.size(100.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // Transparent reusable TopBar
            TopBar(
                title = "Kembali",
                navController = navController,
                isTransparent = true
            )

            // Bottom floating content
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .background(Color(0xFF071228).copy(alpha = 0.9f), RoundedCornerShape(20.dp))
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = siteData.name ?: "",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = description,
                        color = Color.White,
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = { viewModel.prevPage() },
                            enabled = viewModel.hasPrev(),
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (viewModel.hasPrev()) Color(0xFF3B82F6)
                                    else Color(0xFF444B52)
                                )
                        ) {
                            Icon(
                                imageVector = FeatherIcons.ChevronLeft,
                                contentDescription = "Previous",
                                tint = Color.White
                            )
                        }

                        IconButton(
                            onClick = { viewModel.nextPage() },
                            enabled = viewModel.hasNext(),
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (viewModel.hasNext()) Color(0xFF3B82F6)
                                    else Color(0xFF444B52)
                                )
                        ) {
                            Icon(
                                imageVector = FeatherIcons.ChevronRight,
                                contentDescription = "Next",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

