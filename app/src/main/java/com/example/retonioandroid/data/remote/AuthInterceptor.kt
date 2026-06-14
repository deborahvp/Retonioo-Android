package com.example.retonioandroid.data.remote

import com.example.retonioandroid.data.local.SessionStore
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Adjunta el header `Authorization: Bearer <token>` cuando hay sesión.
 * Lee el token de una caché en memoria del SessionStore: lectura síncrona,
 * sin runBlocking ni I/O en el hilo de red. Así nunca se bloquea nada.
 */
class AuthInterceptor(private val sessionStore: SessionStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sessionStore.cachedAccessToken
        val request = chain.request().newBuilder().apply {
            if (!token.isNullOrBlank()) {
                addHeader("Authorization", "Bearer $token")
            }
        }.build()
        return chain.proceed(request)
    }
}
