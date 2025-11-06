package com.example.bolobudur.ui.screen.bolomaps.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.bolobudur.ui.components.SearchBar
import com.example.bolobudur.ui.screen.bolomaps.maps.MapViewModel
import com.example.bolobudur.utils.toScreenHeight

@Composable
fun BottomSheet(viewModel: MapViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val floorData by viewModel.floorData.collectAsState()
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    val listLoading by viewModel.listLoading.collectAsState()


    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching = uiState.searchQuery.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(
                min = 0.5f.toScreenHeight(),
                max = if (isSearching) 0.95f.toScreenHeight() else 0.75f.toScreenHeight()
//                max = 0.75f.toScreenHeight()
            )
            .verticalScroll(verticalScrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "List Lokasi yang Bisa Kamu Kunjungi",
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            fontSize = 18.sp,
        )

        Spacer(modifier = Modifier.height(8.dp))

        SearchBar(
            value = uiState.searchQuery,
            onValueChange = { query ->
                viewModel.onSearchQueryChange(query)
                viewModel.searchPoi(query)
            },
            onSearchSubmit = {
                viewModel.searchPoi(uiState.searchQuery)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isSearching) {
            when {
                listLoading -> {
                    Text(
                        text = "Mencari lokasi...",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                searchResults.isEmpty() -> {
                    Text(
                        text = "Tidak ditemukan hasil untuk \"${uiState.searchQuery}\"",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                else -> {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        searchResults.forEach { item ->
                            SearchResultItem(item = item, viewModel = viewModel)
                        }
                    }
                }
            }
        } else {
            // 🔹 Kalau gak search, tampilkan quick button + daftar lokasi
            Row(
                modifier = Modifier
                    .horizontalScroll(horizontalScrollState)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickButton("Area Stupa")
                QuickButton("Pintu Masuk")
                QuickButton("Pintu Keluar")
                QuickButton("Pintu Utara")
                QuickButton("Pintu Selatan")
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (floorData.isNotEmpty()) {
                floorData.forEach { floor ->
                    ExpendableMenu(title = floor.title, items = floor.items)
                }
            } else {
                Text(
                    text = "Data lokasi belum tersedia...",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }

    }
}
