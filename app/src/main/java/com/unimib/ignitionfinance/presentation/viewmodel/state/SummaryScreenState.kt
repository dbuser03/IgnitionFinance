package com.unimib.ignitionfinance.presentation.viewmodel.state

data class SummaryScreenState(
    val invested: Double = 0.0,
    val investedState: UiState<Double> = UiState.Idle,
    val isNetWorthHidden: Boolean = false
)