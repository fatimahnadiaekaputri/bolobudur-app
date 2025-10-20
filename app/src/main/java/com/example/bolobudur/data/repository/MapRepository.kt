package com.example.bolobudur.data.repository

import com.example.bolobudur.data.remote.api.MapApiService
import com.mapbox.geojson.FeatureCollection
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class MapRepository @Inject constructor(
    private val api: MapApiService
) {
    suspend fun getEdges(): FeatureCollection? {
        val response = api.getAllEdges()
        return if (response.isSuccessful && response.body() != null) {
            FeatureCollection.fromJson(response.body()!!.string())
        } else null
    }

    suspend fun getPoi(): FeatureCollection? {
        val response = api.getAllPoi()
        return if (response.isSuccessful && response.body() != null) {
            FeatureCollection.fromJson(response.body()!!.string())
        } else null
    }
}