package com.example.bolobudur.ui.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bolobudur.data.repository.AuthRepository
import com.example.bolobudur.data.local.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _navigateToNext = MutableStateFlow("")
    val navigateToNext: StateFlow<String> get() = _navigateToNext

    init {
        startSplash()
    }

    private fun startSplash() {
        viewModelScope.launch {
            delay(5000) // delay 5 detik

            val token = tokenManager.getToken()
            if (!token.isNullOrEmpty()) {
                // token ada, user dianggap masih login
                _navigateToNext.value = "home"
            } else {
                // token tidak ada, arahkan ke login
                _navigateToNext.value = "login"
            }
        }
    }

    private fun isUserLoggedIn(): Boolean {
        return false // sementara, nanti diganti cek login beneran
    }
}
