package com.example.bolobudur.data.model

import java.time.Instant
import java.util.Date


data class BluetoothModel(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val speed: Float,
    val imu: Float,
    val timestamp: Date = Date()
)

data class DeviceItem (
    val name: String?,
    val address: String,
    val isDummy: Boolean = false
)