package com.example.bolobudur.data.model

data class SearchResponse(
    val success: Boolean,
    val data: SearchData
)

data class SearchData(
    val poi: FeatureCollection<PoiProperties>,
    val categories: FeatureCollection<CategoryProperties>,
    val sites: FeatureCollection<SiteProperties>
)

data class FeatureCollection<T>(
    val type: String,
    val features: List<Feature<T>>
)

data class Feature<T>(
    val type: String,
    val geometry: NearbyGeometry?,
    val properties: T
)

data class PoiProperties(
    val label: String,
    val poi: String,
    val site_id: Int?,
    val lokasi: String,
    val source_type: String? = null
)

data class CategoryProperties(
    val label: String,
    val poi: String,
    val description: String,
    val source_type: String? = null
)

data class SiteProperties(
    val label: String,
    val poi: String,
    val image_url: String,
    val description: String,
    val source_type: String? = null
)

data class CategoryItem(
    val category_id: Int,
    val name: String,
    val description: String
)

data class SiteItem(
    val site_id: Int,
    val name: String,
    val description: String,
    val image_url: String
)


