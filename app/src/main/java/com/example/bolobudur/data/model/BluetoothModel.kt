package com.example.bolobudur.data.model

data class BluetoothModel(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val speed: Float,
    val imu: String
)

data class DeviceItem (
    val name: String?,
    val address: String,
    val isDummy: Boolean = false
)