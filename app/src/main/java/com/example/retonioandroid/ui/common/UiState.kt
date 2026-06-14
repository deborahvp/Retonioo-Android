package com.example.retonioandroid.ui.common

/**
 * Estado genérico de pantalla que exponen los ViewModels vía StateFlow.
 * Cubre los tres casos que toda pantalla debe manejar: cargando / datos / error.
 */
data class UiState<T>(
    val isLoading: Boolean = false,
    val data: T? = null,
    val error: String? = null,
)
