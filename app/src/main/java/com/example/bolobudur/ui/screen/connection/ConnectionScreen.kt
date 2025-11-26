package com.example.bolobudur.ui.screen.connection

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.bolobudur.R

@Composable
fun ConnectionScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("OH TIDAK", style = MaterialTheme.typography.labelLarge)
        Text("Koneksimu Hilang", style = MaterialTheme.typography.headlineLarge)

        Spacer(Modifier.height(24.dp))

        Image(
            painter = painterResource(id = R.drawable.no_connection),
            contentDescription = null,
            modifier = Modifier.size(180.dp)
        )

        Spacer(Modifier.height(12.dp))

        Text(
            "Sambungkan kembali internetmu untuk mendapatkan pengalaman lebih optimal",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))
    }
}
