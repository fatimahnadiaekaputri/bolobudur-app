package com.example.bolobudur.ui.screen.home

import androidx.lifecycle.ViewModel
import com.example.bolobudur.R
import com.example.bolobudur.ui.model.FeatureData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(): ViewModel(){
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadFeatures()
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

    fun onSearchQueryChange(newValue: String) {
        _uiState.update { it.copy(searchQuery = newValue) }
    }
}

data class HomeUiState(
    val userName: String = "Hanifah",
    val searchQuery: String = "",
    val features: List<FeatureData> = emptyList()
)