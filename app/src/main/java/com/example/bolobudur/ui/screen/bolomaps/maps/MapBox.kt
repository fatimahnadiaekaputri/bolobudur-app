package com.example.bolobudur.ui.screen.bolomaps.maps

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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

@Composable
fun MapBox(
    viewModel: MapViewModel = hiltViewModel(),
    navigationViewModel: NavigationViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
//    val borobudur = Point.fromLngLat(110.2038, -7.6079)
    val testingTeti = Point.fromLngLat(110.37152147214815, -7.765591698479714)
    val viewportState = rememberMapViewportState {
//        setCameraOptions {
//            center(testingTeti)
//            zoom(17.0)
//        }
    }

    val edges by viewModel.edges.collectAsState()
    val poi by viewModel.poi.collectAsState()
    val path by viewModel.shortestPath.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val isPathLoading by viewModel.isPathLoading.collectAsState()
    val currentPos by navigationViewModel.currentPosition.collectAsState()

    val isArrived by navigationViewModel.isArrived.collectAsState()

    val fullPath by navigationViewModel.fullPathLine.collectAsState()
    val isNavigating = fullPath != null

    LaunchedEffect(Unit) {
        viewModel.loadMapData()
        viewportState.setCameraOptions {
            center(testingTeti)
            zoom(17.0)
            pitch(0.0)
            bearing(0.0)
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

                        if (style.styleLayerExists("route-markers-layer")) {
                            style.removeStyleLayer("route-markers-layer")
                        }
                        if (style.styleSourceExists("route-markers-source")) {
                            style.removeStyleSource("route-markers-source")
                        }

                        mapboxMap.getStyle()?.let {
                            it.removeStyleLayer("shortest-path-layer")
                            it.removeStyleSource("shortest-path-source")
                            it.removeStyleLayer("route-markers-layer")
                            it.removeStyleSource("route-markers-source")
                        }

                        viewportState.setCameraOptions {
                            center(Point.fromLngLat(110.2038, -7.6079))
                            zoom(17.0)
                            pitch(0.0)
                            bearing(0.0)
                        }

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
                        val markerSourceId = "route-markers-source"
                        val markerLayerId = "route-markers-layer"

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

                            val markerFeatures = listOf(
                                Feature.fromGeometry(startPoint),
                                Feature.fromGeometry(endPoint)
                            )

                            val markerCollection = FeatureCollection.fromFeatures(markerFeatures)

                            if (style.styleSourceExists(markerSourceId)) {
                                style.getSourceAs<GeoJsonSource>(markerSourceId)
                                    ?.data(markerCollection.toJson())
                            } else {
                                style.addSource(
                                    geoJsonSource(markerSourceId) {
                                        data(markerCollection.toJson())
                                    }
                                )
                            }

                            if (!style.styleLayerExists(markerLayerId)) {
                                style.addLayer(
                                    circleLayer(
                                        markerLayerId,
                                        markerSourceId
                                    ) {
                                        circleColor("#FF0000")
                                        circleRadius(6.0)
                                        circleStrokeWidth(2.0)
                                        circleStrokeColor("#FFFFFF")
                                    }
                                )
                            }

                            // --- 🔹 Zoom kamera biar semua path kelihatan ---
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
            // POSISI USER DAN KAMERA NAVIGASI
            MapEffect(fullPath, currentPos, isArrived) { mapView ->
                val mapboxMap = mapView.mapboxMap
                mapboxMap.getStyle { style ->

                    if (!isArrived) {
                        // 🔹 Full path (biru)
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
                    } else {
                        // 🔹 Hapus semua garis kalau sudah sampai
                        if (style.styleLayerExists("full-path-layer")) style.removeStyleLayer("full-path-layer")
                        if (style.styleSourceExists("full-path-source")) style.removeStyleSource("full-path-source")
                        if (style.styleLayerExists("dynamic-line-layer")) style.removeStyleLayer("dynamic-line-layer")
                        if (style.styleSourceExists("dynamic-line-source")) style.removeStyleSource(
                            "dynamic-line-source"
                        )
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

        }
        if (isPathLoading) {
            Loader()
        }
    }
}
