package com.example.retonioandroid.data.repository

import com.example.retonioandroid.data.remote.RetonioApi
import com.example.retonioandroid.data.remote.dtos.AddToWishlistRequest
import com.example.retonioandroid.data.remote.dtos.toDomain
import com.example.retonioandroid.domain.model.CartItem

/** Lista de deseos del usuario. */
class WishlistRepository(private val api: RetonioApi) {

    suspend fun getWishlist(): Result<List<CartItem>> = safeApiCall {
        api.getWishlist().map { it.toDomain() }
    }

    suspend fun addToWishlist(garmentId: String): Result<Unit> = safeApiCall {
        api.addToWishlist(AddToWishlistRequest(garmentId))
        Unit
    }

    suspend fun removeFromWishlist(garmentId: String): Result<Unit> = safeApiCall {
        val res = api.removeFromWishlist(garmentId)
        if (!res.isSuccessful) throw retrofit2.HttpException(res)
        Unit
    }
}
