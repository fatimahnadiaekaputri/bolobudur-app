package com.example.bolobudur.data.remote

import com.example.bolobudur.data.model.AuthResponse
import com.example.bolobudur.data.model.LoginRequest
import com.example.bolobudur.data.model.RegisterRequest
import com.example.bolobudur.data.model.UpdateProfileRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApi {

    // 🟢 Register endpoint
    @POST("/api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    // 🟢 Login endpoint
    @POST("/api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    // 🟢 Validate token endpoint
    @POST("/api/auth/validate")
    suspend fun validate(
        @Header("Authorization") token: String? = null
    ): Response<AuthResponse>

    // 🟢 Logout endpoint
    @POST("/api/auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String? = null
    ): Response<AuthResponse>

    @GET("api/auth/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): AuthResponse

    @PUT("api/auth/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): AuthResponse

}
