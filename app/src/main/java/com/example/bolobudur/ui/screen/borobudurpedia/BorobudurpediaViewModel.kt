package com.example.bolobudur.ui.screen.borobudurpedia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bolobudur.data.model.CulturalSite
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BorobudurpediaViewModel @Inject constructor() : ViewModel() {

    private val _site = MutableStateFlow<CulturalSite?>(null)
    val site: StateFlow<CulturalSite?> = _site

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage

    private val _descriptionChunks = MutableStateFlow<List<String>>(emptyList())
    val descriptionChunks: StateFlow<List<String>> = _descriptionChunks

    fun loadDummyData() {
        viewModelScope.launch {
            val dummy = CulturalSite(
                name = "Pondasi",
                description = "Istilah lainnya adalah fondamen, yaitu bagian bangunan yang tertanam di dalam tanah berfungsi sebagai penyangga dinding atau tiang. Bentuk pondasi disesuaikan dengan denah bangunan. " +
                        "Pondasi berperan penting dalam menahan beban keseluruhan struktur agar tetap stabil dan aman. " +
                        "Biasanya, pondasi dibuat dari batu kali, beton bertulang, atau bahan kuat lainnya tergantung jenis bangunannya.",
                imageUrl = "https://www.sustainlifetoday.com/wp-content/uploads/2025/05/candi-borobudur-1.jpg"
            )
            _site.value = dummy
            _descriptionChunks.value = dummy.description?.splitToChunks(35) ?: emptyList()
        }
    }

    fun getCurrentDescription(): String {
        val chunks = _descriptionChunks.value
        return if (chunks.isNotEmpty()) chunks[_currentPage.value] else ""
    }

    fun nextPage() {
        val chunks = _descriptionChunks.value
        if (_currentPage.value < chunks.size - 1) {
            _currentPage.value++
        }
    }

    fun prevPage() {
        if (_currentPage.value > 0) {
            _currentPage.value--
        }
    }

    fun hasNext(): Boolean {
        val chunks = _descriptionChunks.value
        return _currentPage.value < chunks.size - 1
    }

    fun hasPrev(): Boolean {
        return _currentPage.value > 0
    }
}

private fun String.splitToChunks(maxWords: Int): List<String> {
    val words = this.split(" ")
    val chunks = mutableListOf<String>()
    var currentChunk = StringBuilder()

    for (word in words) {
        val currentWordCount = currentChunk.toString().split(" ").size
        if (currentWordCount < maxWords) {
            currentChunk.append("$word ")
        } else {
            chunks.add(currentChunk.toString().trim())
            currentChunk = StringBuilder("$word ")
        }
    }
    if (currentChunk.isNotEmpty()) chunks.add(currentChunk.toString().trim())
    return chunks
}
