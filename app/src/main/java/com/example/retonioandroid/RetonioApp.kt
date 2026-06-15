package com.example.retonioandroid

import android.app.Application
import com.example.retonioandroid.di.Graph

/** Application: inicializa el contenedor de dependencias una sola vez. */
class RetonioApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.init(this)
    }
}
