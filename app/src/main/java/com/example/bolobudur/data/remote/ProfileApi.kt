package com.example.bolobudur.data.remote

import com.example.bolobudur.data.model.UserProfile
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface ProfileApi {

    @GET("api/auth/profile")
    suspend fun getProfile(): UserProfile

    @PUT("api/auth/profile")
    suspend fun updateProfile(@Body profile: Map<String, String>): Map<String, String>
}
