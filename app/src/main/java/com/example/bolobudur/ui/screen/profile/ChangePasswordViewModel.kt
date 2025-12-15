package com.example.bolobudur.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bolobudur.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val successMessage: String? = null,
        val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun changePassword(oldPass: String, newPass: String) {
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)

            val result = authRepository.changePassword(oldPass, newPass)

            result.onSuccess { message ->
                _uiState.value = UiState(
                    isLoading = false,
                    successMessage = message
                )
            }

            result.onFailure { err ->
                _uiState.value = UiState(
                    isLoading = false,
                    errorMessage = err.message ?: "Failed to change password"
                )
            }
        }
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

}
