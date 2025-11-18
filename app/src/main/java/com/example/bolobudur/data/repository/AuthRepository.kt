package com.example.bolobudur.data.repository

import com.example.bolobudur.data.local.TokenManager
import com.example.bolobudur.data.model.ChangePasswordRequest
import com.example.bolobudur.data.model.LoginRequest
import com.example.bolobudur.data.model.RegisterRequest
import com.example.bolobudur.data.model.UserProfile
import com.example.bolobudur.data.model.BasicResponse
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
    private val profileApi: ProfileApi,
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
        return api.getProfile("Bearer $token")
    }

    suspend fun updateProfile(name: String, email: String, imageFile: File?): Result<String> {
        return try {
            val nameRB = name.toRequestBody("text/plain".toMediaType())
            val emailRB = email.toRequestBody("text/plain".toMediaType())

            val imagePart = imageFile?.let {
                val req = it.asRequestBody("image/*".toMediaType())
                MultipartBody.Part.createFormData("image", it.name, req)
            }

            val response = profileApi.updateProfile(nameRB, emailRB, imagePart)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.message)
            } else {
                Result.failure(Exception(response.errorBody()?.string()))
            }

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
