package com.example.bolobudur.ui.screen.bluetooth

import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
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
    val isScanning by viewModel.isScanning.collectAsState()
    val rawData by viewModel.rawData.collectAsState()

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
                Text("Data Bluetooth (raw):")
                Spacer(Modifier.height(8.dp))
                if (rawData.isNullOrEmpty()) {
                    Text("Menunggu data...")
                } else {
                    Text(rawData ?: "")
                    Log.d("BluetoothRawData", rawData ?: "")
                }
//                Text("Data Bluetooth (parsed):")
//                Spacer(Modifier.height(8.dp))
//                if (data == null) {
//                    Text("Menunggu data...")
//                } else {
//                    Text("ID: ${data!!.id}")
//                    Text("Latitude: ${data!!.latitude}")
//                    Text("Longitude: ${data!!.longitude}")
//                    Text("Kecepatan: ${data!!.speed}")
//                    Text("IMU: ${data!!.imu}")
//                    Text("Timestamp: ${data!!.timestamp}")
//                }

            }
        }
    }

    if (showDevicePopup) {
        DeviceSelectionDialog(
            devices = devices,
            onDismiss = { showDevicePopup = false },
            isLoading = isScanning,
            onDeviceSelected = { device ->
                viewModel.connectToDevice(device)
                showDevicePopup = false
            }
        )
    }
}