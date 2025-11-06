package com.example.bolobudur.ui.screen.bolofind

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bolobudur.data.model.FeatureItem
import com.example.bolobudur.data.repository.CulturalSiteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoloFindViewModel @Inject constructor(
    private val repository: CulturalSiteRepository
) : ViewModel() {

    private val _features = MutableStateFlow<List<FeatureItem>>(emptyList())
    val features: StateFlow<List<FeatureItem>> = _features

    private val _zoneName = MutableStateFlow<String>("Mencari lokasi...")
    val zoneName: StateFlow<String> = _zoneName

    fun loadNearby(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val response = repository.getNearby(lat, lon)

                val firstArea = response.detectedAreas.firstOrNull()
                _features.value = firstArea?.features ?: emptyList()

                _zoneName.value = firstArea?.zoneName ?: "Area tidak dikenal"
            } catch (e: Exception) {
                e.printStackTrace()
                _zoneName.value = "Gagal memuat lokasi"
            }
        }
    }
}