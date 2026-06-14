package com.example.retonioandroid.data.remote.dtos

/**
 * DTOs: reflejan EXACTAMENTE lo que viaja por la red.
 *
 * Gson está configurado con FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES, así que
 * un campo Kotlin `imageUrl` mapea solo al JSON `image_url`. No hace falta @SerializedName.
 */

// ---- Auth ----
data class RegisterRequest(
    val email: String,
    val password: String,
    val displayName: String, // -> display_name
)

data class LoginRequest(
    val email: String,
    val password: String,
)

data class AuthUserDto(
    val id: String,
    val email: String,
)

data class RegisterResponse(
    val user: AuthUserDto,
)

data class LoginResponse(
    val accessToken: String,  // -> access_token
    val refreshToken: String, // -> refresh_token
    val user: AuthUserDto,
)

// ---- Garments ----
data class GarmentDto(
    val id: String,
    val ownerId: String?,     // -> owner_id
    val title: String,
    val description: String?,
    val brand: String?,
    val size: String?,
    val category: String?,
    val color: String?,
    val condition: String?,
    val imageUrl: String?,    // -> image_url
    val rentalPrice: Double = 0.0, // -> rental_price
    val status: String,
    val createdAt: String?,   // -> created_at
)

/**
 * Historial de la máquina de estados.
 * OJO: la tabla `garment_states` usa la columna `state` (no `status`) y `changed_at`.
 */
data class GarmentStateDto(
    val id: String,
    val garmentId: String,    // -> garment_id
    val state: String,
    val note: String?,
    val changedAt: String?,   // -> changed_at
)

// ---- Carrito / Wishlist ----
// El backend embebe la prenda en la propiedad `garments`.
data class CartItemDto(
    val id: String,
    val userId: String?,      // -> user_id
    val garmentId: String,    // -> garment_id
    val startDate: String?,   // -> start_date
    val endDate: String?,     // -> end_date
    val addedAt: String?,     // -> added_at
    val garments: GarmentDto?,
)

data class AddToCartRequest(
    val garmentId: String,         // -> garment_id
    val startDate: String? = null, // -> start_date
    val endDate: String? = null,   // -> end_date
)

data class AddToWishlistRequest(
    val garmentId: String, // -> garment_id
)

data class ReturnBatchResponse(
    val devueltas: Int,
)
