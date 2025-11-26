package com.example.bolobudur.data.remote

import com.example.bolobudur.data.model.CategoryItem
import com.example.bolobudur.data.model.NearbyResponse
import com.example.bolobudur.data.model.SearchResponse
import com.example.bolobudur.data.model.SiteItem
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CulturalSiteApiService {

    @GET("api/poi/nearby")
    suspend fun  getNearbyPoi(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): NearbyResponse

    @GET("api/search")
    suspend fun searchPoi(
        @Query("keyword") keyword: String
    ): SearchResponse

    @GET("api/borobudurpedia/categories")
    suspend fun getAllCategories(): List<CategoryItem>

    @GET("api/borobudurpedia/categories/{id}")
    suspend fun getCategoryDetail(
        @Path("id") id: Int
    ): CategoryItem

    @GET("api/borobudurpedia/categories/{id}/sites")
    suspend fun getSitesByCategory(
        @Path("id") id: Int
    ): List<SiteItem>

    @GET("api/borobudurpedia/sites/search")
    suspend fun searchSites(
        @Query("q") query: String
    ): List<SiteItem>



}