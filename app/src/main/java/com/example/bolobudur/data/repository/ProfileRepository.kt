package com.example.bolobudur.data.repository

import com.example.bolobudur.data.local.TokenManager
import com.example.bolobudur.data.model.UpdateProfileRequest
import com.example.bolobudur.data.model.UserProfile
import com.example.bolobudur.data.remote.AuthApi
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val api: AuthApi,
    private val tokenManager: TokenManager
) {
    suspend fun getProfile(): UserProfile? {
        val token = tokenManager.getToken() ?: return null
        return api.getProfile("Bearer $token")
    }


}
