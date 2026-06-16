package com.example.retonioandroid.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.retonioandroid.di.Graph
import com.example.retonioandroid.ui.auth.LoginScreen
import com.example.retonioandroid.ui.auth.RegisterScreen
import com.example.retonioandroid.ui.catalog.CatalogScreen
import com.example.retonioandroid.ui.detail.GarmentDetailScreen
import com.example.retonioandroid.ui.profile.ProfileScreen
import com.example.retonioandroid.ui.subscription.PlanScreen
import com.example.retonioandroid.ui.wardrobe.WardrobeCycleScreen
import com.example.retonioandroid.ui.wardrobe.WishlistScreen

private data class BottomItem(val screen: Screen, val label: String, val icon: ImageVector)

private val bottomItems = listOf(
    BottomItem(Screen.Catalog, "Catálogo", Icons.Filled.Storefront),
    BottomItem(Screen.Wardrobe, "Mi Batch", Icons.Filled.Checkroom),
    BottomItem(Screen.Wishlist, "Wishlist", Icons.Filled.FavoriteBorder),
    BottomItem(Screen.Profile, "Perfil", Icons.Filled.Person),
)

@Composable
fun RetonioNavHost(startLoggedIn: Boolean) {
    val navController = rememberNavController()
    // Plan actual para decidir a dónde ir tras el login.
    val plan by Graph.sessionStore.planFlow.collectAsStateWithLifecycle(initialValue = null)

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = bottomItems.any { it.screen.route == currentRoute }

    fun goAfterAuth() {
        val target = if (plan == null) Screen.Plan.route else Screen.Catalog.route
        navController.navigate(target) {
            popUpTo(Screen.Login.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        // El Scaffold externo solo aporta la barra inferior; cada pantalla maneja
        // sus propios insets superiores con su TopAppBar (evita doble padding arriba).
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    bottomItems.forEach { item ->
                        val selected = backStackEntry?.destination?.hierarchy
                            ?.any { it.route == item.screen.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = if (startLoggedIn) Screen.Catalog.route else Screen.Login.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoggedIn = { goAfterAuth() },
                    onGoToRegister = { navController.navigate(Screen.Register.route) },
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegistered = { goAfterAuth() },
                    onBackToLogin = { navController.popBackStack() },
                )
            }
            composable(Screen.Plan.route) {
                PlanScreen(
                    onPlanChosen = {
                        navController.navigate(Screen.Catalog.route) {
                            popUpTo(Screen.Plan.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                )
            }
            composable(Screen.Catalog.route) {
                CatalogScreen(
                    onGarmentClick = { id -> navController.navigate(Screen.Detail.routeFor(id)) },
                )
            }
            composable(Screen.Wardrobe.route) {
                WardrobeCycleScreen(
                    onGarmentClick = { id -> navController.navigate(Screen.Detail.routeFor(id)) },
                )
            }
            composable(Screen.Wishlist.route) {
                WishlistScreen(
                    onGarmentClick = { id -> navController.navigate(Screen.Detail.routeFor(id)) },
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onChangePlan = { navController.navigate(Screen.Plan.route) },
                    onLoggedOut = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(navController.graph.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                )
            }
            composable(Screen.Detail.route) { entry ->
                val id = entry.arguments?.getString(Screen.Detail.ARG_ID).orEmpty()
                GarmentDetailScreen(
                    garmentId = id,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
