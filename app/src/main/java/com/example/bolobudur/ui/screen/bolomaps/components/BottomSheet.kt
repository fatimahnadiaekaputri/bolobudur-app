package com.example.bolobudur.ui.screen.bolomaps.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import com.example.bolobudur.ui.model.FloorData
import com.example.bolobudur.ui.screen.home.HomeViewModel

@Composable
fun BottomSheet(viewModel: HomeViewModel = hiltViewModel()) {
    val menuList = listOf(
        FloorData(
            id = 0,
            title = "Lantai Dasar",
            items = listOf("Selatan", "Timur", "Barat", "Utara")
        ),
        FloorData(
            id = 1,
            title = "Lantai 1",
            items = listOf("Selatan", "Timur", "Barat", "Utara")
        ),
        FloorData(
            id = 2,
            title = "Lantai 2",
            items = listOf("Selatan", "Timur", "Barat", "Utara")
        )
    )
    val uiState by viewModel.uiState.collectAsState()
    Column (
        modifier = Modifier
            .fillMaxWidth()
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
            onValueChange = viewModel::onSearchQueryChange
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuickButton("Area Stupa")
            QuickButton("Pintu Masuk")
            QuickButton("Pintu Keluar")
        }

        Spacer(modifier = Modifier.height(12.dp))

        menuList.forEach { menu ->
            ExpendableMenu(title = menu.title, items = menu.items)
        }
    }
}