package com.example.bolobudur.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bolobudur.data.local.TokenManager
import com.example.bolobudur.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    // 🟢 Register
    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = authRepository.register(name, email, password)
            result
                .onSuccess {
                    _isSuccess.value = true
                }
                .onFailure { e ->
                    _errorMessage.value = e.message ?: "Pendaftaran gagal"
                }

            _isLoading.value = false
        }
    }

    // 🟢 Login
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = authRepository.login(email, password)
            result
                .onSuccess { token ->
                    tokenManager.saveToken(token)
                    _isSuccess.value = true
                }
                .onFailure { e ->
                    _errorMessage.value = e.message ?: "Login gagal"
                }

            _isLoading.value = false
        }
    }

    // 🟢 Validate (dipakai di Splash)
    suspend fun validateToken(): Boolean = authRepository.validateToken()

    // 🟢 Logout
    fun logout() {
        viewModelScope.launch {
            tokenManager.clearToken()
            _isSuccess.value = false
        }
    }

    // 🔹 CEK LOGIN
    fun isUserLoggedIn(): Boolean {
        return tokenManager.getToken() != null
    }

    fun hasToken(): Boolean = authRepository.hasToken()
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Registered : AuthState()
    object LoggedIn : AuthState()
    object LoggedOut : AuthState()
    data class Error(val message: String) : AuthState()
}
