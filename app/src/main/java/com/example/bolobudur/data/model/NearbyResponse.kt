package com.example.bolobudur.data.model

import com.google.gson.annotations.SerializedName

data class NearbyResponse(
    @SerializedName("floor_detected") val floorDetected: Int,
    @SerializedName("detected_areas") val detectedAreas: List<DetectedArea>
)

data class DetectedArea(
    @SerializedName("zone_name") val zoneName: String,
    val features: List<FeatureItem>
)

data class FeatureItem(
    val id: Int,
    val type: String,
    val geometry: NearbyGeometry,
    val properties: FeatureProperties
)

data class FeatureProperties(
    val label: String,
    val floor: Int,
    val radius: Double,
    @SerializedName("distance_m") val distanceM: Double,
    @SerializedName("cultural_site") val culturalSite: CulturalSite
)

data class CulturalSite(
    val name: String?,
    val description: String?,
    @SerializedName("image_url") val imageUrl: String?
)

data class NearbyGeometry(
    val type: String,
    val coordinates: List<Double>
)