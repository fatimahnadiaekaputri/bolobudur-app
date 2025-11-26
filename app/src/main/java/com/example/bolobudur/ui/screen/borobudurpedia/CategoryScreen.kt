package com.example.bolobudur.ui.screen.borobudurpedia

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bolobudur.R
import com.example.bolobudur.ui.components.FeatureCard
import com.example.bolobudur.ui.components.TopBar
import com.example.bolobudur.ui.model.FeatureData

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun CategoryScreen(
    navController: NavController,
    categoryId: Int,
    viewModel: BorobudurpediaViewModel = hiltViewModel()
) {

    val headerHeight = remember { mutableIntStateOf(0) }

    val isLoading by viewModel.isLoadingCategoriesAndSites.collectAsState()
    val category by viewModel.category.collectAsState()
    val sites by viewModel.sites.collectAsState()

    LaunchedEffect(categoryId) {
        viewModel.loadCategoryAndSites(categoryId)
    }

    // 🔹 STATE LOADING
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.CircularProgressIndicator()
        }
        return
    }

    // 🔹 JIKA SUDAH TIDAK LOADING → TAMPILKAN KONTEN
    Column(modifier = Modifier.fillMaxSize()) {

        Box(modifier = Modifier.fillMaxSize()) {

            // 🔹 BAGIAN HEADER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .onGloballyPositioned { layoutCoordinates ->
                        headerHeight.value = layoutCoordinates.size.height
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bolomaps_feature),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .matchParentSize()
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                )

                TopBar(
                    title = category?.name ?: "",
                    navController = navController,
                    isTransparent = true
                )

                Text(
                    text = category?.description ?: "",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                    modifier = Modifier
                        .padding(top = 90.dp, start = 12.dp, end = 16.dp, bottom = 20.dp)
                        .align(Alignment.BottomStart)
                )
            }

            // 🔹 KONTEN DI BAWAH HEADER
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = with(LocalDensity.current) { headerHeight.value.toDp() + 12.dp })
                    .background(Color.White)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(sites) { site ->
                        FeatureCard(
                            feature = FeatureData(
                                id = site.site_id,
                                title = site.name,
                                description = site.description,
                                imageUrl = site.image_url
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            onCardClick = {
                                navController.currentBackStackEntry?.savedStateHandle?.apply {
                                    set("title", site.name)
                                    set("description", site.description)
                                    set("imageUrl", site.image_url)
                                }
                                navController.navigate("culturalSite/${site.site_id}")
                            }
                        )
                    }
                }
            }
        }
    }
}



