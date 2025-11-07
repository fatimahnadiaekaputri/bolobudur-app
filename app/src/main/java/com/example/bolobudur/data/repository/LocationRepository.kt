package com.example.bolobudur.data.repository

import com.example.bolobudur.data.model.BtState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor() {
    private val _latitude = MutableStateFlow(0.0)
    val latitude = _latitude.asStateFlow()

    private val _longitude = MutableStateFlow(0.0)
    val longitude = _longitude.asStateFlow()

    private val _imu = MutableStateFlow(0.0f)
    val imu = _imu.asStateFlow()

    val btStateFlow = MutableStateFlow(BtState())

    fun updateFromBluetooth(lat: Double, lon: Double, imu: Float) {
        _latitude.value = lat
        _longitude.value = lon
        _imu.value = imu
    }

    fun updateBtStateFromService(btState: BtState) {
        btStateFlow.value = btState
    }
}