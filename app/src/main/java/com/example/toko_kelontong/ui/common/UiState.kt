package com.example.toko_kelontong.ui.common

/** State standar untuk setiap layar: loading, error, kosong, sukses (PRD C6). */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data object Empty : UiState<Nothing>
    data class Error(val message: String) : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
}
