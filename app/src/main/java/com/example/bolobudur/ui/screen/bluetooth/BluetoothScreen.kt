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
import androidx.navigation.NavController
import com.example.bolobudur.data.model.DeviceItem
import com.example.bolobudur.utils.BluetoothPermissionHandler
import com.example.bolobudur.utils.OpenBluetoothSettingsDialog

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun BluetoothScreen(
    navController: NavController,
    viewModel: BluetoothViewModel = hiltViewModel()
) {
    val ctx = LocalContext.current

    val isEnabled by viewModel.isBluetoothEnabled.collectAsState()
    val pairedDevices by viewModel.pairedDevices.collectAsState()
    val scannedDevices by viewModel.scannedDevices.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()

    val lat by viewModel.locationRepository.latitude.collectAsState()
    val lon by viewModel.locationRepository.longitude.collectAsState()
    val imu by viewModel.locationRepository.imu.collectAsState()

    var showChooseDevice by remember { mutableStateOf(false) }

    BluetoothPermissionHandler {
        viewModel.checkBluetoothEnabled()
        if (isEnabled) viewModel.loadDevices()
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Bluetooth") }) }) { pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isEnabled) {
                Text("Bluetooth off")
                Button({
                    ctx.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                }) { Text("Buka Bluetooth") }
                return@Column
            }

            Button(
                onClick = {
                    viewModel.loadDevices()
                    showChooseDevice = true
                }
            ) { Text("Pilih Device") }

            Spacer(Modifier.height(18.dp))
            Button(
                onClick = { viewModel.stopService(ctx) }
            ) { Text("Stop Service") }

            Spacer(Modifier.height(20.dp))
            Text("Latitude:  $lat")
            Text("Longitude: $lon")
            Text("IMU: $imu°")
        }
    }

    // ---- Device Selection Dialog ----
    if (showChooseDevice) {
        DeviceSelectionDialog(
            pairedDevices = pairedDevices,
            scannedDevices = scannedDevices,
            isScanning = isScanning,
            onDismiss = { showChooseDevice = false },
            onDeviceSelected = { device ->
                viewModel.startService(ctx, device.address, device.isDummy)
                showChooseDevice = false

                navController.navigate("home") {
                    popUpTo("bluetooth") { inclusive = true }
                }
            }
        )
    }
}

