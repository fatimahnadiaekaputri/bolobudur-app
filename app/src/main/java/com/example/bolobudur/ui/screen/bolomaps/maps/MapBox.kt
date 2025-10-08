package com.example.bolobudur.ui.screen.bolomaps.maps

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState

@Composable
fun MapBox() {
    val borobudur = Point.fromLngLat(110.2038, -7.6079)

    val viewportState = rememberMapViewportState {
        setCameraOptions {
            center(borobudur)
            zoom(15.0)
            pitch(0.0)
            bearing(0.0)
        }
    }

    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = viewportState
    )
}
