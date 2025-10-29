package com.example.bolobudur.data.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import java.util.UUID
import com.example.bolobudur.data.model.DeviceItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

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
    fun getAdapter(): BluetoothAdapter? = adapter

    private fun hasPermission(permission: String): Boolean =
        ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    @RequiresApi(Build.VERSION_CODES.S)
    private fun hasAllBluetoothPermissions(): Boolean =
        hasPermission(Manifest.permission.BLUETOOTH_CONNECT) &&
                hasPermission(Manifest.permission.BLUETOOTH_SCAN)

    /**
     * Return bonded (paired) devices as DeviceItem list.
     * If permission not granted, returns emptyList.
     */
    @RequiresApi(Build.VERSION_CODES.S)
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun getPairedDevices(): List<DeviceItem> {
        if (!hasAllBluetoothPermissions()) return emptyList()
        return adapter?.bondedDevices?.map {
            DeviceItem(name = it.name ?: "Unknown", address = it.address, isDummy = false)
        } ?: emptyList()
    }

    /**
     *  Return bluetooth devices via discovery scan
     */

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    suspend fun discoverDevices(): List<DeviceItem> = suspendCancellableCoroutine { cont ->
        if (!hasAllBluetoothPermissions()) {
            cont.resume(emptyList())
            return@suspendCancellableCoroutine
        }

        val discoveredDevices = mutableListOf<DeviceItem>()
        val receiver = object : BroadcastReceiver() {
            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device: BluetoothDevice? =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        if (device != null) {
                            discoveredDevices.add(
                                DeviceItem(
                                    name = device.name ?: "Unknown",
                                    address = device.address,
                                    isDummy = false
                                )
                            )
                        }
                    }
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                        context?.unregisterReceiver(this)
                        cont.resume(discoveredDevices)
                    }
                }
            }
        }

        context.registerReceiver(
            receiver,
            IntentFilter(BluetoothDevice.ACTION_FOUND).apply {
                addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            }
        )

        adapter?.startDiscovery()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @RequiresApi(Build.VERSION_CODES.S)
    suspend fun getAllDevices(): List<DeviceItem> {
        val paired = getPairedDevices()
        val discovered = discoverDevices()

        return (paired + discovered).distinctBy { it.address }
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
        try {
            if (inputStream == null) return@withContext null
            val reader = BufferedReader(InputStreamReader(inputStream))
            return@withContext reader.readLine()
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