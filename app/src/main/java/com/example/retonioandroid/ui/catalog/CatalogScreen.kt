package com.example.retonioandroid.ui.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.retonioandroid.di.Graph
import com.example.retonioandroid.domain.model.GarmentCategory
import com.example.retonioandroid.ui.components.EmptyBox
import com.example.retonioandroid.ui.components.ErrorBox
import com.example.retonioandroid.ui.components.GarmentCard
import com.example.retonioandroid.ui.components.LoadingBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    onGarmentClick: (String) -> Unit,
    viewModel: CatalogViewModel = viewModel(factory = Graph.viewModelFactory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo") },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Recargar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            FilterBar(
                selectedCategory = state.selectedCategory,
                onCategorySelected = viewModel::selectCategory,
                sizes = state.availableSizes,
                selectedSize = state.selectedSize,
                onSizeSelected = viewModel::selectSize,
            )

            val garments = state.garments
            when {
                garments.isLoading -> LoadingBox()
                garments.error != null -> ErrorBox(garments.error, onRetry = viewModel::refresh)
                garments.data.isNullOrEmpty() -> EmptyBox(
                    title = "No hay prendas",
                    subtitle = "Prueba quitando algún filtro.",
                )
                else -> LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(garments.data, key = { it.id }) { garment ->
                        GarmentCard(
                            garment = garment,
                            onClick = { onGarmentClick(garment.id) },
                            modifier = Modifier.animateItem(),
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBar(
    selectedCategory: GarmentCategory?,
    onCategorySelected: (GarmentCategory?) -> Unit,
    sizes: List<String>,
    selectedSize: String?,
    onSizeSelected: (String?) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { onCategorySelected(null) },
                    label = { Text("Todas") },
                )
            }
            items(GarmentCategory.entries) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = {
                        onCategorySelected(if (selectedCategory == category) null else category)
                    },
                    label = { Text(category.label) },
                )
            }
        }

        if (sizes.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
            ) {
                item {
                    FilterChip(
                        selected = selectedSize == null,
                        onClick = { onSizeSelected(null) },
                        label = { Text("Toda talla") },
                    )
                }
                items(sizes) { size ->
                    FilterChip(
                        selected = selectedSize == size,
                        onClick = { onSizeSelected(if (selectedSize == size) null else size) },
                        label = { Text(size) },
                    )
                }
            }
        }
    }
}
