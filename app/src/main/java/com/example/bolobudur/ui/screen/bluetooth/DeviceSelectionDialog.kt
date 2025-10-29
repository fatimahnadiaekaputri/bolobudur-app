package com.example.bolobudur.ui.screen.bluetooth

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bolobudur.data.model.DeviceItem
import com.example.bolobudur.ui.components.Loader

@Composable
fun DeviceSelectionDialog(
    devices: List<DeviceItem>,
    onDismiss: () -> Unit,
    isLoading: Boolean = false,
    onDeviceSelected: (DeviceItem) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pilih Perangkat Bluetooth") },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> {
                        Loader() // 🔹 pakai Loader dari component
                    }
                    devices.isEmpty() -> {
                        Text("Tidak ada perangkat terdeteksi. Pastikan Bluetooth aktif.")
                    }
                    else -> {
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            itemsIndexed(devices) { index, d ->
                                val interactionSource = remember { MutableInteractionSource() }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .clickable(
                                            interactionSource = interactionSource,
                                            indication = LocalIndication.current
                                        ) { onDeviceSelected(d) },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = d.name ?: "Unknown")
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = d.address, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                                if (index < devices.size - 1) Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Tutup") }
        }
    )
}
