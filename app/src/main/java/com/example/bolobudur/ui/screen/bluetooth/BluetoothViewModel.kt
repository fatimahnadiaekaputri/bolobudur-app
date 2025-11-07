package com.example.bolobudur.ui.screen.bluetooth

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bolobudur.data.bluetooth.BluetoothReceiver
import com.example.bolobudur.data.model.DeviceItem
import com.example.bolobudur.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.bolobudur.data.bluetooth.BluetoothService
import com.example.bolobudur.data.model.BtState

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val receiver: BluetoothReceiver,
    val locationRepository: LocationRepository
) : ViewModel() {

    private val _isBluetoothEnabled = MutableStateFlow(false)
    val isBluetoothEnabled = _isBluetoothEnabled.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<DeviceItem>>(emptyList())
    val pairedDevices = _pairedDevices.asStateFlow()

    private val _scannedDevices = MutableStateFlow<List<DeviceItem>>(emptyList())
    val scannedDevices = _scannedDevices.asStateFlow()


    private val _isScanning = MutableStateFlow(false)
    val isScanning = _isScanning.asStateFlow()

    private val _btState = MutableStateFlow(BtState())
    val btState = _btState.asStateFlow()


    fun checkBluetoothEnabled() {
        _isBluetoothEnabled.value = receiver.isBluetoothEnabled()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun loadDevices() {
        viewModelScope.launch {
            _isScanning.value = true

            // 1️⃣ Load paired devices dulu + dummy
            val paired = try { receiver.getPairedDevices() } catch (_: SecurityException) { emptyList() }
            _pairedDevices.value = paired + listOf(DeviceItem("Dummy ESP32", "DUMMY", isDummy = true))
        }

        // 2️⃣ Scan devices async, dijalankan di coroutine terpisah
        viewModelScope.launch {
            val scanned = try { receiver.discoverDevices() } catch (_: SecurityException) { emptyList() }
            _scannedDevices.value = scanned
            _isScanning.value = false
        }
    }

    fun updateBtState(
        isEnabled: Boolean? = null,
        isConnected: Boolean? = null,
        isPaused: Boolean? = null,
        deviceName: String? = null,
        isScanning: Boolean? = null
    ) {
        _btState.value = _btState.value.copy(
            isEnabled = isEnabled ?: _btState.value.isEnabled,
            isConnected = isConnected ?: _btState.value.isConnected,
            isPaused = isPaused ?: _btState.value.isPaused,
            deviceName = deviceName ?: _btState.value.deviceName,
            isScanning = isScanning ?: _btState.value.isScanning
        )
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun startService(context: Context, addr: String, isDummy: Boolean) {
        val intent = Intent(context.applicationContext, BluetoothService::class.java).apply {
            putExtra(BluetoothService.EXTRA_DEVICE_ADDRESS, addr)
            putExtra(BluetoothService.EXTRA_IS_DUMMY, isDummy)
        }

        context.startForegroundService(intent)
    }

    fun stopService(context: Context) {
        context.stopService(Intent(context, BluetoothService::class.java))
    }
}



