package com.example.retonioandroid.data.repository

import com.example.retonioandroid.data.remote.RetonioApi
import com.example.retonioandroid.data.remote.dtos.toDomain
import com.example.retonioandroid.domain.model.Garment
import com.example.retonioandroid.domain.model.GarmentState

/** Catálogo y detalle de prendas (endpoints públicos). */
class GarmentRepository(private val api: RetonioApi) {

    suspend fun getGarments(
        category: String? = null,
        size: String? = null,
        status: String? = null,
    ): Result<List<Garment>> = safeApiCall {
        api.getGarments(category, size, status).map { it.toDomain() }
    }

    suspend fun getGarment(id: String): Result<Garment> = safeApiCall {
        api.getGarment(id).toDomain()
    }

    suspend fun getStates(id: String): Result<List<GarmentState>> = safeApiCall {
        api.getGarmentStates(id).map { it.toDomain() }
    }
}
