package com.example.bolobudur.ui.screen.bolomaps.maps

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.bolobudur.ui.components.Loader
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState

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
    val isLoading by viewModel.loading.collectAsState()

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
            // Panggil fungsi addLineAndLabelLayer & addPoiLayers setelah style siap
            MapEffect(edges, poi) { mapView ->
                val mapboxMap = mapView.mapboxMap
                mapboxMap.getStyle { style ->

                    edges?.let {
                        style.addLineAndLabelLayer(it.toJson())
                    }

                    poi?.let {
                        style.addPoiLayers(context, it.toJson())
                    }
                }

            }

        }
    }
}
