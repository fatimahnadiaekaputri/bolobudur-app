package com.example.bolobudur.ui.screen.bolomaps.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bolobudur.data.model.FloorData
import com.example.bolobudur.data.model.PoiFeature
import com.example.bolobudur.data.model.ShortestPathResponse
import com.example.bolobudur.data.repository.MapRepository
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapUiState(
    val searchQuery: String = ""
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: MapRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }
    private val _edges = MutableStateFlow<FeatureCollection?>(null)
    val edges: StateFlow<FeatureCollection?> = _edges

    private val _poi = MutableStateFlow<FeatureCollection?>(null)
    val poi: StateFlow<FeatureCollection?> = _poi

    private val _floorData = MutableStateFlow<List<FloorData>>(emptyList())
    val floorData: StateFlow<List<FloorData>> = _floorData

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    // Tambahan state shortest path
    private val _shortestPath = MutableStateFlow<FeatureCollection?>(null)
    val shortestPath: StateFlow<FeatureCollection?> = _shortestPath

    private val _isPathLoading = MutableStateFlow(false)
    val isPathLoading: StateFlow<Boolean> = _isPathLoading

    private val _isPathVisible = MutableStateFlow(false)
    val isPathVisible: StateFlow<Boolean> = _isPathVisible

    private val _selectedDestination = MutableStateFlow<PoiFeature?>(null)
    val selectedDestination: StateFlow<PoiFeature?> = _selectedDestination

    private val _pathInfo = MutableStateFlow<ShortestPathResponse?>(null)
    val pathInfo: StateFlow<ShortestPathResponse?> = _pathInfo


    fun loadMapData() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val edgesRes = repository.getEdges()
                val poiRes = repository.getPoi()

                _edges.value = edgesRes
                _poi.value = poiRes

                poiRes?.let { featureCollection ->
                    val floors = mapPoiToFloors(featureCollection.features() ?: emptyList())
                    _floorData.value = floors
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }

    fun getShortestPath(
        fromLat: Double,
        fromLon: Double,
        toLat: Double,
        toLon: Double,
        destinationLabel: String? = null
    ) {
        viewModelScope.launch {
            _isPathLoading.value = true
            try {
                val response = repository.getShortestPath(fromLat, fromLon, toLat, toLon)

                if (response != null) {
                    _pathInfo.value = response
                    _isPathVisible.value = true

                    // 🔹 ubah geojson -> FeatureCollection
                    val features = response.geojson.features.mapNotNull { feature ->
                        val coords = feature.geometry.coordinates
                        if (coords.size >= 2) {
                            val lineString = com.mapbox.geojson.LineString.fromLngLats(
                                coords.map { coord ->
                                    com.mapbox.geojson.Point.fromLngLat(coord[0], coord[1])
                                }
                            )
                            com.mapbox.geojson.Feature.fromGeometry(lineString).apply {
                                addNumberProperty("distance", feature.properties.distance)
                            }
                        } else null
                    }
                    _shortestPath.value = com.mapbox.geojson.FeatureCollection.fromFeatures(features)

                    // 🔹 simpan label destinasi
                    if (destinationLabel != null) {
                        _selectedDestination.value = PoiFeature(
                            label = destinationLabel,
                            poi = "",
                            lokasi = "",
                            lat = toLat,
                            lon = toLon
                        )
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isPathLoading.value = false
            }
        }
    }




    fun resetPath() {
        _isPathVisible.value = false
        _isPathLoading.value = false
        _shortestPath.value = null
    }


    private fun mapPoiToFloors(features: List<Feature>): List<FloorData> {
        val poiList = features.mapNotNull { feature ->
            val props = feature.properties()
            val label = props?.get("label")?.asString
            val lokasi = props?.get("lokasi")?.asString
            val poi = props?.get("poi")?.asString
            val geometry = feature.geometry()

            val point = geometry as? Point
            val lat = point?.latitude()
            val lon = point?.longitude()

            if (label != null && lokasi != null && lat != null && lon != null) {
                PoiFeature(label, poi ?: "", lokasi, lat, lon)
            } else null
        }

        val grouped = poiList.groupBy { it.lokasi }

        return grouped.entries.mapIndexed { index, entry ->
            FloorData(
                id = index,
                title = entry.key,
                items = entry.value
            )
        }.sortedBy { it.title }
    }
}