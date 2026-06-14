package com.example.retonioandroid.data.remote

import com.example.retonioandroid.data.remote.dtos.AddToCartRequest
import com.example.retonioandroid.data.remote.dtos.AddToWishlistRequest
import com.example.retonioandroid.data.remote.dtos.CartItemDto
import com.example.retonioandroid.data.remote.dtos.GarmentDto
import com.example.retonioandroid.data.remote.dtos.GarmentStateDto
import com.example.retonioandroid.data.remote.dtos.LoginRequest
import com.example.retonioandroid.data.remote.dtos.LoginResponse
import com.example.retonioandroid.data.remote.dtos.RegisterRequest
import com.example.retonioandroid.data.remote.dtos.RegisterResponse
import com.example.retonioandroid.data.remote.dtos.ReturnBatchResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/** Interfaz Retrofit que describe el contrato del backend Retoño. */
interface RetonioApi {

    // ---- Auth (público) ----
    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): RegisterResponse

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    // ---- Catálogo / Detalle (público) ----
    @GET("garments")
    suspend fun getGarments(
        @Query("category") category: String? = null,
        @Query("size") size: String? = null,
        @Query("status") status: String? = null,
    ): List<GarmentDto>

    @GET("garments/{id}")
    suspend fun getGarment(@Path("id") id: String): GarmentDto

    @GET("garments/{id}/states")
    suspend fun getGarmentStates(@Path("id") id: String): List<GarmentStateDto>

    // ---- Carrito / Batch (requiere Bearer) ----
    @GET("cart")
    suspend fun getCart(): List<CartItemDto>

    @POST("cart")
    suspend fun addToCart(@Body body: AddToCartRequest): CartItemDto

    @DELETE("cart/{garment_id}")
    suspend fun removeFromCart(@Path("garment_id") garmentId: String): Response<Unit>

    @POST("cart/return")
    suspend fun returnBatch(): ReturnBatchResponse

    // ---- Wishlist (requiere Bearer) ----
    @GET("wishlist")
    suspend fun getWishlist(): List<CartItemDto>

    @POST("wishlist")
    suspend fun addToWishlist(@Body body: AddToWishlistRequest): CartItemDto

    @DELETE("wishlist/{garment_id}")
    suspend fun removeFromWishlist(@Path("garment_id") garmentId: String): Response<Unit>
}
