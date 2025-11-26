package com.example.bolobudur.data.repository

import com.example.bolobudur.data.model.CategoryItem
import com.example.bolobudur.data.model.NearbyResponse
import com.example.bolobudur.data.model.SearchResponse
import com.example.bolobudur.data.model.SiteItem
import com.example.bolobudur.data.remote.CulturalSiteApiService
import javax.inject.Inject

class CulturalSiteRepository @Inject constructor(
    private val api: CulturalSiteApiService
) {
    suspend fun getNearby(lat: Double, lon: Double): NearbyResponse {
        return api.getNearbyPoi(lat, lon)
    }

    suspend fun searchPoi(keyword: String): SearchResponse{
        return api.searchPoi(keyword)
    }

    suspend fun getAllCategories(): List<CategoryItem> {
        return api.getAllCategories()
    }

    suspend fun getCategoryDetail(id: Int): CategoryItem {
        return api.getCategoryDetail(id)
    }

    suspend fun getSitesByCategory(id: Int): List<SiteItem> {
        return api.getSitesByCategory(id)
    }

    suspend fun searchSites(query: String): List<SiteItem> {
        return api.searchSites(query)
    }


}