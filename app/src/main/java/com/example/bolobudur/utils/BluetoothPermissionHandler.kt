package com.example.bolobudur.utils

import android.Manifest
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun BluetoothPermissionHandler(onAllGranted: () -> Unit) {
    val context = LocalContext.current

    val permissions = arrayOf(
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val denied = result.filterValues { !it }.keys
        if (denied.isEmpty()) {
            onAllGranted()
        } else {
            Toast.makeText(context, "Izin Bluetooth belum diberikan", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        val notGranted = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        if (notGranted.isNotEmpty()) launcher.launch(permissions)
        else onAllGranted()
    }
}

/** small dialog helper to ask user to open Bluetooth settings */
@Composable
fun OpenBluetoothSettingsDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Bluetooth belum aktif") },
        text = { Text("Silakan aktifkan Bluetooth di pengaturan agar dapat scan & connect.") },
        confirmButton = {
            TextButton(onClick = {
                context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                onDismiss()
            }) {
                Text("Buka Pengaturan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Tutup") }
        }
    )
}