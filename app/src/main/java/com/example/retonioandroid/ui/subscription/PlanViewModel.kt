package com.example.retonioandroid.ui.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retonioandroid.data.local.SessionStore
import com.example.retonioandroid.domain.model.SubscriptionPlan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlanViewModel(
    private val sessionStore: SessionStore,
) : ViewModel() {

    /** Plan actualmente guardado (para resaltar la selección). */
    val currentPlan: StateFlow<SubscriptionPlan?> =
        sessionStore.planFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved.asStateFlow()

    fun selectPlan(plan: SubscriptionPlan) {
        viewModelScope.launch {
            sessionStore.savePlan(plan)
            _saved.value = true
        }
    }

    fun consumeSaved() { _saved.value = false }
}
