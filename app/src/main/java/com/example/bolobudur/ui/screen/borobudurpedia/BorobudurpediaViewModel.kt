package com.example.bolobudur.ui.screen.borobudurpedia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bolobudur.data.model.CategoryItem
import com.example.bolobudur.data.model.CulturalSite
import com.example.bolobudur.data.model.SiteItem
import com.example.bolobudur.data.repository.CulturalSiteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BorobudurpediaViewModel @Inject constructor(
    private val repo: CulturalSiteRepository
) : ViewModel() {

    private val _site = MutableStateFlow<CulturalSite?>(null)
    val site: StateFlow<CulturalSite?> = _site

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage

    private val _descriptionChunks = MutableStateFlow<List<String>>(emptyList())
//    val descriptionChunks: StateFlow<List<String>> = _descriptionChunks

    private val _categories = MutableStateFlow<List<CategoryItem>>(emptyList())
    val categories: StateFlow<List<CategoryItem>> = _categories

    private val _isLoadingCategories = MutableStateFlow(false)
    val isLoadingCategories: StateFlow<Boolean> = _isLoadingCategories

    private val _isLoadingCategoriesAndSites = MutableStateFlow(true)
    val isLoadingCategoriesAndSites: StateFlow<Boolean> = _isLoadingCategoriesAndSites

    private val _category = MutableStateFlow<CategoryItem?>(null)
    val category: StateFlow<CategoryItem?> = _category

    private val _sites = MutableStateFlow<List<SiteItem>>(emptyList())
    val sites: StateFlow<List<SiteItem>> = _sites

    private val _searchedSites = MutableStateFlow<List<SiteItem>>(emptyList())
    val searchedSites: StateFlow<List<SiteItem>> = _searchedSites

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery



//    fun loadDummyData() {
//        viewModelScope.launch {
//            val dummy = CulturalSite(
//                name = "Pondasi",
//                description = "Istilah lainnya adalah fondamen, yaitu bagian bangunan yang tertanam di dalam tanah berfungsi sebagai penyangga dinding atau tiang. Bentuk pondasi disesuaikan dengan denah bangunan. " +
//                        "Pondasi berperan penting dalam menahan beban keseluruhan struktur agar tetap stabil dan aman. " +
//                        "Biasanya, pondasi dibuat dari batu kali, beton bertulang, atau bahan kuat lainnya tergantung jenis bangunannya.",
//                imageUrl = "https://www.sustainlifetoday.com/wp-content/uploads/2025/05/candi-borobudur-1.jpg"
//            )
//            _site.value = dummy
//            _descriptionChunks.value = dummy.description?.splitToChunks(35) ?: emptyList()
//        }
//    }

    fun loadCategoryAndSites(id: Int) {
        // Kalau kategori sama sebelumnya, jangan fetch lagi
        if (_category.value?.category_id == id && _sites.value.isNotEmpty()) return

        viewModelScope.launch {
            try {
                _isLoadingCategoriesAndSites.value = true
                _category.value = repo.getCategoryDetail(id)
                _sites.value = repo.getSitesByCategory(id)
            } finally {
                _isLoadingCategoriesAndSites.value = false
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchedSites.value = emptyList()
            return
        }
        searchSites(query)
    }


    private fun loadCategories() {
        if (_categories.value.isNotEmpty()) return

        viewModelScope.launch {
            try {
                _isLoadingCategories.value = true

                val result = repo.getAllCategories()
                _categories.value = result

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoadingCategories.value = false
            }
        }
    }

    fun searchSites(query: String) {
        viewModelScope.launch {
            try {
                _searchedSites.value = repo.searchSites(query)
            } catch (e: Exception) {
                e.printStackTrace()
                _searchedSites.value = emptyList()
            }
        }
    }




    init {
        loadCategories()
    }

    fun loadSite(name: String, description: String, imageUrl: String?) {
        viewModelScope.launch {
            val chunks = description.splitToChunks(35)

            val siteData = CulturalSite(
                name = name,
                description = description,
                imageUrl = imageUrl
            )
            _site.value = siteData
            _descriptionChunks.value = chunks
            _currentPage.value = 0
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
