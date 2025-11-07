package com.example.bolobudur.ui.screen.bolomaps.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import compose.icons.feathericons.Layers

@Composable
fun ExpendableMenu(
    title: String,
    items: List<PoiFeature>,
    viewModel: MapViewModel = hiltViewModel(),
    navigationViewModel: NavigationViewModel = hiltViewModel()
) {
    var expanded by remember { mutableStateOf(false) }

    val currentPos by navigationViewModel.currentPosition.collectAsState()

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = LocalIndication.current
                ) { expanded = !expanded }
                .padding(vertical = 5.dp)
                .clip(RoundedCornerShape(6.dp))
                .border(BorderStroke(1.dp, Color(0xFFD5D7DA)), shape = RoundedCornerShape(6.dp))
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = FeatherIcons.Layers, contentDescription = "Nama Lantai")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null
            )
        }

        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp)
            ) {
                items.forEach { item ->
                    TextButton(onClick = {
                        // misal titik asal tetap (posisi user)
                        val fromLat = currentPos?.latitude() ?: 0.0
                        val fromLon = currentPos?.longitude() ?: 0.0
                        val toLat = item.lat
                        val toLon = item.lon
                        val destinationLabel = item.label

                        viewModel.getShortestPath(fromLat, fromLon, toLat, toLon, destinationLabel)
                    }) {
                        Text(item.label, color = Color.Black, fontWeight = FontWeight.Light)
                        Spacer(modifier = Modifier.weight(1.5f))
                        Icon(
                            imageVector = FeatherIcons.ArrowRight,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.weight(0.5f)
                        )
                    }
                }
            }
        }
    }
}
