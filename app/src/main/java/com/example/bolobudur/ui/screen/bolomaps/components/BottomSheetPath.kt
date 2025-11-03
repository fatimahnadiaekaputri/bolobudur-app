package com.example.bolobudur.ui.screen.bolomaps.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bolobudur.utils.toScreenHeight

@SuppressLint("DefaultLocale")
@Composable
fun BottomSheetPath(
    destinationLabel: String,
    totalDistance: Any,
    onStartNavigation: () -> Unit,
    onCancel: () -> Unit
) {
    val formattedDistance = String.format("%.0f m", totalDistance)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.33f.toScreenHeight())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Lokasi saat ini", fontSize = 16.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text(destinationLabel, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(formattedDistance, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onStartNavigation() }) {
            Text("Mulai menjelajah")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onCancel) {
            Text("Batal", color = Color.Red)
        }
    }
}