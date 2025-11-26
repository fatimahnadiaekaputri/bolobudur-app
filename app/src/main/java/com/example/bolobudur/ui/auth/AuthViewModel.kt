package com.example.bolobudur.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bolobudur.data.local.TokenManager
import com.example.bolobudur.data.model.UpdateProfileRequest
import com.example.bolobudur.data.model.UserProfile
import com.example.bolobudur.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

//    var userProfile by mutableStateOf<UserProfile?>(null)
//        private set

//    var isLoading by mutableStateOf(false)
//        private set
//
//    var errorMessage by mutableStateOf<String?>(null)
//        private set
//
//    var isSuccess by mutableStateOf(false)
//        private set

//    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
//    val authState: StateFlow<AuthState> = _authState
//
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    // 🟢 Register
    fun register(name: String, email: String, password: String) = viewModelScope.launch {
        _isLoading.value = true
        try {
            val result = repository.register(name, email, password)
            if (result.isSuccess) {
                _errorMessage.value = null
                _isSuccess.value = true
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
        } catch (e: Exception) {
            _errorMessage.value = e.message
        } finally {
            _isLoading.value = false
        }
    }

    // 🟢 Login
    fun login(email: String, password: String) = viewModelScope.launch {
        _isLoading.value = true
        try {
            val result = repository.login(email, password)
            if (result.isSuccess) {
                _errorMessage.value = null
                _isSuccess.value = true
            } else {
                _errorMessage.value = "Email atau password salah"
            }
        } catch (e: Exception) {
            _errorMessage.value = "Terjadi kesalahan saat login"
        } finally {
            _isLoading.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun loadProfile() = viewModelScope.launch {
        _isLoading.value = true
        try {
            _userProfile.value = repository.getProfile()
        } catch (e: Exception) {
            _errorMessage.value = e.message
        } finally {
            _isLoading.value = false
        }
    }



    // 🟢 Validate (dipakai di Splash)
    fun validateToken(onValid: () -> Unit, onInvalid: () -> Unit) = viewModelScope.launch {
        if (repository.validateToken()) onValid() else onInvalid()
    }

    // 🟢 Logout
    fun logout(onLogout: () -> Unit) = viewModelScope.launch {
        try {
            repository.logout()
            _isSuccess.value = false
            _errorMessage.value = null
            onLogout()
        } catch (e: Exception) {
            _errorMessage.value = e.message
        }
    }

    // 🔹 CEK LOGIN
    fun isUserLoggedIn(): Boolean {
        return tokenManager.getToken() != null
    }

    fun hasToken(): Boolean = repository.hasToken()
}
