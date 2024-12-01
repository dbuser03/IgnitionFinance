package com.unimib.ignitionfinance.domain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

class NavigationViewModel : ViewModel() {
    private val _hasVisitedIntroScreen = mutableStateOf(false)
    val hasVisitedIntroScreen: State<Boolean> = _hasVisitedIntroScreen

    fun markIntroScreenAsVisited() {
        _hasVisitedIntroScreen.value = true
    }
}