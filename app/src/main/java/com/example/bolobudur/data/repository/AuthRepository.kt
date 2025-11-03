package com.example.bolobudur.data.repository

import com.example.bolobudur.data.local.TokenManager
import com.example.bolobudur.data.model.LoginRequest
import com.example.bolobudur.data.model.RegisterRequest
import com.example.bolobudur.data.remote.AuthApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    // 🟢 Validate token
    suspend fun validateToken(): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = api.validate()
            response.isSuccessful && response.body()?.valid == true
        } catch (e: Exception) {
            false
        }
    }

    // 🟢 Logout
    suspend fun logout(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.logout()
            tokenManager.clearToken()
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Logout failed"))
        } catch (e: Exception) {
            tokenManager.clearToken()
            Result.failure(e)
        }
    }

    // 🟡 Cek token lokal (untuk Splash)
    fun hasToken(): Boolean = tokenManager.getToken() != null
}
