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

    val latitude = repository.latitude
    val longitude = repository.longitude
    val imu = repository.imu

    private val _remainingDistance = MutableStateFlow(0.0)
    val remainingDistance = _remainingDistance.asStateFlow()

    private val _turnInstruction = MutableStateFlow("Lurus")
    val turnInstruction = _turnInstruction.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)

    private val _currentPosition = MutableStateFlow<Point?>(null)
    val currentPosition = _currentPosition.asStateFlow()

    private var _pathSegments: List<List<Point>> = emptyList()

    private val _isArrived = MutableStateFlow(false)
    val isArrived = _isArrived.asStateFlow()

    private var _fullPathLine = MutableStateFlow<FeatureCollection?>(null)
    val fullPathLine = _fullPathLine.asStateFlow()

    private val _bearing = MutableStateFlow(0f)
    val bearing: StateFlow<Float> = _bearing

    // listener off-route
    private var _onOffRoute: (() -> Unit)? = null
    fun setOnOffRouteListener(callback: () -> Unit) {
        _onOffRoute = callback
    }

    init {
        // pantau GPS & IMU
        viewModelScope.launch {
            combine(latitude, longitude, imu) { lat, lon, imuBearing ->
                Triple(lat, lon, imuBearing)
            }.collect { (lat, lon, imuBearing) ->
                if (lat == 0.0 && lon == 0.0) return@collect

                val currentPos = Point.fromLngLat(lon, lat)
                val previousPos = _currentPosition.value ?: currentPos
                _currentPosition.value = currentPos

                val gpsBearing = calculateBearing(
                    previousPos.latitude(),
                    previousPos.longitude(),
                    currentPos.latitude(),
                    currentPos.longitude()
                )

                val effectiveBearing =
                    if (haversine(
                            previousPos.latitude(),
                            previousPos.longitude(),
                            currentPos.latitude(),
                            currentPos.longitude()
                        ) > 0.5
                    ) gpsBearing else imuBearing

                _bearing.value = effectiveBearing

                if (_pathSegments.isNotEmpty()) {
                    updateNavigationLine(currentPos)
                    _remainingDistance.value = calculateRemainingDistance(currentPos)
                }

                _turnInstruction.value = detectTurn(effectiveBearing)
            }
        }
    }

    // mulai navigasi
    fun startNavigation(shortestPath: ShortestPathResponse) {
        _pathSegments = shortestPath.geojson.features.map { feature ->
            feature.geometry.coordinates.map { coord ->
                Point.fromLngLat(coord[0], coord[1])
            }
        }

        val allPoints = _pathSegments.flatMap { it }
        val line = LineString.fromLngLats(allPoints)
        _fullPathLine.value = FeatureCollection.fromFeatures(listOf(Feature.fromGeometry(line)))

        _currentIndex.value = 0
        _remainingDistance.value = shortestPath.totalDistance
        _turnInstruction.value = "Lurus"
        _isArrived.value = false
    }

    fun updateNavigationLine(currentPos: Point) {
        if (_pathSegments.isEmpty() || _currentIndex.value >= _pathSegments.size) return

        checkSegmentProgress(currentPos)

        val currentSegment = _pathSegments[_currentIndex.value]
        val destination = currentSegment.last()

        val remainingPoints = listOf(currentPos) +
                _pathSegments.drop(_currentIndex.value).flatMap { it.drop(1) }

        val line = LineString.fromLngLats(remainingPoints)
        _fullPathLine.value = FeatureCollection.fromFeatures(listOf(Feature.fromGeometry(line)))

        val distToEnd = haversine(
            currentPos.latitude(), currentPos.longitude(),
            destination.latitude(), destination.longitude()
        )
        _remainingDistance.value = distToEnd

        if (distToEnd < 5) {
            if (_currentIndex.value < _pathSegments.size - 1) {
                _currentIndex.value += 1
            } else {
                _isArrived.value = true
                _fullPathLine.value = null
            }
        }
    }

    fun resetNavigation() {
        _pathSegments = emptyList()
        _fullPathLine.value = null
        _remainingDistance.value = 0.0
        _isArrived.value = false
        _turnInstruction.value = "Lurus"
        _currentIndex.value = 0
    }

    private fun calculateRemainingDistance(currentPos: Point): Double {
        var sum = 0.0
        for (i in _currentIndex.value until _pathSegments.size) {
            val seg = _pathSegments[i]
            val start = if (i == _currentIndex.value) currentPos else seg.first()
            for (j in 0 until seg.size - 1) {
                sum += haversine(
                    start.latitude(), start.longitude(),
                    seg[j + 1].latitude(), seg[j + 1].longitude()
                )
            }
        }
        return sum
    }

    // arah hadap user (0 derajat di barat)
    fun detectTurn(bearing: Float): String {
        val b = (bearing + 270f) % 360f

        return when {
            b >= 315f || b < 45f -> "Barat"
            b >= 45f && b < 135f -> "Utara"
            b >= 135f && b < 225f -> "Timur"
            b >= 225f -> "Selatan"
            else -> "Barat"
        }
    }


    // hitung jarak
    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371e3
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) *
                cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        return R * 2 * atan2(sqrt(a), sqrt(1 - a))
    }

    // bearing arah gerak
    fun calculateBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val lat1R = Math.toRadians(lat1)
        val lat2R = Math.toRadians(lat2)
        val dLon = Math.toRadians(lon2 - lon1)

        val y = sin(dLon) * cos(lat2R)
        val x = cos(lat1R) * sin(lat2R) -
                sin(lat1R) * cos(lat2R) * cos(dLon)

        return ((Math.toDegrees(atan2(y, x)) + 360) % 360).toFloat()
    }

    // jarak user ke garis
    private fun distanceToLine(p: Point, a: Point, b: Point): Double {
        val x0 = p.longitude()
        val y0 = p.latitude()
        val x1 = a.longitude()
        val y1 = a.latitude()
        val x2 = b.longitude()
        val y2 = b.latitude()

        val A = x0 - x1
        val B = y0 - y1
        val C = x2 - x1
        val D = y2 - y1

        val dot = A * C + B * D
        val len = C * C + D * D
        val param = if (len != 0.0) dot / len else -1.0

        val (xx, yy) = when {
            param < 0 -> x1 to y1
            param > 1 -> x2 to y2
            else -> (x1 + param * C) to (y1 + param * D)
        }

        return haversine(y0, x0, yy, xx)
    }

    // NAVIGASI MIRIP GOOGLE MAPS (segment-based)
    private fun checkSegmentProgress(current: Point) {
        if (_pathSegments.isEmpty()) return

        val idx = _currentIndex.value

        val switchThreshold = 12.0  // meter
        val offRouteThreshold = 20.0

        val currentSeg = _pathSegments[idx]

        // jarak ke segmen sekarang
        var minCurrent = Double.MAX_VALUE
        for (i in 0 until currentSeg.size - 1) {
            val d = distanceToLine(current, currentSeg[i], currentSeg[i + 1])
            minCurrent = min(minCurrent, d)
        }

        // jarak ke segmen berikutnya
        var minNext = Double.MAX_VALUE
        if (idx < _pathSegments.size - 1) {
            val next = _pathSegments[idx + 1]
            for (i in 0 until next.size - 1) {
                val d = distanceToLine(current, next[i], next[i + 1])
                minNext = min(minNext, d)
            }
        }

        // cek off-route (jarak min ke seluruh garis)
        var globalMin = Double.MAX_VALUE
        for (seg in _pathSegments) {
            for (i in 0 until seg.size - 1) {
                globalMin = min(globalMin, distanceToLine(current, seg[i], seg[i + 1]))
            }
        }
        if (globalMin > offRouteThreshold) {
            _onOffRoute?.invoke()
        }

        // pindah segmen kalau lebih dekat ke next
        if (minNext < minCurrent && minNext < switchThreshold) {
            _currentIndex.value = idx + 1
        }
    }


}
