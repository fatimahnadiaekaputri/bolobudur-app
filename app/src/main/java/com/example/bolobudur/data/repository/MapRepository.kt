package com.example.bolobudur.data.repository

import android.util.Log
import com.example.bolobudur.data.remote.MapApiService
import com.example.bolobudur.data.model.ShortestPathResponse
import com.mapbox.geojson.FeatureCollection
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

    suspend fun getShortestPath(
        fromLat: Double,
        fromLon: Double,
        toLat: Double,
        toLon: Double
    ): ShortestPathResponse? {
        val response = api.getShortestPath(fromLat, fromLon, toLat, toLon)

        return if (response.isSuccessful && response.body() != null) {
            val body = response.body()!!

            // 🟢 Log data untuk debug
            Log.d("ShortestPath", "Total distance: ${body.totalDistance}")
            Log.d("ShortestPath", "Nodes: ${body.pathNodes}")

            body // langsung return seluruh objek JSON
        } else {
            null
        }
    }


}