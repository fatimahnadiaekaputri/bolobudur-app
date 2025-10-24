package com.example.bolobudur.ui.screen.bluetooth

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bolobudur.data.bluetooth.BluetoothReceiver
import com.example.bolobudur.data.bluetooth.DummyBluetoothReceiver
import com.example.bolobudur.data.model.DeviceItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val receiver: BluetoothReceiver
) : ViewModel() {

    // states for UI
    private val _isBluetoothEnabled = MutableStateFlow(false)
    val isBluetoothEnabled = _isBluetoothEnabled.asStateFlow()

    private val _devices = MutableStateFlow<List<DeviceItem>>(emptyList())
    val devices = _devices.asStateFlow()

    private val _connectedDevice = MutableStateFlow<DeviceItem?>(null)
    val connectedDevice = _connectedDevice.asStateFlow()

    private val _data = MutableStateFlow<Map<String, Any>?>(null)
    val data = _data.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // dummy simulator instance (not injected)
    private val dummy = DummyBluetoothReceiver()

    fun checkBluetoothEnabled() {
        _isBluetoothEnabled.value = receiver.isBluetoothEnabled()
    }

    /**
     * populate devices (paired devices). If none, show dummy list so UI can test.
     * Note: permission must be requested from UI before calling this (we still handle safely).
     */
    fun loadPairedDevices() {
        viewModelScope.launch {
            try {
                val list = try { receiver.getPairedDevices() } catch (_: SecurityException) { emptyList<DeviceItem>() }

                // daftar dummy statis yang selalu muncul di list
                val staticDummy = listOf(
                    DeviceItem("ESP32-DUMMY-1", "DUMMY_ADDR_1", isDummy = true),
                    DeviceItem("ESP32-DUMMY-2", "DUMMY_ADDR_2", isDummy = true)
                )

                // merge real + dummy
                _devices.value = list + staticDummy
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = e.message
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
                            if (!raw.isNullOrEmpty()) parseAndPost(raw)
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
            _data.value = mapOf(
                "id" to json.optString("id", ""),
                "latitude" to json.optDouble("latitude", 0.0),
                "longitude" to json.optDouble("longitude", 0.0),
                "kecepatan" to json.optDouble("kecepatan", 0.0),
                "imu" to json.optString("imu", "")
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