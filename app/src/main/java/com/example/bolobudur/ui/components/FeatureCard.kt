package com.example.bolobudur.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bolobudur.ui.model.FeatureData
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowUpRight

@Composable
fun FeatureCard(feature: FeatureData, navController: NavController, modifier: Modifier = Modifier, onCardClick: () -> Unit = {}) {
    Card(
        onClick = onCardClick,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box{
            Image(
                painter = painterResource(id = feature.imageRes),
                contentDescription = feature.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF414651).copy(alpha = 0.7f))
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = feature.title,
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                )
                Text(
                    text = feature.description,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f))
                )
            }
            IconButton(
                onClick = {navController.navigate("detail/${feature.id}")},
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .background(Color(0xFF3469CA), RoundedCornerShape(8.dp))
                    .size(36.dp)
            ) {
                Icon(FeatherIcons.ArrowUpRight, contentDescription = null, tint = Color.White)
            }
        }
    }
}