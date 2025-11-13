package com.example.bolobudur.ui.screen.borobudurpedia

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bolobudur.R

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun CategoryScreen(navController: NavController, categoryName: String) {

    // Daftar kategori (kamu bisa ubah nanti kalau ada data dinamis)
    val categories = listOf(
        Pair("Pondasi", R.drawable.bolomaps_feature),
        Pair("Kaki, Tubuh, Atap Candi", R.drawable.bolomaps_feature),
        Pair("Selasar, Tangga, Pagar Langkan", R.drawable.bolomaps_feature),
        Pair("Rupadhatu, Arupadhatu, Kamadhatu, Bhuvarloka, Bhurloka", R.drawable.bolomaps_feature),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // 🏷️ Header Title
        Text(
            text = categoryName,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2A2A2A)
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp, bottom = 24.dp)
        )

        // 📚 Grid kategori
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(categories) { (name, imageRes) ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    onClick = {
                        // nanti bisa navigate ke detail kategori di sini
                        // contoh: navController.navigate("category_detail/${name}")
                    }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF9F9F9))
                    ) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .height(120.dp)
                                .fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF333333)
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
