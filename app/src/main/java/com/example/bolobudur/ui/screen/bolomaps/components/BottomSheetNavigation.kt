package com.example.bolobudur.ui.screen.bolomaps.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomSheetNavigation(
    remainingDistance: Double,
    turnInstruction: String
) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Instruksi: $turnInstruction", fontSize = 20.sp)
        Text("Sisa jarak: ${"%.1f".format(remainingDistance)} m", fontSize = 16.sp)
    }
}