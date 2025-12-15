package com.example.bolobudur.ui.screen.profile

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bolobudur.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UpdateProfileViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdateProfileUiState())
    val uiState: StateFlow<UpdateProfileUiState> = _uiState

    init {
        loadCurrentProfile()
    }

    private fun loadCurrentProfile() {
        viewModelScope.launch {
            try {
                val profile = repository.getProfile()
                _uiState.update {
                    it.copy(
                        name = profile?.name.orEmpty(),
                        email = profile?.email.orEmpty(),
                        imageProfileUrl = profile?.image_profile
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.message)
                }
            }
        }
    }

    fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Cannot open URI")

        val file = File(
            context.cacheDir,
            "profile_${System.currentTimeMillis()}.jpg"
        )

        file.outputStream().use { output ->
            inputStream.copyTo(output)
        }

        return file
    }


    fun updateProfile(
        context: Context,
        name: String,
        email: String,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val imageFile = imageUri?.let {
                    uriToFile(context, it)
                }

                repository.updateProfile(
                    name = name,
                    email = email,
                    imageFile = imageFile
                )

                _uiState.update {
                    it.copy(isLoading = false, isSuccess = true)
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }
}


data class UpdateProfileUiState(
    val name: String = "",
    val email: String = "",
    val imageProfileUrl: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

