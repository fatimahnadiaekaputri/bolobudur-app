package com.example.bolobudur.ui.screen.borobudurpedia

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
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
import androidx.navigation.NavController
import com.example.bolobudur.R
import com.example.bolobudur.ui.components.BottomNavBar
import com.example.bolobudur.ui.components.FeatureCard
import com.example.bolobudur.ui.components.SearchBar
import com.example.bolobudur.ui.model.FeatureData
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun BorobudurpediaScreen(navController: NavController) {

    var searchQuery by remember { mutableStateOf("") }

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
//                        SearchBar(
//                            value = searchQuery,
//                            onValueChange = { searchQuery = it }
//                        )
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
                val features = listOf(
                    FeatureData(1, "Struktur Utama Candi", "", R.drawable.bolomaps_feature),
                    FeatureData(2, "Elemen Arsitektural", "", R.drawable.bolomaps_feature),
                    FeatureData(3, "Relief & Ornamen", "", R.drawable.bolomaps_feature),
                    FeatureData(4, "Arca & Simbol", "", R.drawable.bolomaps_feature)
                )

                val filtered = features.filter {
                    it.title.contains(searchQuery, ignoreCase = true)
                }

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    filtered.forEach { feature ->
                        FeatureCard(
                            feature = feature,
                            navController = navController,
                            onCardClick = {
                                val encodedTitle = URLEncoder.encode(feature.title, StandardCharsets.UTF_8.toString())
                                navController.navigate("category/$encodedTitle")

                            },
                            modifier = Modifier
                                .fillMaxWidth(0.47f)
                                .height(180.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}