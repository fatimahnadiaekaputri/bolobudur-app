package com.example.bolobudur.data.remote.api

import com.example.bolobudur.data.remote.model.ShortestPathResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MapApiService {
    @GET("/api/edge/geojson")
    suspend fun getAllEdges(): Response<ResponseBody>

    @GET("/api/poi")
    suspend fun getAllPoi(): Response<ResponseBody>

    @GET("/api/path/shortest")
    suspend fun getShortestPath(
        @Query("from_lat") fromLat: Double,
        @Query("from_lon") fromLon: Double,
        @Query("to_lat") toLat: Double,
        @Query("to_lon") toLon: Double
    ) : Response<ShortestPathResponse>
}