package com.example.bolobudur.data.remote

import com.example.bolobudur.data.model.AuthResponse
import com.example.bolobudur.data.model.LoginRequest
import com.example.bolobudur.data.model.RegisterRequest
import com.example.bolobudur.data.model.UserProfile
import com.example.bolobudur.data.model.ChangePasswordRequest
import com.example.bolobudur.data.model.BasicResponse
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

    @GET("api/auth/profile")
    suspend fun getProfile(@Header("Authorization") token: String): UserProfile

    @PUT("api/auth/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body profile: String
    ): UserProfile

    @POST("api/auth/validate")
    suspend fun validateToken(@Header("Authorization") token: String): Response<Unit>

    @POST("api/auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<Unit>

    @PUT("api/auth/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): Response<BasicResponse>


    // 🟢 Validate token endpoint


//    @GET("api/auth/profile")
//    suspend fun getProfile(
//        @Header("Authorization") token: String
//    ): AuthResponse
//
//    @PUT("api/auth/profile")
//    suspend fun updateProfile(
//        @Header("Authorization") token: String,
//        @Body request: UpdateProfileRequest
//    ): AuthResponse

}
