package com.example.bolobudur.data.model

import com.google.gson.annotations.SerializedName

data class PoiFeature(
    val label: String,
    val poi: String,
    val lokasi: String,
    val lat: Double,
    val lon: Double
)

data class FloorData(
    val id: Int,
    val title: String,
    val items: List<PoiFeature>
)

data class ShortestPathResponse(
    val success: Boolean,
    val message: String,
    @SerializedName("total_distance") val totalDistance: Double,
    @SerializedName("path_nodes") val pathNodes: List<Int>,
    val geojson: GeoJson
)

data class GeoJson(
    val type: String,
    val features: List<GeoJsonFeature>
)

data class GeoJsonFeature(
    val type: String,
    val properties: GeoJsonProperties,
    val geometry: Geometry
)

data class GeoJsonProperties(
    @SerializedName("from_node") val fromNode: Int,
    @SerializedName("to_node") val toNode: Int,
    val distance: Double
)

data class Geometry(
    val type: String,
    val coordinates: List<List<Double>>
)

