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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
fun WishlistScreen(
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wishlist") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            val wishlist = state.wishlist
            when {
                wishlist.isLoading -> LoadingBox()
                wishlist.error != null -> ErrorBox(wishlist.error, onRetry = viewModel::loadWishlist)
                wishlist.data.isNullOrEmpty() -> EmptyBox(
                    title = "Tu wishlist está vacía",
                    subtitle = "Guarda prendas que te gusten desde el detalle.",
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(wishlist.data, key = { it.id }) { item ->
                        WishlistItemCard(
                            item = item,
                            onClick = { item.garment?.id?.let(onGarmentClick) },
                            onMoveToBatch = { viewModel.moveToBatch(item.garmentId) },
                            onRemove = { viewModel.removeFromWishlist(item.garmentId) },
                            modifier = Modifier.animateItem(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WishlistItemCard(
    item: CartItem,
    onClick: () -> Unit,
    onMoveToBatch: () -> Unit,
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
                garment?.let { StatusChip(status = it.status) }
            }
            IconButton(onClick = onMoveToBatch) {
                Icon(Icons.Filled.Add, contentDescription = "Mover a mi batch")
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Filled.Delete, contentDescription = "Quitar de wishlist")
            }
        }
    }
}
