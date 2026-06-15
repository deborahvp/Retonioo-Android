package com.example.retonioandroid.ui.wardrobe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retonioandroid.data.local.SessionStore
import com.example.retonioandroid.data.repository.CartRepository
import com.example.retonioandroid.data.repository.WishlistRepository
import com.example.retonioandroid.domain.model.CartItem
import com.example.retonioandroid.domain.model.SubscriptionPlan
import com.example.retonioandroid.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WardrobeUiState(
    val cart: UiState<List<CartItem>> = UiState(isLoading = true),
    val wishlist: UiState<List<CartItem>> = UiState(isLoading = true),
    val plan: SubscriptionPlan? = null,
    val returning: Boolean = false,
    val message: String? = null,
)

class WardrobeViewModel(
    private val cartRepository: CartRepository,
    private val wishlistRepository: WishlistRepository,
    private val sessionStore: SessionStore,
) : ViewModel() {

    private val _state = MutableStateFlow(WardrobeUiState())
    val state: StateFlow<WardrobeUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            sessionStore.planFlow.collect { plan ->
                _state.update { it.copy(plan = plan) }
            }
        }
        loadCart()
        loadWishlist()
    }

    fun loadCart() {
        _state.update { it.copy(cart = it.cart.copy(isLoading = true, error = null)) }
        viewModelScope.launch {
            cartRepository.getCart()
                .onSuccess { list -> _state.update { it.copy(cart = UiState(data = list)) } }
                .onFailure { e -> _state.update { it.copy(cart = UiState(error = e.message)) } }
        }
    }

    fun loadWishlist() {
        _state.update { it.copy(wishlist = it.wishlist.copy(isLoading = true, error = null)) }
        viewModelScope.launch {
            wishlistRepository.getWishlist()
                .onSuccess { list -> _state.update { it.copy(wishlist = UiState(data = list)) } }
                .onFailure { e -> _state.update { it.copy(wishlist = UiState(error = e.message)) } }
        }
    }

    fun removeFromCart(garmentId: String) {
        viewModelScope.launch {
            cartRepository.removeFromCart(garmentId)
                .onSuccess { loadCart() }
                .onFailure { e -> _state.update { it.copy(message = e.message) } }
        }
    }

    fun removeFromWishlist(garmentId: String) {
        viewModelScope.launch {
            wishlistRepository.removeFromWishlist(garmentId)
                .onSuccess { loadWishlist() }
                .onFailure { e -> _state.update { it.copy(message = e.message) } }
        }
    }

    /** Mueve una prenda de la wishlist al batch, respetando el límite del plan. */
    fun moveToBatch(garmentId: String) {
        viewModelScope.launch {
            val limit = _state.value.plan?.limit
            val currentCount = _state.value.cart.data?.size ?: 0
            if (limit != null && currentCount >= limit) {
                _state.update {
                    it.copy(message = "Tu plan permite $limit prendas; devuelve tu batch para tomar uno nuevo.")
                }
                return@launch
            }
            cartRepository.addToCart(garmentId)
                .onSuccess {
                    wishlistRepository.removeFromWishlist(garmentId)
                    loadCart()
                    loadWishlist()
                    _state.update { it.copy(message = "Movida a tu batch.") }
                }
                .onFailure { e -> _state.update { it.copy(message = e.message) } }
        }
    }

    fun returnBatch() {
        _state.update { it.copy(returning = true) }
        viewModelScope.launch {
            cartRepository.returnBatch()
                .onSuccess { n ->
                    _state.update { it.copy(returning = false, message = "Devolviste $n prenda(s). ¡A limpieza!") }
                    loadCart()
                }
                .onFailure { e -> _state.update { it.copy(returning = false, message = e.message) } }
        }
    }

    fun consumeMessage() = _state.update { it.copy(message = null) }
}
