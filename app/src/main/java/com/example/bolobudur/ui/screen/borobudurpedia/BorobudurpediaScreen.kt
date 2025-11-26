package com.example.bolobudur.ui.screen.borobudurpedia

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bolobudur.R
import com.example.bolobudur.ui.components.BottomNavBar
import com.example.bolobudur.ui.components.FeatureCard
import com.example.bolobudur.ui.components.Loader
import com.example.bolobudur.ui.components.SearchBar
import com.example.bolobudur.ui.model.FeatureData


@Composable
fun BorobudurpediaScreen(navController: NavController, viewModel: BorobudurpediaViewModel = hiltViewModel()) {

    var searchQuery by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoadingCategories.collectAsState()
    val categories by viewModel.categories.collectAsState()

    var showOverlay by remember { mutableStateOf(false) }
    val searchedSites by viewModel.searchedSites.collectAsState()

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8F8F8)),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 🏛️ HEADER
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bolomaps_feature),
                        contentDescription = "Borobudur Background",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6f))
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 16.dp, vertical = 80.dp)
                            .zIndex(2f)
                    ) {
                        Text(
                            text = "BorobudurPedia",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "MCB Warisan Dunia Borobudur, Direktorat Jenderal Kebudayaan, " +
                                    "Kementerian Pendidikan, Kebudayaan, Riset, dan Teknologi",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                            .zIndex(3f)
                    ) {
                        SearchBar(
                            value = searchQuery,
                            onValueChange = {
                                searchQuery = it
                                viewModel.updateSearchQuery(it)
                                showOverlay = it.isNotBlank()
                            },
                            onSearchSubmit = {
                                viewModel.updateSearchQuery(searchQuery)
                                showOverlay = true
                            }
                        )

                    }
                }
            }

            // 🏷️ SECTION TITLE
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Categories",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2A2A2A)
                        )
                    )
                    Text(
                        text = "Berbagai kategori dari arsitektur candi yang ada",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF707070)
                        )
                    )
                }
            }

            // 📚 GRID (pakai FlowRow supaya ikut scroll parent)
            item {
//                val features = listOf(
//                    FeatureData(1, "Struktur Utama Candi", "", R.drawable.bolomaps_feature),
//                    FeatureData(2, "Elemen Arsitektural", "", R.drawable.bolomaps_feature),
//                    FeatureData(3, "Relief & Ornamen", "", R.drawable.bolomaps_feature),
//                    FeatureData(4, "Arca & Simbol", "", R.drawable.bolomaps_feature)
//                )
//
//                val filtered = features.filter {
//                    it.title.contains(searchQuery, ignoreCase = true)
//                }


                if (isLoading) {
                    Loader()
                } else {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        categories.forEach { feature ->
                            FeatureCard(
                                feature = FeatureData(
                                    id = feature.category_id,
                                    title = feature.name,
                                    description = "",
                                    imageRes = R.drawable.bolomaps_feature
                                ),
                                onCardClick = {
                                    navController.navigate("category/${feature.category_id}")
                                },
                                modifier = Modifier
                                    .fillMaxWidth(0.47f)
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }

        if (showOverlay) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 230.dp) // sesuaikan tinggi header image
            ) {
                androidx.compose.material3.Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = cardElevation(6.dp)
                ) {
                    when {
                        isLoading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Loader()
                            }
                        }

                        searchedSites.isEmpty() -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Tidak ditemukan")
                            }
                        }

                        else -> {
                            SearchSiteResultSheet(
                                sites = searchedSites,
                                onSiteClick = { site ->
                                    showOverlay = false

                                    // Kirim data
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
}