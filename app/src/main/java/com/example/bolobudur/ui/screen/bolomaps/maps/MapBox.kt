package com.example.bolobudur.ui.screen.bolomaps.maps

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.bolobudur.ui.components.Loader
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.easeTo

@Composable
fun MapBox(
    viewModel: MapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val borobudur = Point.fromLngLat(110.2038, -7.6079)
    val viewportState = rememberMapViewportState {
        setCameraOptions {
            center(borobudur)
            zoom(17.0)
        }
    }

    val edges by viewModel.edges.collectAsState()
    val poi by viewModel.poi.collectAsState()
    val path by viewModel.shortestPath.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val isPathLoading by viewModel.isPathLoading.collectAsState()

    LaunchedEffect(Unit) {
       viewModel.loadMapData()
    }

    if (isLoading) {
        Loader()
    } else {
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = viewportState,
        ) {
            MapEffect(edges, poi, path) { mapView ->
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
                                com.mapbox.geojson.Feature.fromGeometry(startPoint),
                                com.mapbox.geojson.Feature.fromGeometry(endPoint)
                            )

                            val markerCollection = FeatureCollection.fromFeatures(markerFeatures)

                            if (style.styleSourceExists(markerSourceId)) {
                                style.getSourceAs<GeoJsonSource>(markerSourceId)?.data(markerCollection.toJson())
                            } else {
                                style.addSource(
                                    geoJsonSource(markerSourceId) {
                                        data(markerCollection.toJson())
                                    }
                                )
                            }

                            if (!style.styleLayerExists(markerLayerId)) {
                                style.addLayer(
                                    com.mapbox.maps.extension.style.layers.generated.circleLayer(
                                        markerLayerId,
                                        markerSourceId
                                    ) {
                                        circleColor("#FF0000") // merah
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

        }

        if(isPathLoading) {
            Loader()
        }
    }
}
