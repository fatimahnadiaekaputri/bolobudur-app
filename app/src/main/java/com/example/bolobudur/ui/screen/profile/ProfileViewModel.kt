package com.example.bolobudur.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bolobudur.data.model.AuthResponse
import com.example.bolobudur.data.repository.AuthRepository
import com.example.bolobudur.data.repository.ProfileRepository
import com.example.bolobudur.data.local.TokenManager
import com.example.bolobudur.data.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _profileState = MutableStateFlow<UserProfile?>(null)
    val profileState: StateFlow<UserProfile> = _profileState as StateFlow<UserProfile>

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _profileState.value = authRepository.getProfile()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

//    fun updateProfile(name: String, email: String) {
//        viewModelScope.launch {
//            _loading.value = true
//            try {
//                repository.updateProfile(name, email)
//                loadProfile()
//            } catch (e: Exception) {
//                _error.value = e.message
//            } finally {
//                _loading.value = false
//            }
//        }
//    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearToken()
            _isSuccess.value = false
        }
    }
}
