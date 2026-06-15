package com.example.retonioandroid.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.retonioandroid.data.local.SessionStore
import com.example.retonioandroid.data.remote.RetrofitClient
import com.example.retonioandroid.data.repository.AuthRepository
import com.example.retonioandroid.data.repository.CartRepository
import com.example.retonioandroid.data.repository.GarmentRepository
import com.example.retonioandroid.data.repository.WishlistRepository
import com.example.retonioandroid.ui.auth.AuthViewModel
import com.example.retonioandroid.ui.catalog.CatalogViewModel
import com.example.retonioandroid.ui.detail.DetailViewModel
import com.example.retonioandroid.ui.profile.ProfileViewModel
import com.example.retonioandroid.ui.subscription.PlanViewModel
import com.example.retonioandroid.ui.wardrobe.WardrobeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Contenedor de dependencias manual (sin Hilt) inicializado una vez por la Application.
 * Construye red, persistencia y repositorios, y expone un único [ViewModelProvider.Factory].
 */
object Graph {

    lateinit var sessionStore: SessionStore
        private set

    lateinit var authRepository: AuthRepository
        private set
    lateinit var garmentRepository: GarmentRepository
        private set
    lateinit var cartRepository: CartRepository
        private set
    lateinit var wishlistRepository: WishlistRepository
        private set

    // Scope de aplicación (no se cancela) para tareas de fondo como sincronizar la caché del token.
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun init(context: Context) {
        sessionStore = SessionStore(context.applicationContext)
        val api = RetrofitClient.create(sessionStore)
        authRepository = AuthRepository(api, sessionStore)
        garmentRepository = GarmentRepository(api)
        cartRepository = CartRepository(api)
        wishlistRepository = WishlistRepository(api)

        // Mantiene cachedAccessToken sincronizado con DataStore en segundo plano.
        appScope.launch { sessionStore.keepTokenCacheFresh() }
    }

    /** Una sola factory que sabe construir todos los ViewModels de la app. */
    val viewModelFactory: ViewModelProvider.Factory
        get() = viewModelFactory {
            initializer { AuthViewModel(authRepository) }
            initializer { CatalogViewModel(garmentRepository) }
            initializer { DetailViewModel(garmentRepository, cartRepository, wishlistRepository, sessionStore) }
            initializer { WardrobeViewModel(cartRepository, wishlistRepository, sessionStore) }
            initializer { PlanViewModel(sessionStore) }
            initializer { ProfileViewModel(authRepository, sessionStore) }
        }
}
