package com.example.bolobudur.ui.screen.bolomaps.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bolobudur.data.repository.MapRepository
import com.mapbox.geojson.FeatureCollection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: MapRepository
) : ViewModel() {

    private val _edges = MutableStateFlow<FeatureCollection?>(null)
    val edges: StateFlow<FeatureCollection?> = _edges

    private val _poi = MutableStateFlow<FeatureCollection?>(null)
    val poi: StateFlow<FeatureCollection?> = _poi

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    fun loadMapData() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val edgesRes = repository.getEdges()
                val poiRes = repository.getPoi()

                _edges.value = edgesRes
                _poi.value = poiRes
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }
}