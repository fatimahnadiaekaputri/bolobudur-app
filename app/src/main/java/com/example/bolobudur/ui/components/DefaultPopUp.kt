package com.example.bolobudur.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.unit.sp
import com.example.bolobudur.R

@Composable
fun DefaultPopup(
    visible: Boolean,
    onDismiss: () -> Unit = {},
    title: String = "Lihat Sekitar",
    description: String = "Ayo lihat apa yang ada di sekitarmu, dan pelajari lebih dekat!",
    imageRes: Int = R.drawable.sample_buddha,
    icon: ImageVector,
    onConnect: () -> Unit = {},
    onSkip: () -> Unit = onDismiss,
    showSkip: Boolean = true
) {
    if (visible) {
        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        Icon(
                            imageVector = icon,
                            contentDescription = "Icon tengah",
                            tint = Color.White,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(40.dp)
                                .background(
                                    color = Color.Black.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(50)
                                )
                                .padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onConnect,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B6EDC)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(48.dp)
                    ) {
                        Text("Oke", color = Color.White, fontSize = 16.sp)
                    }

                    if (showSkip) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Lewati",
                            color = Color.Gray,
                            modifier = Modifier.clickable { onSkip() }
                        )
                    }
                }
            }
        }
    }
}
