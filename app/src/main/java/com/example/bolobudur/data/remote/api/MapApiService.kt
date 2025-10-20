package com.example.bolobudur.data.remote.api

import com.mapbox.geojson.FeatureCollection
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface MapApiService {
    @GET("/api/edge/geojson")
    suspend fun getAllEdges(): Response<ResponseBody>

    @GET("/api/poi")
    suspend fun getAllPoi(): Response<ResponseBody>
}