package com.example.bolobudur.data.bluetooth

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.random.Random

class DummyBluetoothReceiver {
    private val _dataFlow = MutableSharedFlow<String>()
    val dataFlow = _dataFlow.asSharedFlow()
    private var running = false
    private var jobScope: CoroutineScope? = null

    suspend fun connect(): Boolean {
        running = true
        jobScope = CoroutineScope(Dispatchers.Default)
        jobScope?.launch {
            while (running) {
                val json = JSONObject(
                    mapOf(
                        "id" to "ESP32-DUMMY",
                        "latitude" to (-7.7956 + Random.nextDouble(-0.0005, 0.0005)),
                        "longitude" to (110.3695 + Random.nextDouble(-0.0005, 0.0005)),
                        "kecepatan" to Random.nextDouble(0.0, 3.0),
                        "imu" to "[${Random.nextFloat()}, ${Random.nextFloat()}, ${Random.nextFloat()}]"
                    )
                )
                _dataFlow.emit(json.toString())
                delay(1000)
            }
        }
        return true
    }

    fun disconnect() {
        running = false
        jobScope = null
    }

}