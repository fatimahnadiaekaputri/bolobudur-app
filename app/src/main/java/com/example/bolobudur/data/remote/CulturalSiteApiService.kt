package com.example.bolobudur.data.remote

import com.example.bolobudur.data.model.NearbyResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CulturalSiteApiService {

    @GET("api/poi/nearby")
    suspend fun  getNearbyPoi(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): NearbyResponse
}