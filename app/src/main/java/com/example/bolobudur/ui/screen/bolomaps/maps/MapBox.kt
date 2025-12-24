package com.example.bolobudur.ui.screen.bolomaps.maps

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.example.bolobudur.ui.components.Loader
import com.example.bolobudur.ui.screen.bolomaps.NavigationViewModel
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.circleLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.easeTo
import com.example.bolobudur.R
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.toCameraOptions
import kotlinx.coroutines.launch
import com.mapbox.maps.RenderedQueryOptions
import com.mapbox.maps.QueryRenderedFeaturesCallback
import com.mapbox.maps.ScreenCoordinate


@SuppressLint("LocalContextResourcesRead")
@Composable
fun MapBox(
    viewModel: MapViewModel = hiltViewModel(),
    navigationViewModel: NavigationViewModel = hiltViewModel(),
) {


    val context = LocalContext.current
    val borobudur = Point.fromLngLat(110.2038, -7.6079)
//    val testingTeti = Point.fromLngLat(110.37152147214815, -7.765591698479714)
    val viewportState = rememberMapViewportState {
//        setCameraOptions {
//            center(testingTeti)
//            zoom(17.0)
//        }
    }

    val edges by viewModel.edges.collectAsState()
    val poi by viewModel.poi.collectAsState()
    val path by viewModel.shortestPath.collectAsState()
    val isLoading by viewModel.mapLoading.collectAsState()
    val isPathLoading by viewModel.isPathLoading.collectAsState()
    val currentPos by navigationViewModel.currentPosition.collectAsState()
    val bearing by navigationViewModel.bearing.collectAsState(initial = 0f)

    val isArrived by navigationViewModel.isArrived.collectAsState()

    val fullPath by navigationViewModel.fullPathLine.collectAsState()
    val isNavigating = fullPath != null

    LaunchedEffect(Unit) {
        viewModel.loadMapData()
        viewportState.setCameraOptions {
            center(borobudur)
            zoom(17.0)
            pitch(0.0)
            bearing(0.0)
        }


    }

    LaunchedEffect(Unit) {
        var isRerouting = false
        navigationViewModel.setOnOffRouteListener {
            if (!isRerouting) {
                isRerouting = true
                val current = navigationViewModel.currentPosition.value
                val destination = viewModel.selectedDestination.value
                if (current != null && destination != null) {
                    viewModel.getShortestPath(
                        current.latitude(),
                        current.longitude(),
                        destination.lat,
                        destination.lon,
                        destination.label,
                        navigationViewModel
                    )
                }
                navigationViewModel.viewModelScope.launch {
                    kotlinx.coroutines.delay(3000)
                    isRerouting = false
                }
            }
        }
    }

    if (isLoading) {
        Loader()
    } else {
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = viewportState,
        ) {
            // MAP LAYER HANDLING
            MapEffect(edges, poi, path, isNavigating) { mapView ->
                val mapboxMap = mapView.mapboxMap
                mapboxMap.getStyle { style ->

                    edges?.let {
                        style.addLineAndLabelLayer(it.toJson())
                    }

                    poi?.let {
                        style.addPoiLayers(context, it.toJson())
                    }

                    if (path == null) {
                        if (style.styleLayerExists("shortest-path-layer")) {
                            style.removeStyleLayer("shortest-path-layer")
                        }
                        if (style.styleSourceExists("shortest-path-source")) {
                            style.removeStyleSource("shortest-path-source")
                        }

                        // --- ⛔️ PERUBAHAN DI SINI: Hapus layer marker LAMA ---
//                        if (style.styleLayerExists("route-markers-layer")) {
//                            style.removeStyleLayer("route-markers-layer")
//                        }
//                        if (style.styleSourceExists("route-markers-source")) {
//                            style.removeStyleSource("route-markers-source")
//                        }

                        // --- ✅ PERUBAHAN DI SINI: Hapus layer marker BARU ---
                        if (style.styleLayerExists("current-marker-layer")) {
                            style.removeStyleLayer("current-marker-layer")
                        }
                        if (style.styleSourceExists("current-marker-source")) {
                            style.removeStyleSource("current-marker-source")
                        }
                        if (style.styleLayerExists("destination-marker-layer")) {
                            style.removeStyleLayer("destination-marker-layer")
                        }
                        if (style.styleSourceExists("destination-marker-source")) {
                            style.removeStyleSource("destination-marker-source")
                        }


                        mapboxMap.getStyle()?.let {
                            it.removeStyleLayer("shortest-path-layer")
                            it.removeStyleSource("shortest-path-source")

                            it.removeStyleLayer("current-marker-layer")
                            it.removeStyleSource("current-marker-source")
                            it.removeStyleLayer("destination-marker-layer")
                            it.removeStyleSource("destination-marker-source")
                        }

//                        viewportState.setCameraOptions {
//                            center(Point.fromLngLat(110.2038, -7.6079))
//                            zoom(17.0)
//                            pitch(0.0)
//                            bearing(0.0)
//                        }

                        return@getStyle
                    }

                    if (isNavigating) {
                        if (style.styleLayerExists("shortest-path-layer"))
                            style.removeStyleLayer("shortest-path-layer")
                        if (style.styleSourceExists("shortest-path-source"))
                            style.removeStyleSource("shortest-path-source")
                        return@getStyle
                    }

                    if (!isPathLoading && path != null) {
                        val sourceId = "shortest-path-source"
                        val layerId = "shortest-path-layer"


                        val featureCollection = FeatureCollection.fromJson(path!!.toJson())

                        if (style.styleSourceExists(sourceId)) {
                            val source = style.getSourceAs<GeoJsonSource>(sourceId)
                            source?.data(path!!.toJson())
                        } else {
                            style.addSource(
                                geoJsonSource(sourceId) {
                                    data(path!!.toJson())
                                }
                            )
                        }

                        if (!style.styleLayerExists(layerId)) {
                            style.addLayer(
                                lineLayer(layerId, sourceId) {
                                    lineColor("#1E90FF")
                                    lineWidth(5.0)
                                    lineOpacity(0.8)
                                }
                            )
                        }

                        val allCoordinates = featureCollection.features()
                            ?.flatMap { feature ->
                                (feature.geometry() as? LineString)?.coordinates() ?: emptyList()
                            } ?: emptyList()

                        if (allCoordinates.isNotEmpty()) {
                            val startPoint = allCoordinates.first()
                            val endPoint = allCoordinates.last()

                            val currentSourceId = "current-marker-source"
                            val currentLayerId = "current-marker-layer"
                            val destinationSourceId = "destination-marker-source"
                            val destinationLayerId = "destination-marker-layer"

                            if (style.styleSourceExists(currentSourceId)) {
                                style.getSourceAs<GeoJsonSource>(currentSourceId)?.feature(
                                    Feature.fromGeometry(startPoint)
                                )
                            } else {
                                style.addSource(
                                    geoJsonSource(currentSourceId) {
                                        feature(Feature.fromGeometry(startPoint))
                                    }
                                )
                            }

                            if (!style.styleLayerExists(currentLayerId)) {
                                style.addLayer(
                                    circleLayer(currentLayerId, currentSourceId) {
                                        circleRadius(6.0)
                                        circleColor("#FF0000") // merah
                                        circleStrokeColor("#880000")
                                        circleStrokeWidth(1.0)
                                    }
                                )
                            }

                            if (style.styleSourceExists(destinationSourceId)) {
                                style.getSourceAs<GeoJsonSource>(destinationSourceId)?.feature(
                                    Feature.fromGeometry(endPoint)
                                )
                            } else {
                                style.addSource(
                                    geoJsonSource(destinationSourceId) {
                                        feature(Feature.fromGeometry(endPoint))
                                    }
                                )
                            }

                            if (!style.styleLayerExists(destinationLayerId)) {
                                val bitmap = ContextCompat.getDrawable(context, R.drawable.ic_marker)?.toBitmap()

                                if (bitmap != null) {
                                    if (style.getStyleImage("destination-icon") == null) {
                                        style.addImage("destination-icon", bitmap)
                                    }

                                    style.addLayer(
                                        symbolLayer(destinationLayerId, destinationSourceId) {
                                            iconImage("destination-icon")
                                            iconAnchor(IconAnchor.BOTTOM)
                                            iconAllowOverlap(true)
                                            iconIgnorePlacement(true)
                                        }
                                    )
                                } else {
                                    style.addLayer(
                                        circleLayer(destinationLayerId, destinationSourceId) {
                                            circleRadius(6.0)
                                            circleColor("#FF8C00") // Oranye
                                            circleStrokeWidth(1.0)
                                            circleStrokeColor("#FFFFFF")
                                            circleOpacity(0.9)
                                        }
                                    )
                                }
                            }

                            val cameraOptions = mapboxMap.cameraForCoordinates(
                                allCoordinates,
                                EdgeInsets(100.0, 100.0, 100.0, 100.0),
                                null,
                                null
                            )

                            mapboxMap.easeTo(cameraOptions, mapAnimationOptions {
                                duration(2000L)
                            })
                        }
                    }

                }
            }

            MapEffect(fullPath, currentPos, isArrived) { mapView ->
                val mapboxMap = mapView.mapboxMap
                mapboxMap.getStyle { style ->

                    if (!isArrived) {
                        // === Full path (biru) ===
                        fullPath?.let { full ->
                            val sourceId = "full-path-source"
                            val layerId = "full-path-layer"
                            if (style.styleSourceExists(sourceId)) {
                                style.getSourceAs<GeoJsonSource>(sourceId)?.data(full.toJson())
                            } else {
                                style.addSource(geoJsonSource(sourceId) { data(full.toJson()) })
                            }
                            if (!style.styleLayerExists(layerId)) {
                                style.addLayer(lineLayer(layerId, sourceId) {
                                    lineColor("#1E90FF")
                                    lineWidth(4.0)
                                    lineOpacity(0.6)
                                })
                            }
                        }

                        // === Tambahan: marker merah di destination ===
                        val destinationSourceId = "destination-source"
                        val destinationLayerId = "destination-layer"

                        // Ambil koordinat titik terakhir (destination)
                        val destinationPoint = fullPath?.features()
                            ?.lastOrNull()
                            ?.geometry()
                            ?.let { it as? LineString }
                            ?.coordinates()
                            ?.lastOrNull()

                        destinationPoint?.let { dest ->
                            val geoJson = FeatureCollection.fromFeatures(
                                listOf(Feature.fromGeometry(Point.fromLngLat(dest.longitude(), dest.latitude())))
                            )

                            if (style.styleSourceExists(destinationSourceId)) {
                                style.getSourceAs<GeoJsonSource>(destinationSourceId)?.featureCollection(geoJson)
                            } else {
                                style.addSource(geoJsonSource(destinationSourceId) {
                                    featureCollection(geoJson)
                                })
                            }

                            // Coba pakai icon custom marker merah
                            val bitmap = ContextCompat.getDrawable(context, R.drawable.ic_marker)?.toBitmap()
                            if (bitmap != null) {
                                if (style.getStyleImage("destination-icon") == null) {
                                    style.addImage("destination-icon", bitmap)
                                }

                                if (!style.styleLayerExists(destinationLayerId)) {
                                    style.addLayer(
                                        symbolLayer(destinationLayerId, destinationSourceId) {
                                            iconImage("destination-icon")
                                            iconAnchor(IconAnchor.BOTTOM)
                                            iconAllowOverlap(true)
                                            iconIgnorePlacement(true)
                                        }
                                    )
                                }
                            } else {
                                // fallback kalau ic_marker_red ga ada
                                if (!style.styleLayerExists(destinationLayerId)) {
                                    style.addLayer(
                                        circleLayer(destinationLayerId, destinationSourceId) {
                                            circleRadius(6.0)
                                            circleColor("#FF0000") // Merah
                                            circleStrokeWidth(1.0)
                                            circleStrokeColor("#FFFFFF")
                                            circleOpacity(0.9)
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        // === Hapus semua garis dan marker kalau sudah sampai ===
                        listOf(
                            "full-path-layer", "full-path-source",
                            "dynamic-line-layer", "dynamic-line-source",
                            "destination-layer", "destination-source"
                        ).forEach { id ->
                            if (style.styleLayerExists(id)) style.removeStyleLayer(id)
                            if (style.styleSourceExists(id)) style.removeStyleSource(id)
                        }
                    }
                }
            }

            MapEffect(fullPath) { mapView ->
                val mapboxMap = mapView.mapboxMap
                mapboxMap.getStyle { style ->
                    if (fullPath == null) {
                        if (style.styleLayerExists("full-path-layer")) style.removeStyleLayer("full-path-layer")
                        if (style.styleSourceExists("full-path-source")) style.removeStyleSource("full-path-source")
                        if (style.styleLayerExists("dynamic-line-layer")) style.removeStyleLayer("dynamic-line-layer")
                        if (style.styleSourceExists("dynamic-line-source")) style.removeStyleSource("dynamic-line-source")

                        // 🧹 hapus juga icon marker tujuan
                        if (style.styleLayerExists("destination-layer")) style.removeStyleLayer("destination-layer")
                        if (style.styleSourceExists("destination-source")) style.removeStyleSource("destination-source")
                    }
                }
            }



            MapEffect(currentPos) { mapView ->
                val mapboxMap = mapView.mapboxMap
                mapboxMap.getStyle { style ->
                    currentPos?.let { point ->
                        val id = "current-pos"
                        val layerId = "current-pos-layer"

                        if (style.styleSourceExists(id)) {
                            style.getSourceAs<GeoJsonSource>(id)
                                ?.data(Feature.fromGeometry(point).toJson())
                        } else {
                            style.addSource(geoJsonSource(id) {
                                data(
                                    Feature.fromGeometry(point).toJson()
                                )
                            })
                            style.addLayer(circleLayer(layerId, id) {
                                circleColor("#00FF00")
                                circleRadius(6.0)
                                circleStrokeWidth(2.0)
                                circleStrokeColor("#FFFFFF")
                            })
                        }
                    }
                }
            }

            MapEffect(isNavigating, currentPos) { mapView ->
                val mapboxMap = mapView.mapboxMap

                if (isNavigating && currentPos != null) {
                    val point = currentPos!!

                    // Buat animasi kamera agar smooth mengikuti posisi
                    val cameraOptions = mapboxMap.cameraState.toCameraOptions().toBuilder()
                        .center(point)
                        .zoom(18.0) // sesuaikan tingkat zoom
                        .bearing(bearing.toDouble()) // jaga orientasi kamera
                        .pitch(0.0) // efek 3D ringan
                        .build()

                    mapboxMap.easeTo(
                        cameraOptions,
                        mapAnimationOptions {
                            duration(1500L) // durasi animasi
                        }
                    )
                }
            }


        }
        if (isPathLoading) {
            Loader()
        }
    }
}
