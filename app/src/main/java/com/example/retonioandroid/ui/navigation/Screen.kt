package com.example.retonioandroid.ui.navigation

/** Rutas de navegación de la app. */
sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Plan : Screen("plan")
    data object Catalog : Screen("catalog")
    data object Wardrobe : Screen("wardrobe")
    data object Wishlist : Screen("wishlist")
    data object Profile : Screen("profile")

    data object Detail : Screen("detail/{id}") {
        const val ARG_ID = "id"
        fun routeFor(id: String) = "detail/$id"
    }
}
