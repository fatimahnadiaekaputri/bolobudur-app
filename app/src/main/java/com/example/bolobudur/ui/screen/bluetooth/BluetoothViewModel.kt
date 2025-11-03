package com.example.bolobudur.ui.screen.bluetooth

import android.bluetooth.BluetoothDevice
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bolobudur.data.bluetooth.BluetoothReceiver
import com.example.bolobudur.data.bluetooth.DummyBluetoothReceiver
import com.example.bolobudur.data.model.BluetoothModel
import com.example.bolobudur.data.model.DeviceItem
import com.example.bolobudur.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val receiver: BluetoothReceiver,
    private val locationRepository: LocationRepository
) : ViewModel() {

    // states for UI
    private val _isBluetoothEnabled = MutableStateFlow(false)
    val isBluetoothEnabled = _isBluetoothEnabled.asStateFlow()

    private val _devices = MutableStateFlow<List<DeviceItem>>(emptyList())
    val devices = _devices.asStateFlow()

    private val _connectedDevice = MutableStateFlow<DeviceItem?>(null)
    val connectedDevice = _connectedDevice.asStateFlow()

    private val _data = MutableStateFlow<BluetoothModel?>(null)
    val data = _data.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // dummy simulator instance (not injected)
    private val dummy = DummyBluetoothReceiver()

    private val _isScanning = MutableStateFlow(false)
    val isScanning = _isScanning.asStateFlow()

    private val _rawData = MutableStateFlow<String?>(null)
    val rawData: StateFlow<String?> = _rawData


    fun checkBluetoothEnabled() {
        _isBluetoothEnabled.value = receiver.isBluetoothEnabled()
    }

    /**
     * populate devices (paired devices). If none, show dummy list so UI can test.
     * Note: permission must be requested from UI before calling this (we still handle safely).
     */
    @RequiresApi(Build.VERSION_CODES.S)
    fun loadPairedDevices() {
        viewModelScope.launch {
            _isScanning.value = true
            try {
                val list = try { receiver.getAllDevices() } catch (_: SecurityException) { emptyList() }
                val staticDummy = listOf(
                    DeviceItem("ESP32-DUMMY-1", "DUMMY_ADDR_1", isDummy = true),
                    DeviceItem("ESP32-DUMMY-2", "DUMMY_ADDR_2", isDummy = true)
                )
                _devices.value = list + staticDummy
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = e.message
            } finally {
                _isScanning.value = false
            }
        }
    }


    /**
     * Connect flow: if device.isDummy -> start dummy collect,
     * else connect via receiver and start read loop.
     */
    fun connectToDevice(deviceItem: DeviceItem) {
        viewModelScope.launch {
            _error.value = null
            if (deviceItem.isDummy) {
                _connectedDevice.value = deviceItem
                try {
                    val ok = dummy.connect()
                    if (ok) {
                        dummy.dataFlow.collect { raw ->
                            parseAndPost(raw)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _error.value = "Dummy connect error"
                }
                return@launch
            }

            // real device
            val adapter = receiver.getAdapter()
            if (adapter == null) {
                _error.value = "Bluetooth adapter tidak tersedia"
                return@launch
            }
            try {
                val btDevice: BluetoothDevice = adapter.getRemoteDevice(deviceItem.address)
                val ok = receiver.connectToDevice(btDevice)
                if (ok) {
                    _connectedDevice.value = deviceItem
                    // start reading loop
                    viewModelScope.launch {
                        while (_connectedDevice.value != null) {
                            val raw = receiver.readData()
                            if (!raw.isNullOrEmpty()) _rawData.value = raw
                        }
                    }
                } else {
                    _error.value = "Gagal connect ke device"
                }
            } catch (se: SecurityException) {
                se.printStackTrace()
                _error.value = "Permission BLUETOOTH_CONNECT diperlukan"
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Gagal connect: ${e.message}"
            }
        }
    }

    private fun parseAndPost(raw: String) {
        try {
            val json = JSONObject(raw)
            val model = BluetoothModel(
                id = json.optString("id", ""),
                latitude = json.optDouble("latitude", 0.0),
                longitude = json.optDouble("longitude", 0.0),
                speed = json.optDouble("kecepatan", 0.0).toFloat(),
                imu = json.optDouble("imu", 0.0).toFloat(),
                timestamp = Date()
            )
            _data.value = model

            locationRepository.updateFromBluetooth(
                model.latitude,
                model.longitude,
                model.imu
            )
        } catch (e: Exception) {
            e.printStackTrace()
            _error.value = "Parsing data gagal"
        }
    }

    fun disconnect() {
        // stop dummy or real
        dummy.disconnect()
        receiver.disconnect()
        _connectedDevice.value = null
    }
}