package com.example.bolobudur.ui.model

data class FeatureData(
    val id: Int,
    val title: String,
    val description: String,
    val imageRes: Int,   // optional
    val imageUrl: String? = null // untuk gambar dari API
)