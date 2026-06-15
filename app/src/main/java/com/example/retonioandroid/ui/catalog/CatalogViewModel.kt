package com.example.retonioandroid.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retonioandroid.data.repository.GarmentRepository
import com.example.retonioandroid.domain.model.Garment
import com.example.retonioandroid.domain.model.GarmentCategory
import com.example.retonioandroid.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CatalogUiState(
    val garments: UiState<List<Garment>> = UiState(isLoading = true),
    val selectedCategory: GarmentCategory? = null,
    val selectedSize: String? = null,
    val availableSizes: List<String> = emptyList(),
)

class CatalogViewModel(
    private val repository: GarmentRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CatalogUiState())
    val state: StateFlow<CatalogUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun selectCategory(category: GarmentCategory?) {
        _state.update { it.copy(selectedCategory = category) }
        load()
    }

    fun selectSize(size: String?) {
        _state.update { it.copy(selectedSize = size) }
        load()
    }

    fun refresh() = load()

    private fun load() {
        val current = _state.value
        _state.update { it.copy(garments = it.garments.copy(isLoading = true, error = null)) }
        viewModelScope.launch {
            repository.getGarments(
                category = current.selectedCategory?.api,
                size = current.selectedSize,
            ).onSuccess { list ->
                _state.update { s ->
                    s.copy(
                        garments = UiState(data = list),
                        // Las tallas de los chips se calculan solo cuando NO hay filtro de talla,
                        // para no "perder" opciones al filtrar.
                        availableSizes = if (s.selectedSize == null) {
                            list.mapNotNull { it.size }.distinct().sorted()
                        } else {
                            s.availableSizes
                        },
                    )
                }
            }.onFailure { e ->
                _state.update { it.copy(garments = UiState(error = e.message)) }
            }
        }
    }
}
