package com.example.bolobudur.ui.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

    private val _navigateToNext = MutableStateFlow(false)
    val navigateToNext: StateFlow<Boolean> get() = _navigateToNext

    init {
        startSplash()
    }

    private fun startSplash() {
        viewModelScope.launch {
            delay(5000) // delay 5 detik
            _navigateToNext.value = true
        }
    }
}
