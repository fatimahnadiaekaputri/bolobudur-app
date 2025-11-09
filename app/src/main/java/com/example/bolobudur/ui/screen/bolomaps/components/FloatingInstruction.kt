package com.example.bolobudur.ui.screen.bolomaps.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.bolobudur.ui.screen.bolomaps.NavigationViewModel
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowDown
import compose.icons.feathericons.ArrowLeft
import compose.icons.feathericons.ArrowRight
import compose.icons.feathericons.ArrowUp

@Composable
fun FloatingInstructionBox(
    instruction: String,
//    distanceText: String,
    bearing: Float,
    navigationViewModel: NavigationViewModel = hiltViewModel()
) {
    val directionText = navigationViewModel.detectTurn(bearing)
    val icon = when (directionText) {
        "Utara" -> FeatherIcons.ArrowUp
        "Timur" -> FeatherIcons.ArrowRight
        "Selatan" -> FeatherIcons.ArrowDown
        "Barat" -> FeatherIcons.ArrowLeft
        else -> FeatherIcons.ArrowUp
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp)
            .wrapContentHeight(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF071228)),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = directionText,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
//                    text = "$instruction ke arah $directionText sejauh $distanceText",
                    text = "$instruction ke arah $directionText",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
