package com.example.bolobudur.data.repository

import com.example.bolobudur.data.model.NearbyResponse
import com.example.bolobudur.data.remote.CulturalSiteApiService
import javax.inject.Inject

class CulturalSiteRepository @Inject constructor(
    private val api: CulturalSiteApiService
) {
    suspend fun getNearby(lat: Double, lon: Double): NearbyResponse {
        return api.getNearbyPoi(lat, lon)
    }
}