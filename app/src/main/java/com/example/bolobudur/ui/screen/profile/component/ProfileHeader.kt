package com.example.bolobudur.ui.screen.profile.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.bolobudur.R

@Composable
fun ProfileHeader(
    name: String,
    email: String,
    imageUrl: String?,            // ⬅️ TAMBAH INI
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val imageModel = imageUrl ?: R.drawable.profil_ryujin

    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context)
                .data(imageModel)
                .crossfade(true)
                .build()
        ),
        contentDescription = "Foto Profil",
        modifier = modifier
            .size(100.dp)           // fixed size
            .clip(CircleShape),    // circle
        contentScale = ContentScale.Crop
    )


    Spacer(modifier = Modifier.height(12.dp))

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}
