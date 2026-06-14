package com.example.retonioandroid.data.remote

/**
 * Único lugar donde vive la URL base del backend.
 *
 * El emulador de Android ve el "localhost" de la PC anfitriona como 10.0.2.2,
 * por eso NO se usa 127.0.0.1 / localhost. Si pruebas en un dispositivo físico,
 * cambia esto por la IP de tu PC en la red local (p. ej. "http://192.168.1.X:3000/").
 */
object ApiConfig {
    const val BASE_URL = "http://10.0.2.2:3000/"
}
