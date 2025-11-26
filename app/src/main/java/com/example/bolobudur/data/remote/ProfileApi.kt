package com.example.bolobudur.data.remote

import okhttp3.RequestBody
import retrofit2.http.Part
import com.example.bolobudur.data.model.BasicResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.PUT

interface ProfileApi {
    @Multipart
    @PUT("api/auth/profile")
    suspend fun updateProfile(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<BasicResponse>
}