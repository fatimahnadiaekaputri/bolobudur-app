package com.example.bolobudur.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bolobudur.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val repo: LocationRepository
) : ViewModel() {

    val lat = repo.latitude.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0
    )
    val lon = repo.longitude.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0
    )
    val imu = repo.imu.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), 0f
    )
}
