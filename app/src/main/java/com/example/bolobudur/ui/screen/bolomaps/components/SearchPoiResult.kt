package com.example.bolobudur.ui.screen.bolomaps.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.bolobudur.data.model.PoiFeature
import com.example.bolobudur.ui.screen.bolomaps.NavigationViewModel
import com.example.bolobudur.ui.screen.bolomaps.maps.MapViewModel

@Composable
fun SearchResultItem(
    item: PoiFeature,
    viewModel: MapViewModel = hiltViewModel(),
    navigationViewModel: NavigationViewModel = hiltViewModel()
) {
    val currentPos by navigationViewModel.currentPosition.collectAsState()

    TextButton(
        onClick = {
            val fromLat = currentPos?.latitude() ?: 0.0
            val fromLon = currentPos?.longitude() ?: 0.0
            viewModel.getShortestPath(fromLat, fromLon, item.lat, item.lon, item.label)
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(item.label, color = Color.Black)
    }
}
