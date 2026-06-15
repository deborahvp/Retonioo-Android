package com.example.retonioandroid.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retonioandroid.data.local.SessionStore
import com.example.retonioandroid.data.repository.CartRepository
import com.example.retonioandroid.data.repository.GarmentRepository
import com.example.retonioandroid.data.repository.WishlistRepository
import com.example.retonioandroid.domain.model.Garment
import com.example.retonioandroid.domain.model.GarmentState
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val garment: Garment? = null,
    val states: List<GarmentState> = emptyList(),
    val actionInProgress: Boolean = false,
    val message: String? = null, // feedback puntual (snackbar)
)

class DetailViewModel(
    private val garmentRepository: GarmentRepository,
    private val cartRepository: CartRepository,
    private val wishlistRepository: WishlistRepository,
    private val sessionStore: SessionStore,
) : ViewModel() {

    private val _state = MutableStateFlow(DetailUiState())
    val state: StateFlow<DetailUiState> = _state.asStateFlow()

    fun load(id: String) {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val garmentDeferred = async { garmentRepository.getGarment(id) }
            val statesDeferred = async { garmentRepository.getStates(id) }
            val garmentRes = garmentDeferred.await()
            val statesRes = statesDeferred.await()

            garmentRes.onSuccess { garment ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        garment = garment,
                        states = statesRes.getOrDefault(emptyList()),
                    )
                }
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun addToBatch() {
        val garment = _state.value.garment ?: return
        _state.update { it.copy(actionInProgress = true) }
        viewModelScope.launch {
            // Valida el límite del plan ANTES de agregar.
            val plan = sessionStore.planFlow.first()
            val limit = plan?.limit
            if (limit != null) {
                val currentCount = cartRepository.getCart().getOrDefault(emptyList()).size
                if (currentCount >= limit) {
                    _state.update {
                        it.copy(
                            actionInProgress = false,
                            message = "Tu plan ${plan.displayName} permite $limit prendas; " +
                                "devuelve tu batch para tomar uno nuevo.",
                        )
                    }
                    return@launch
                }
            }

            cartRepository.addToCart(garment.id)
                .onSuccess { _state.update { it.copy(actionInProgress = false, message = "Agregada a tu batch.") } }
                .onFailure { e -> _state.update { it.copy(actionInProgress = false, message = e.message) } }
        }
    }

    fun addToWishlist() {
        val garment = _state.value.garment ?: return
        _state.update { it.copy(actionInProgress = true) }
        viewModelScope.launch {
            wishlistRepository.addToWishlist(garment.id)
                .onSuccess { _state.update { it.copy(actionInProgress = false, message = "Guardada en tu wishlist.") } }
                .onFailure { e -> _state.update { it.copy(actionInProgress = false, message = e.message) } }
        }
    }

    fun consumeMessage() = _state.update { it.copy(message = null) }
}
