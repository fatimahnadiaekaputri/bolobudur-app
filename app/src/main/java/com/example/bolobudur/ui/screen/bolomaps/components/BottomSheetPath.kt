package com.example.bolobudur.ui.screen.bolomaps.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bolobudur.utils.toScreenHeight
import compose.icons.FeatherIcons
import compose.icons.feathericons.Crosshair

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
            .height(0.35f.toScreenHeight())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.width(28.dp)
            ) {
                Icon(
                    imageVector = FeatherIcons.Crosshair,
                    contentDescription = null,
                    tint = Color(0xFF3469CA),
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .background(Color.LightGray, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Spacer(modifier = Modifier.height(6.dp))

                Icon(
                    imageVector = Icons.Outlined.Place,
                    contentDescription = null,
                    tint = Color(0xFF3469CA),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text("Lokasi saat ini", fontSize = 16.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.LightGray)
                        .padding(vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(destinationLabel, fontSize = 18.sp, color = Color.Gray)
                Text(
                    formattedDistance,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onStartNavigation,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3469CA)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Mulai menjelajah", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onCancel,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Batal", color = Color.Gray, fontSize = 14.sp)
        }
    }
}
