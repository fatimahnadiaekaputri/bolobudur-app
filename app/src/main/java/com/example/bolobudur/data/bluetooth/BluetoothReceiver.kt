package com.example.bolobudur.data.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import java.util.UUID
import com.example.bolobudur.data.model.DeviceItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothReceiver @Inject constructor(
    private val context: Context,
    private val adapter: BluetoothAdapter?
) {
    private var socket: BluetoothSocket? = null
    private var inputStream: InputStream? = null

    // SPP UUID (ESP32 classic typical)
    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    fun isBluetoothEnabled(): Boolean = adapter?.isEnabled == true

    // expose adapter for ViewModel when needed
    fun getAdapter(): BluetoothAdapter? = adapter

    private fun hasPermission(permission: String): Boolean =
        ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    private fun hasAllBluetoothPermissions(): Boolean =
        hasPermission(Manifest.permission.BLUETOOTH_CONNECT) &&
                hasPermission(Manifest.permission.BLUETOOTH_SCAN)

    /**
     * Return bonded (paired) devices as DeviceItem list.
     * If permission not granted, returns emptyList.
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun getPairedDevices(): List<DeviceItem> {
        if (!hasAllBluetoothPermissions()) return emptyList()
        return adapter?.bondedDevices?.map {
            DeviceItem(name = it.name ?: "Unknown", address = it.address, isDummy = false)
        } ?: emptyList()
    }

    /**
     * Connect to a BluetoothDevice using SPP UUID
     */
    suspend fun connectToDevice(device: BluetoothDevice): Boolean = withContext(Dispatchers.IO) {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) return@withContext false
        return@withContext try {
            socket = device.createRfcommSocketToServiceRecord(uuid)
            socket?.connect()
            inputStream = socket?.inputStream
            true
        } catch (e: Exception) {
            e.printStackTrace()
            try { socket?.close() } catch (_: Exception) {}
            false
        }
    }

    /**
     * Read raw string from input stream (blocking).
     */
    suspend fun readData(): String? = withContext(Dispatchers.IO) {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) return@withContext null
        return@withContext try {
            val buffer = ByteArray(2048)
            val bytes = inputStream?.read(buffer) ?: return@withContext null
            String(buffer, 0, bytes)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun disconnect() {
        try {
            inputStream?.close()
            socket?.close()
        } catch (_: Exception) {}
        inputStream = null
        socket = null
    }
}