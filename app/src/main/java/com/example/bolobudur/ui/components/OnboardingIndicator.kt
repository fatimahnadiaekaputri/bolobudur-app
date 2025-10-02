package com.example.bolobudur.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.bolobudur.ui.theme.BluePrimary
import com.example.bolobudur.ui.theme.GrayLight

@Composable
fun OnboardingIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(pageCount) { index ->
            val color = if (index == currentPage) BluePrimary else GrayLight
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(if (index == currentPage) 12.dp else 8.dp) // current lebih besar
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
