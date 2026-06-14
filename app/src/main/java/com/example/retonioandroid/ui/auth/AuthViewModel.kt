package com.example.retonioandroid.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retonioandroid.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
)

class AuthViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun login(email: String, password: String) {
        if (!validate(email, password)) return
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            authRepository.login(email, password)
                .onSuccess { _state.update { s -> s.copy(isLoading = false, success = true) } }
                .onFailure { e -> _state.update { s -> s.copy(isLoading = false, error = e.message) } }
        }
    }

    fun register(email: String, password: String, displayName: String) {
        if (!validate(email, password)) return
        if (displayName.isBlank()) {
            _state.update { it.copy(error = "Ingresa tu nombre.") }
            return
        }
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            authRepository.register(email, password, displayName)
                .onSuccess { _state.update { s -> s.copy(isLoading = false, success = true) } }
                .onFailure { e -> _state.update { s -> s.copy(isLoading = false, error = e.message) } }
        }
    }

    private fun validate(email: String, password: String): Boolean {
        if (email.isBlank() || password.isBlank()) {
            _state.update { it.copy(error = "Email y contraseña son obligatorios.") }
            return false
        }
        return true
    }

    /** Se llama tras consumir el evento de éxito (ya navegamos). */
    fun consumeSuccess() = _state.update { it.copy(success = false) }

    fun clearError() = _state.update { it.copy(error = null) }
}
