package com.example.retonioandroid.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.retonioandroid.domain.model.SubscriptionPlan
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Un único DataStore para toda la app, asociado al Context de aplicación.
private val Context.dataStore by preferencesDataStore(name = "retonio_session")

/**
 * Persiste la sesión: token de acceso, datos del usuario y plan elegido.
 * Expone Flows para que la UI reaccione y funciones suspend para escribir.
 */
class SessionStore(private val context: Context) {

    /**
     * Copia del access_token en memoria para que el interceptor de OkHttp la lea de
     * forma síncrona, SIN runBlocking ni I/O por request. Se mantiene fresca con
     * [keepTokenCacheFresh] y se actualiza al instante en saveSession/clear.
     */
    @Volatile
    var cachedAccessToken: String? = null
        private set

    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val PLAN = stringPreferencesKey("plan")
    }

    val accessTokenFlow: Flow<String?> =
        context.dataStore.data.map { it[Keys.ACCESS_TOKEN] }

    val userEmailFlow: Flow<String?> =
        context.dataStore.data.map { it[Keys.USER_EMAIL] }

    val planFlow: Flow<SubscriptionPlan?> =
        context.dataStore.data.map { SubscriptionPlan.from(it[Keys.PLAN]) }

    /** Lectura puntual del token (para decidir la pantalla inicial sin observar). */
    suspend fun currentAccessToken(): String? = accessTokenFlow.first()

    /** Mantiene [cachedAccessToken] sincronizado con DataStore. Colectar en un scope de app. */
    suspend fun keepTokenCacheFresh() {
        accessTokenFlow.collect { cachedAccessToken = it }
    }

    suspend fun saveSession(
        accessToken: String,
        refreshToken: String,
        userId: String,
        email: String,
    ) {
        context.dataStore.edit {
            it[Keys.ACCESS_TOKEN] = accessToken
            it[Keys.REFRESH_TOKEN] = refreshToken
            it[Keys.USER_ID] = userId
            it[Keys.USER_EMAIL] = email
        }
        // Refresca la caché de inmediato para el siguiente request autenticado.
        cachedAccessToken = accessToken
    }

    suspend fun savePlan(plan: SubscriptionPlan) {
        context.dataStore.edit { it[Keys.PLAN] = plan.key }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
        cachedAccessToken = null
    }
}
