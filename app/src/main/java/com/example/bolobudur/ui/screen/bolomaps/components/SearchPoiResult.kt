package com.example.bolobudur.ui.screen.bolomaps.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.bolobudur.data.model.PoiFeature
import com.example.bolobudur.ui.screen.bolomaps.NavigationViewModel
import com.example.bolobudur.ui.screen.bolomaps.maps.MapViewModel
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowRight

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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.label,
                color = Color.Black,
                fontWeight = FontWeight.Light,
                fontSize = 13.sp
            )
            Icon(
                imageVector = FeatherIcons.ArrowRight,
                contentDescription = null,
                tint = Color.Black
            )
        }
    }
}
