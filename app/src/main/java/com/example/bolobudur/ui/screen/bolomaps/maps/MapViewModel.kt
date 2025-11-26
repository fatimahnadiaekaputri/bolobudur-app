package com.example.bolobudur.ui.screen.bolomaps.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bolobudur.data.model.FloorData
import com.example.bolobudur.data.model.NearbyResponse
import com.example.bolobudur.data.model.PoiFeature
import com.example.bolobudur.data.model.ShortestPathResponse
import com.example.bolobudur.data.repository.CulturalSiteRepository
import com.example.bolobudur.data.repository.MapRepository
import com.example.bolobudur.ui.screen.bolomaps.NavigationViewModel
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
    private val repository: MapRepository,
    private val siteRepository: CulturalSiteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        if (query.isBlank()) {
            clearSearchResults()
        }
    }
    private val _edges = MutableStateFlow<FeatureCollection?>(null)
    val edges: StateFlow<FeatureCollection?> = _edges

    private val _poi = MutableStateFlow<FeatureCollection?>(null)
    val poi: StateFlow<FeatureCollection?> = _poi

    private val _floorData = MutableStateFlow<List<FloorData>>(emptyList())
    val floorData: StateFlow<List<FloorData>> = _floorData

    private val _mapLoading = MutableStateFlow(true)
    val mapLoading: StateFlow<Boolean> = _mapLoading

    private val _listLoading = MutableStateFlow(false)
    val listLoading: StateFlow<Boolean> = _listLoading


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

    private val _searchResults = MutableStateFlow<List<PoiFeature>>(emptyList())
    val searchResults: StateFlow<List<PoiFeature>> = _searchResults

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _nearbyPoi = MutableStateFlow<NearbyResponse?>(null)
    val nearbyPoi: StateFlow<NearbyResponse?> = _nearbyPoi

    fun loadMapData() {
        viewModelScope.launch {
            try {
                _mapLoading.value = true
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
                _mapLoading.value = false
            }
        }
    }

    fun getShortestPath(
        fromLat: Double,
        fromLon: Double,
        toLat: Double,
        toLon: Double,
        destinationLabel: String? = null,
        navigationViewModel: NavigationViewModel? = null
    ) {
        viewModelScope.launch {
            _isPathLoading.value = true
            try {
                val response = repository.getShortestPath(fromLat, fromLon, toLat, toLon)

                if (response == null) {
                    _errorMessage.value = "Gagal memuat rute. Coba lokasi lain."
                    return@launch
                }

                if (!response.success) {
                    _errorMessage.value = response.message
                    return@launch
                }

                _pathInfo.value = response
                _isPathVisible.value = true

                val features = response.geojson.features.mapNotNull { feature ->
                    val coords = feature.geometry.coordinates
                    if (coords.size >= 2) {
                        val lineString = com.mapbox.geojson.LineString.fromLngLats(
                            coords.map { coord ->
                                Point.fromLngLat(coord[0], coord[1])
                            }
                        )
                        Feature.fromGeometry(lineString).apply {
                            addNumberProperty("distance", feature.properties.distance)
                        }
                    } else null
                }
                _shortestPath.value = FeatureCollection.fromFeatures(features)

                if (destinationLabel != null) {
                    _selectedDestination.value = PoiFeature(
                        label = destinationLabel,
                        poi = "",
                        lokasi = "",
                        lat = toLat,
                        lon = toLon
                    )
                }

                navigationViewModel?.startNavigation(response)

            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Terjadi kesalahan saat memuat rute."
            } finally {
                _isPathLoading.value = false
            }
        }
    }


    fun clearErrorMessage() {
        _errorMessage.value = null
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

    fun searchPoi(keyword: String) {
        viewModelScope.launch {
            try {
                _listLoading.value = true
                val response = repository.searchPoi(keyword)

                val results = response?.features()?.mapNotNull { feature ->
                    val props = feature.properties()
                    val label = props?.get("label")?.asString
                    val lokasi = props?.get("lokasi")?.asString
                    val poi = props?.get("poi")?.asString
                    val geometry = feature.geometry() as? Point
                    val lat = geometry?.latitude()
                    val lon = geometry?.longitude()

                    if (label != null && lat != null && lon != null) {
                        PoiFeature(label, poi ?: "", lokasi ?: "", lat, lon)
                    } else null
                } ?: emptyList()

                _searchResults.value = results
            } catch (e: Exception) {
                e.printStackTrace()
                _searchResults.value = emptyList()
            } finally {
                _listLoading.value = false
            }
        }
    }

    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }

    fun checkNearby(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val response = siteRepository.getNearby(lat, lon)
                if (response.detectedAreas.isNotEmpty()) {
                    _nearbyPoi.value = response
                }
            } catch (_: Exception) {}
        }
    }

    fun clearNearbyFlag() {
        _nearbyPoi.value = null
    }


}