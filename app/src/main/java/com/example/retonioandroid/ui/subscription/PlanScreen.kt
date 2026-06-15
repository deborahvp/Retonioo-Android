package com.example.retonioandroid.ui.subscription

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.retonioandroid.di.Graph
import com.example.retonioandroid.domain.model.SubscriptionPlan

@Composable
fun PlanScreen(
    onPlanChosen: () -> Unit,
    viewModel: PlanViewModel = viewModel(factory = Graph.viewModelFactory),
) {
    val currentPlan by viewModel.currentPlan.collectAsStateWithLifecycle()
    val saved by viewModel.saved.collectAsStateWithLifecycle()

    LaunchedEffect(saved) {
        if (saved) {
            viewModel.consumeSaved()
            onPlanChosen()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Elige tu plan", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Text(
            "Define cuántas prendas caben en tu batch. Puedes cambiarlo cuando quieras desde tu perfil.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        SubscriptionPlan.entries.forEach { plan ->
            PlanCard(
                plan = plan,
                selected = currentPlan == plan,
                onClick = { viewModel.selectPlan(plan) },
            )
        }
    }
}

@Composable
private fun PlanCard(
    plan: SubscriptionPlan,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface,
        ),
        border = if (selected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "${plan.displayName} · ${plan.limit} prendas",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(plan.tagline, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(plan.priceLabel, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
            if (selected) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = "Plan seleccionado",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
