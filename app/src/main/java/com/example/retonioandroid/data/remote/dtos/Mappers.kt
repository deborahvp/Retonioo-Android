package com.example.retonioandroid.data.remote.dtos

import com.example.retonioandroid.domain.model.AuthUser
import com.example.retonioandroid.domain.model.CartItem
import com.example.retonioandroid.domain.model.Garment
import com.example.retonioandroid.domain.model.GarmentState
import com.example.retonioandroid.domain.model.GarmentStatus

/** Conversión DTO -> modelo de dominio. Una sola dirección: red hacia adentro. */

fun GarmentDto.toDomain(): Garment = Garment(
    id = id,
    ownerId = ownerId,
    title = title,
    description = description,
    brand = brand,
    size = size,
    category = category,
    color = color,
    condition = condition,
    imageUrl = imageUrl,
    rentalPrice = rentalPrice,
    status = GarmentStatus.from(status),
    createdAt = createdAt,
)

fun GarmentStateDto.toDomain(): GarmentState = GarmentState(
    id = id,
    state = GarmentStatus.from(state),
    note = note,
    changedAt = changedAt,
)

fun CartItemDto.toDomain(): CartItem = CartItem(
    id = id,
    garmentId = garmentId,
    startDate = startDate,
    endDate = endDate,
    garment = garments?.toDomain(),
)

fun AuthUserDto.toDomain(): AuthUser = AuthUser(id = id, email = email)
