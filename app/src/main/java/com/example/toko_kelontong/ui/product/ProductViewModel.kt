package com.example.toko_kelontong.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toko_kelontong.data.model.Product
import com.example.toko_kelontong.data.repository.ApiResult
import com.example.toko_kelontong.data.repository.ProductRepository
import com.example.toko_kelontong.ui.common.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Contoh pola MVVM untuk daftar + pencarian. Pola yang sama dipakai
 * ulang untuk kategori, riwayat transaksi, dll.
 */
class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _products = MutableStateFlow<UiState<List<Product>>>(UiState.Loading)
    val products: StateFlow<UiState<List<Product>>> = _products.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private var searchJob: Job? = null

    init {
        load()
    }

    fun onQueryChange(value: String) {
        _query.value = value
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(DEBOUNCE_MS) // hindari request di setiap ketikan
            load()
        }
    }

    fun load() {
        _products.value = UiState.Loading
        viewModelScope.launch {
            _products.value = when (val result = repository.getProducts(search = _query.value)) {
                is ApiResult.Success -> {
                    val list = result.data.orEmpty()
                    if (list.isEmpty()) UiState.Empty else UiState.Success(list)
                }
                is ApiResult.Error -> UiState.Error(result.message)
            }
        }
    }

    private companion object {
        const val DEBOUNCE_MS = 400L
    }
}
