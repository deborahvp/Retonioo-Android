package com.example.retonioandroid.ui.wardrobe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.retonioandroid.di.Graph
import com.example.retonioandroid.domain.model.CartItem
import com.example.retonioandroid.ui.components.EmptyBox
import com.example.retonioandroid.ui.components.ErrorBox
import com.example.retonioandroid.ui.components.GarmentImage
import com.example.retonioandroid.ui.components.LoadingBox
import com.example.retonioandroid.ui.components.StatusChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardrobeCycleScreen(
    onGarmentClick: (String) -> Unit,
    viewModel: WardrobeViewModel = viewModel(factory = Graph.viewModelFactory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    val cart = state.cart
    val count = cart.data?.size ?: 0
    val limit = state.plan?.limit

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi batch") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // Encabezado: contador X / N y microcopy de economía circular
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = if (limit != null) "$count / $limit prendas" else "$count prendas",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = state.plan?.let { "Plan ${it.displayName}" }
                        ?: "Elige un plan en tu perfil para definir el tamaño de tu batch.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Box(Modifier.weight(1f)) {
                when {
                    cart.isLoading -> LoadingBox()
                    cart.error != null -> ErrorBox(cart.error, onRetry = viewModel::loadCart)
                    cart.data.isNullOrEmpty() -> EmptyBox(
                        title = "Tu batch está vacío",
                        subtitle = "Agrega prendas desde el catálogo.",
                    )
                    else -> LazyColumn(
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(cart.data, key = { it.id }) { item ->
                            BatchItemCard(
                                item = item,
                                onClick = { item.garment?.id?.let(onGarmentClick) },
                                onRemove = { viewModel.removeFromCart(item.garmentId) },
                                modifier = Modifier.animateItem(),
                            )
                        }
                    }
                }
            }

            // CTA destacado: devolver batch
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    "Al devolver, tus prendas van a limpieza y vuelven al catálogo para otra familia. Economía circular 🌱",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Button(
                    onClick = viewModel::returnBatch,
                    enabled = !state.returning && count > 0,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (state.returning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text("Devolver batch y elegir nuevo")
                    }
                }
            }
        }
    }
}

@Composable
private fun BatchItemCard(
    item: CartItem,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val garment = item.garment
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            GarmentImage(
                url = garment?.imageUrl,
                contentDescription = garment?.title,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp)),
            )
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = garment?.title ?: "Prenda",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                val subtitle = listOfNotNull(garment?.brand, garment?.size?.let { "Talla $it" })
                    .joinToString(" · ")
                if (subtitle.isNotBlank()) {
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                garment?.let { StatusChip(status = it.status) }
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Filled.Delete, contentDescription = "Quitar del batch")
            }
        }
    }
}
