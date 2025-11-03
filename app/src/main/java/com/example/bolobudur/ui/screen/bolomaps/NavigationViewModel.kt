package com.example.bolobudur.ui.screen.bolomaps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bolobudur.data.model.ShortestPathResponse
import com.example.bolobudur.data.repository.LocationRepository
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.*

@HiltViewModel
class NavigationViewModel @Inject constructor(
    val repository: LocationRepository
) : ViewModel() {

    // ambil aliran dari repository
    val latitude = repository.latitude
    val longitude = repository.longitude
    val imu = repository.imu

    private val _remainingDistance = MutableStateFlow(0.0)
    val remainingDistance = _remainingDistance.asStateFlow()

    private val _turnInstruction = MutableStateFlow("Lurus")
    val turnInstruction = _turnInstruction.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
//    val currentIndex = _currentIndex.asStateFlow()

    private val _currentPosition = MutableStateFlow<Point?>(null)
    val currentPosition = _currentPosition.asStateFlow()

    private var pathSegments: List<List<Point>> = emptyList()

    private val _isArrived = MutableStateFlow(false)
    val isArrived = _isArrived.asStateFlow()

    private var _fullPathLine = MutableStateFlow<FeatureCollection?>(null)
    val fullPathLine = _fullPathLine.asStateFlow()

    init {
        // 🔹 pantau perubahan lat, lon, dan imu secara real-time dari repository
        viewModelScope.launch {
            combine(latitude, longitude, imu) { lat, lon, bearing ->
                Triple(lat, lon, bearing)
            }.collect { (lat, lon, bearing) ->
                if (lat == 0.0 && lon == 0.0) return@collect

                val currentPos = Point.fromLngLat(lon, lat)
                _currentPosition.value = currentPos

                if (pathSegments.isNotEmpty()) {
                    updateNavigationLine(currentPos)
                    _remainingDistance.value = calculateRemainingDistance(currentPos)
                }

                _turnInstruction.value = detectTurn(bearing)
            }
        }
    }

    fun startNavigation(shortestPath: ShortestPathResponse) {
        pathSegments = shortestPath.geojson.features.map { feature ->
            feature.geometry.coordinates.map { coord -> Point.fromLngLat(coord[0], coord[1]) }
        }

        // 🔹 Buat full path dari semua titik
        val allPoints = pathSegments.flatMap { it }
        val line = LineString.fromLngLats(allPoints)
        _fullPathLine.value = FeatureCollection.fromFeatures(listOf(Feature.fromGeometry(line)))

        _currentIndex.value = 0
        _remainingDistance.value = shortestPath.totalDistance
        _turnInstruction.value = "Lurus"
        _isArrived.value = false
    }

    fun updateNavigationLine(currentPos: Point) {
        if (pathSegments.isEmpty() || _currentIndex.value >= pathSegments.size) return

        val currentSegment = pathSegments[_currentIndex.value]
        val destination = currentSegment.last()

        // Full path: dari posisi user ke semua titik sisa (concat semua segment setelah currentIndex)
        val remainingPoints = listOf(currentPos) + pathSegments
            .drop(_currentIndex.value)
            .flatMap { it.drop(1) } // skip first point karena sudah currentPos
        val fullLine = LineString.fromLngLats(remainingPoints)
        _fullPathLine.value = FeatureCollection.fromFeatures(listOf(Feature.fromGeometry(fullLine)))

        val distanceToDest = haversine(
            currentPos.latitude(), currentPos.longitude(),
            destination.latitude(), destination.longitude()
        )
        _remainingDistance.value = distanceToDest

        if (distanceToDest < 2) {
            if (_currentIndex.value < pathSegments.size - 1) {
                _currentIndex.value += 1
            } else {
                _isArrived.value = true
                _fullPathLine.value = null
            }
        }
    }




    fun resetNavigation() {
        pathSegments = emptyList()
        _remainingDistance.value = 0.0
        _isArrived.value = false
    }

    private fun calculateRemainingDistance(currentPos: Point): Double {
        var total = 0.0
        for (i in _currentIndex.value until pathSegments.size) {
            val segment = pathSegments[i]
            val start = if (i == _currentIndex.value) currentPos else segment.first()
            for (j in 0 until segment.size - 1) {
                total += haversine(
                    start.latitude(), start.longitude(),
                    segment[j + 1].latitude(), segment[j + 1].longitude()
                )
            }
        }
        return total
    }


    private fun detectTurn(bearing: Float): String {
        return when {
            bearing in 330f..360f || bearing in 0f..30f -> "Lurus"
            bearing in 31f..100f -> "Belok Kanan"
            bearing in 260f..329f -> "Belok Kiri"
            else -> "Putar Balik"
        }
    }

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371e3 // meter
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) *
                cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }
}
