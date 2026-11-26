package com.example.toko_kelontong.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toko_kelontong.data.repository.ApiResult
import com.example.toko_kelontong.data.repository.AuthRepository
import com.example.toko_kelontong.ui.common.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val namaError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val success: Boolean = false,
)

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun login(email: String, password: String) {
        val emailError = Validators.email(email)
        val passwordError = Validators.required(password, "Password")
        if (emailError != null || passwordError != null) {
            _state.update {
                it.copy(emailError = emailError, passwordError = passwordError, errorMessage = null)
            }
            return
        }
        submit { repository.login(email.trim(), password) }
    }

    fun register(nama: String, email: String, password: String) {
        val namaError = Validators.required(nama, "Nama")
        val emailError = Validators.email(email)
        val passwordError = Validators.password(password)
        if (namaError != null || emailError != null || passwordError != null) {
            _state.update {
                it.copy(
                    namaError = namaError,
                    emailError = emailError,
                    passwordError = passwordError,
                    errorMessage = null,
                )
            }
            return
        }
        submit { repository.register(nama.trim(), email.trim(), password) }
    }

    private fun submit(call: suspend () -> ApiResult<*>) {
        if (_state.value.isLoading) return
        _state.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            _state.value = when (val result = call()) {
                is ApiResult.Success -> AuthUiState(success = true)
                is ApiResult.Error -> AuthUiState(errorMessage = result.message)
            }
        }
    }
}
