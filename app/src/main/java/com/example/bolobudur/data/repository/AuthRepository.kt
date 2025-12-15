package com.example.bolobudur.data.repository

import com.example.bolobudur.data.local.TokenManager
import com.example.bolobudur.data.model.ChangePasswordRequest
import com.example.bolobudur.data.model.LoginRequest
import com.example.bolobudur.data.model.RegisterRequest
import com.example.bolobudur.data.model.UserProfile
import com.example.bolobudur.data.model.BasicResponse
import com.example.bolobudur.data.model.UpdateProfileRequest
import com.example.bolobudur.data.remote.AuthApi
import com.example.bolobudur.data.remote.ProfileApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: AuthApi,
    private val tokenManager: TokenManager
) {

    // 🟢 Register
    suspend fun register(name: String, email: String, password: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.register(RegisterRequest(name, email, password))
                if (response.isSuccessful) Result.success(Unit)
                else Result.failure(Exception(response.errorBody()?.string() ?: "Register failed"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // 🟢 Login
    suspend fun login(email: String, password: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body()?.token != null) {
                    val token = response.body()!!.token!!
                    tokenManager.saveToken(token)
                    Result.success(token)
                } else {
                    Result.failure(Exception(response.errorBody()?.string() ?: "Login failed"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getProfile(): UserProfile? {
        val token = tokenManager.getToken() ?: return null

        return try {
            val response = api.getProfile("Bearer $token")
            if (response.status) {
                response.data
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }




    suspend fun updateProfile(
        name: String,
        email: String,
        imageFile: File?
    ): Result<Unit> {
        val token = tokenManager.getToken()
            ?: return Result.failure(Exception("Unauthorized"))

        return try {
            // 1️⃣ Upload image (optional)
            val imageUrl = imageFile?.let {
                val requestBody = it
                    .asRequestBody("image/*".toMediaType())
                val part = MultipartBody.Part
                    .createFormData("image", it.name, requestBody)

                val uploadResponse =
                    api.uploadProfileImage("Bearer $token", part)

                uploadResponse.url
            }

            // 2️⃣ Update profile
            val request = UpdateProfileRequest(
                name = name,
                email = email,
                image_profile = imageUrl
            )

            api.updateProfile("Bearer $token", request)

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // 🟢 Validate token
    suspend fun validateToken(): Boolean {
        val token = tokenManager.getToken() ?: return false
        val response = api.validateToken("Bearer $token")
        return response.isSuccessful
    }

    suspend fun logout() {
        val token = tokenManager.getToken() ?: return
        api.logout("Bearer $token")
        tokenManager.clearToken()
    }

    suspend fun changePassword(oldPassword: String, newPassword: String): Result<String> {
        return try {
            val request = ChangePasswordRequest(oldPassword, newPassword)
            val response = api.changePassword(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.message)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 🟢 Logout

    // 🟡 Cek token lokal (untuk Splash)
    fun hasToken(): Boolean = tokenManager.getToken() != null

}
