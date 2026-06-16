package com.example.retonioandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.retonioandroid.di.Graph
import com.example.retonioandroid.ui.components.LoadingBox
import com.example.retonioandroid.ui.navigation.RetonioNavHost
import com.example.retonioandroid.ui.theme.RetonioAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RetonioAndroidTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RetonioRoot()
                }
            }
        }
    }
}

/**
 * Resuelve una sola vez si ya hay sesión guardada para decidir la pantalla inicial,
 * sin bloquear el hilo principal. Mientras tanto muestra un breve indicador.
 */
@Composable
private fun RetonioRoot() {
    var loggedIn by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        loggedIn = !Graph.sessionStore.currentAccessToken().isNullOrBlank()
    }

    when (val resolved = loggedIn) {
        null -> LoadingBox()
        else -> RetonioNavHost(startLoggedIn = resolved)
    }
}
