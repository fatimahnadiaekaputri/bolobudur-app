package com.example.bolobudur.data.model


data class DeviceItem (
    val name: String?,
    val address: String,
    val isDummy: Boolean = false
)

data class BtState(
    val isEnabled: Boolean = false,
    val isConnected: Boolean = false,
    val isPaused: Boolean = false,
    val deviceName: String? = null,
    val isScanning: Boolean = false
)