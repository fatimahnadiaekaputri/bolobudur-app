package com.example.bolobudur.ui.screen.bluetooth

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bolobudur.data.model.DeviceItem
import com.example.bolobudur.ui.components.Loader

@Composable
fun DeviceSelectionDialog(
    pairedDevices: List<DeviceItem>,
    scannedDevices: List<DeviceItem>,
    isScanning: Boolean,
    onDismiss: () -> Unit,
    onDeviceSelected: (DeviceItem) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pilih Perangkat Bluetooth (ESP32_BT)") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {

                // ---- Paired Section ----
                Text(
                    "Perangkat Terpasang (Paired)",
                    style = MaterialTheme.typography.titleMedium
                )
                if (pairedDevices.isEmpty()) {
                    Text("Tidak ada perangkat paired")
                } else {
                    LazyColumn {
                        itemsIndexed(pairedDevices) { _, d ->
                            DeviceRow(d, onDeviceSelected)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ---- Scanned Section ----
                Text(
                    "Perangkat Ditemukan (Scan)",
                    style = MaterialTheme.typography.titleMedium
                )

                if (isScanning) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Loader()
                    }
                }

                if (!isScanning && scannedDevices.isEmpty()) {
                    Text("Tidak ada perangkat ditemukan")
                } else {
                    LazyColumn {
                        itemsIndexed(scannedDevices) { _, d ->
                            DeviceRow(d, onDeviceSelected)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF3469CA) // warna tombol
                )
            ) {
                Text("Tutup")
            }
        },
        containerColor = Color.White // ubah background popup jadi putih
    )
}

@Composable
private fun DeviceRow(device: DeviceItem, onDeviceSelected: (DeviceItem) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current
            ) { onDeviceSelected(device) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(device.name ?: "Unknown")
            Text(device.address, style = MaterialTheme.typography.bodySmall)
        }
    }
}
