package com.example.bolobudur.data.repository

import com.example.bolobudur.data.remote.ProfileApi
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val api: ProfileApi
) {
    suspend fun getProfile() = api.getProfile()
    suspend fun updateProfile(name: String, email: String) =
        api.updateProfile(mapOf("name" to name, "email" to email))
}
