package com.example.retonioandroid.data.repository

import com.example.retonioandroid.data.local.SessionStore
import com.example.retonioandroid.data.remote.RetonioApi
import com.example.retonioandroid.data.remote.dtos.LoginRequest
import com.example.retonioandroid.data.remote.dtos.RegisterRequest
import com.example.retonioandroid.domain.model.AuthUser
import com.example.retonioandroid.data.remote.dtos.toDomain

/** Auth: login / registro. Al iniciar sesión persiste el token en el SessionStore. */
class AuthRepository(
    private val api: RetonioApi,
    private val sessionStore: SessionStore,
) {
    suspend fun login(email: String, password: String): Result<AuthUser> = safeApiCall {
        val res = api.login(LoginRequest(email.trim(), password))
        sessionStore.saveSession(
            accessToken = res.accessToken,
            refreshToken = res.refreshToken,
            userId = res.user.id,
            email = res.user.email,
        )
        res.user.toDomain()
    }

    /** Registra y, si todo sale bien, inicia sesión automáticamente. */
    suspend fun register(
        email: String,
        password: String,
        displayName: String,
    ): Result<AuthUser> = safeApiCall {
        api.register(RegisterRequest(email.trim(), password, displayName.trim()))
        val res = api.login(LoginRequest(email.trim(), password))
        sessionStore.saveSession(
            accessToken = res.accessToken,
            refreshToken = res.refreshToken,
            userId = res.user.id,
            email = res.user.email,
        )
        res.user.toDomain()
    }

    suspend fun logout() = sessionStore.clear()
}
