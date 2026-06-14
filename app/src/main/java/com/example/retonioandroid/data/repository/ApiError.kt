package com.example.retonioandroid.data.repository

import com.google.gson.JsonParseException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

/**
 * Ejecuta una llamada al API y la envuelve en Result, traduciendo las excepciones
 * comunes a mensajes en español aptos para mostrar al usuario.
 */
suspend fun <T> safeApiCall(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (e: HttpException) {
    Result.failure(Exception(e.toUserMessage()))
} catch (e: IOException) {
    Result.failure(Exception("No se pudo conectar con el servidor. Revisa que el backend esté corriendo."))
} catch (e: JsonParseException) {
    Result.failure(Exception("La respuesta del servidor no tiene el formato esperado."))
} catch (e: Exception) {
    Result.failure(Exception(e.message ?: "Ocurrió un error inesperado."))
}

/** Intenta leer el campo `error` del body que devuelve el backend Express. */
private fun HttpException.toUserMessage(): String {
    val raw = try {
        response()?.errorBody()?.string()
    } catch (_: Exception) {
        null
    }
    if (!raw.isNullOrBlank()) {
        try {
            val parsed = JSONObject(raw).optString("error")
            if (parsed.isNotBlank()) return parsed
        } catch (_: Exception) {
            // body no-JSON: caemos al mensaje genérico de abajo
        }
    }
    return when (code()) {
        401 -> "Credenciales inválidas o sesión expirada."
        404 -> "No se encontró el recurso solicitado."
        else -> "Error del servidor (${code()})."
    }
}
