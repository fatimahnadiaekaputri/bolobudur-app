package com.example.bolobudur.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bolobudur.R
import com.example.bolobudur.data.model.SearchResponse
import com.example.bolobudur.data.repository.CulturalSiteRepository
import com.example.bolobudur.data.repository.AuthRepository
import com.example.bolobudur.ui.model.FeatureData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CulturalSiteRepository,
    private val profileRepository: AuthRepository
): ViewModel(){
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadFeatures()
        loadUserName()
        loadUserProfile()
    }

    private fun loadFeatures() {
        _uiState.update {
            it.copy(
                features = listOf(
                    FeatureData(1, "BoloMaps", "Jelajahi setiap sudut Borobudur...", R.drawable.bolomaps_feature),
                    FeatureData(2, "BoloFind", "Kenali arca dan relief secara otomatis...", R.drawable.bolofind_feature)
                )
            )
        }
    }

    private fun loadUserName() {
        viewModelScope.launch {
            val profile = profileRepository.getProfile()

            _uiState.update {
                it.copy(
                    userName = profile?.name ?: "Guest"
                )
            }
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val profile = profileRepository.getProfile()
            _uiState.update {
                it.copy(
                    userName = profile?.name.orEmpty(),
                    imageProfileUrl = profile?.image_profile
                )
            }
        }
    }




    fun onSearchQueryChange(newValue: String) {
        _uiState.update { it.copy(searchQuery = newValue) }

        if (newValue.isBlank()) {
            _uiState.update { it.copy(searchResult = null) }
            return
        }

        // Live Search
        viewModelScope.launch {
            try {
                val response = repository.searchPoi(newValue)
                _uiState.update { it.copy(searchResult = response) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearSearch() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                searchResult = null,
                isLoading = false
            )
        }
    }


    fun onSearchSubmit() {
        val keyword = uiState.value.searchQuery
        if (keyword.isNotBlank()) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) } // mulai loading

                try {
                    val response = repository.searchPoi(keyword)
                    _uiState.update {
                        it.copy(
                            searchResult = response,
                            isLoading = false // selesai
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _uiState.update { it.copy(isLoading = false) } // selesai walau error
                }
            }
        }
    }

}

data class HomeUiState(
    val userName: String = "Guest",
    val imageProfileUrl: String? = null,
    val searchQuery: String = "",
    val features: List<FeatureData> = emptyList(),
    val searchResult: SearchResponse? = null,
    val isLoading: Boolean = false
)