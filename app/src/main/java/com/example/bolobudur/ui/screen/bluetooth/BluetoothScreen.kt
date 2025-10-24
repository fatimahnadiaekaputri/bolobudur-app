package com.example.bolobudur.ui.screen.bluetooth

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.bolobudur.data.model.DeviceItem
import com.example.bolobudur.utils.BluetoothPermissionHandler
import com.example.bolobudur.utils.OpenBluetoothSettingsDialog

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun BluetoothScreen(viewModel: BluetoothViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val isEnabled by viewModel.isBluetoothEnabled.collectAsState()
    val devices by viewModel.devices.collectAsState()
    val connectedDevice by viewModel.connectedDevice.collectAsState()
    val data by viewModel.data.collectAsState()
    val error by viewModel.error.collectAsState()

    var showDialogOpenBluetooth by remember { mutableStateOf(false) }
    var showDevicePopup by remember { mutableStateOf(false) }

    // ask permissions first; when granted call check + load devices
    BluetoothPermissionHandler {
        viewModel.checkBluetoothEnabled()
        if (!viewModel.isBluetoothEnabled.value) {
            showDialogOpenBluetooth = true
        } else {
            viewModel.loadPairedDevices()
        }
    }

    if (showDialogOpenBluetooth) {
        OpenBluetoothSettingsDialog {
            showDialogOpenBluetooth = false
            viewModel.checkBluetoothEnabled()
            if (viewModel.isBluetoothEnabled.value) viewModel.loadPairedDevices()
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Bluetooth Receiver") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isEnabled) {
                Text("Bluetooth belum aktif")
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                }) {
                    Text("Buka Pengaturan Bluetooth")
                }
            } else {
                Spacer(Modifier.height(8.dp))
                if (connectedDevice == null) {
                    Text("Belum tersambung ke perangkat")
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = {
                        viewModel.loadPairedDevices()
                        showDevicePopup = true
                    }) {
                        Text("Scan / Pilih Perangkat")
                    }
                } else {
                    Text("Tersambung ke: ${connectedDevice?.name ?: "-"}")
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { viewModel.disconnect() }) {
                        Text("Disconnect")
                    }
                }

                Spacer(Modifier.height(24.dp))
                Text("Data Bluetooth (parsed):")
                Spacer(Modifier.height(8.dp))
                if (data == null) {
                    Text("Menunggu data...")
                } else {
                    Text("ID: ${data?.get("id")}")
                    Text("Latitude: ${data?.get("latitude")}")
                    Text("Longitude: ${data?.get("longitude")}")
                    Text("Kecepatan: ${data?.get("kecepatan")}")
                    Text("IMU: ${data?.get("imu")}")
                }

                error?.let {
                    Spacer(Modifier.height(12.dp))
                    Text("Error: $it", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    if (showDevicePopup) {
        DeviceSelectionDialog(
            devices = devices,
            onDismiss = { showDevicePopup = false },
            onDeviceSelected = { device ->
                viewModel.connectToDevice(device)
                showDevicePopup = false
            }
        )
    }
}