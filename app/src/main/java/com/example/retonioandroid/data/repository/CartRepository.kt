package com.example.retonioandroid.data.repository

import com.example.retonioandroid.data.remote.RetonioApi
import com.example.retonioandroid.data.remote.dtos.AddToCartRequest
import com.example.retonioandroid.data.remote.dtos.toDomain
import com.example.retonioandroid.domain.model.CartItem

/** El "Ciclo de Armario": el carrito es el batch actual del usuario. */
class CartRepository(private val api: RetonioApi) {

    suspend fun getCart(): Result<List<CartItem>> = safeApiCall {
        api.getCart().map { it.toDomain() }
    }

    suspend fun addToCart(garmentId: String): Result<Unit> = safeApiCall {
        api.addToCart(AddToCartRequest(garmentId))
        Unit
    }

    suspend fun removeFromCart(garmentId: String): Result<Unit> = safeApiCall {
        val res = api.removeFromCart(garmentId)
        if (!res.isSuccessful) throw retrofit2.HttpException(res)
        Unit
    }

    /** Devuelve el batch: las prendas pasan a limpieza y se vacía el carrito. */
    suspend fun returnBatch(): Result<Int> = safeApiCall {
        api.returnBatch().devueltas
    }
}
