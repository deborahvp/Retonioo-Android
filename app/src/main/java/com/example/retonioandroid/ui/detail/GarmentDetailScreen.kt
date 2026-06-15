package com.example.retonioandroid.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.retonioandroid.di.Graph
import com.example.retonioandroid.domain.model.Garment
import com.example.retonioandroid.domain.model.GarmentState
import com.example.retonioandroid.domain.model.GarmentStatus
import com.example.retonioandroid.ui.components.ErrorBox
import com.example.retonioandroid.ui.components.GarmentImage
import com.example.retonioandroid.ui.components.LoadingBox
import com.example.retonioandroid.ui.components.StatusChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GarmentDetailScreen(
    garmentId: String,
    onBack: () -> Unit,
    viewModel: DetailViewModel = viewModel(factory = Graph.viewModelFactory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(garmentId) { viewModel.load(garmentId) }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> LoadingBox()
                state.error != null -> ErrorBox(state.error!!, onRetry = { viewModel.load(garmentId) })
                state.garment != null -> DetailContent(
                    garment = state.garment!!,
                    states = state.states,
                    actionInProgress = state.actionInProgress,
                    onAddToBatch = viewModel::addToBatch,
                    onAddToWishlist = viewModel::addToWishlist,
                )
            }
        }
    }
}

@Composable
private fun DetailContent(
    garment: Garment,
    states: List<GarmentState>,
    actionInProgress: Boolean,
    onAddToBatch: () -> Unit,
    onAddToWishlist: () -> Unit,
) {
    val available = garment.status == GarmentStatus.AVAILABLE
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        GarmentImage(
            url = garment.imageUrl,
            contentDescription = garment.title,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(20.dp)),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = garment.title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
            )
            StatusChip(status = garment.status)
        }

        garment.brand?.takeIf { it.isNotBlank() }?.let {
            Text(it, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Ficha de atributos
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            AttributeRow("Talla", garment.size)
            AttributeRow("Color", garment.color)
            AttributeRow("Condición", garment.condition)
            AttributeRow("Precio referencia", "$${"%.2f".format(garment.rentalPrice)} / semana")
        }

        garment.description?.takeIf { it.isNotBlank() }?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
        }

        // Timeline de la máquina de estados
        Text(
            "Historial de la prenda",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            "Ciclo: disponible → reservada → rentada → en limpieza → disponible",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (states.isEmpty()) {
            Text(
                "Sin historial de estados.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            StateTimeline(states = states, currentStatus = garment.status)
        }

        // Acciones
        Spacer(Modifier.height(4.dp))
        Button(
            onClick = onAddToBatch,
            enabled = available && !actionInProgress,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (actionInProgress) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                )
            } else {
                Text("Agregar a mi batch")
            }
        }
        OutlinedButton(
            onClick = onAddToWishlist,
            enabled = available && !actionInProgress,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Guardar en wishlist")
        }
        if (!available) {
            Text(
                "Solo puedes agregar prendas disponibles.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun AttributeRow(label: String, value: String?) {
    if (value.isNullOrBlank()) return
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}

/** Timeline vertical: punto + línea por cada estado, resaltando el estado actual. */
@Composable
private fun StateTimeline(states: List<GarmentState>, currentStatus: GarmentStatus) {
    Column(Modifier.fillMaxWidth()) {
        states.forEachIndexed { index, entry ->
            val isLast = index == states.lastIndex
            val isCurrent = isLast && entry.state == currentStatus
            Row(Modifier.fillMaxWidth()) {
                // Columna del indicador (punto + línea conectora)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(if (isCurrent) 16.dp else 12.dp)
                            .clip(CircleShape)
                            .background(
                                if (isCurrent) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline,
                            ),
                    )
                    if (!isLast) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(36.dp)
                                .background(MaterialTheme.colorScheme.outline),
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.padding(bottom = if (isLast) 0.dp else 12.dp)) {
                    Text(
                        text = entry.state.label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isCurrent) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface,
                    )
                    entry.changedAt?.let { ts ->
                        Text(
                            text = formatTimestamp(ts),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    entry.note?.takeIf { it.isNotBlank() }?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

/** Toma la fecha "YYYY-MM-DD" del timestamp ISO; si no se puede, devuelve el original. */
private fun formatTimestamp(iso: String): String =
    if (iso.length >= 10) iso.substring(0, 10) else iso
