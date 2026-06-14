package com.example.retonioandroid.domain.model

/**
 * Modelos limpios de dominio: lo que la UI consume.
 * No saben nada de Gson ni de la red (eso vive en los DTOs).
 */

/** Estados de la "máquina de estados" del Ciclo de Armario. */
enum class GarmentStatus(val api: String, val label: String) {
    AVAILABLE("available", "Disponible"),
    RESERVED("reserved", "Reservada"),
    RENTED("rented", "Rentada"),
    IN_CLEANING("in_cleaning", "En limpieza"),
    RETIRED("retired", "Retirada"),
    UNKNOWN("unknown", "Desconocido");

    companion object {
        fun from(value: String?): GarmentStatus =
            entries.firstOrNull { it.api == value } ?: UNKNOWN
    }
}

/** Categorías para los filtros del catálogo. */
enum class GarmentCategory(val api: String, val label: String) {
    TOPS("tops", "Tops"),
    BOTTOMS("bottoms", "Pantalones"),
    DRESSES("dresses", "Vestidos"),
    OUTERWEAR("outerwear", "Abrigos"),
    SHOES("shoes", "Calzado"),
    ACCESSORIES("accessories", "Accesorios");

    companion object {
        fun from(value: String?): GarmentCategory? =
            entries.firstOrNull { it.api == value }
    }
}

data class Garment(
    val id: String,
    val ownerId: String?,
    val title: String,
    val description: String?,
    val brand: String?,
    val size: String?,
    val category: String?,
    val color: String?,
    val condition: String?,
    val imageUrl: String?,
    val rentalPrice: Double,
    val status: GarmentStatus,
    val createdAt: String?,
)

/** Una entrada del historial de estados de una prenda. */
data class GarmentState(
    val id: String,
    val state: GarmentStatus,
    val note: String?,
    val changedAt: String?,
)

/** Item del batch (carrito) o de la wishlist; la prenda embebida puede ser null. */
data class CartItem(
    val id: String,
    val garmentId: String,
    val startDate: String?,
    val endDate: String?,
    val garment: Garment?,
)

data class AuthUser(
    val id: String,
    val email: String,
)

/**
 * Planes de suscripción SIMULADOS en el cliente (no hay backend de pagos).
 * El `limit` define cuántas prendas caben en el batch.
 */
enum class SubscriptionPlan(
    val key: String,
    val displayName: String,
    val limit: Int,
    val priceLabel: String,
    val tagline: String,
) {
    PEQUENO("pequeno", "Pequeño", 5, "$199 / mes", "Ideal para empezar a rotar."),
    MEDIANO("mediano", "Mediano", 10, "$349 / mes", "El equilibrio para el día a día."),
    GRANDE("grande", "Grande", 15, "$499 / mes", "Para guardarropas que cambian rápido.");

    companion object {
        fun from(key: String?): SubscriptionPlan? =
            entries.firstOrNull { it.key == key }
    }
}
