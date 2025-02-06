package com.unimib.ignitionfinance.presentation.viewmodel.state

import com.unimib.ignitionfinance.data.remote.model.user.SimulationResult

data class SimulationScreenState(
    val currentPortfolioValue: Double = 0.0,
    val portfolioValueState: UiState<Double> = UiState.Idle,
    val initialInvestment: Double = 0.0,
    val simulationDuration: Int = 1,
    val parametersState: UiState<Any> = UiState.Idle,
    val simulationResult: SimulationResult? = null,
    val simulationState: UiState<Any> = UiState.Idle  // Cambiato da UiState<SimulationResult> a UiState<Any>
)

/*
data class SimulationScreenState(
    val currentPortfolioValue: Double = 0.0,
    val portfolioValueState: UiState<Double> = UiState.Idle,
    val initialInvestment: Double = 0.0,
    val simulationDuration: Int = 1,
    val parametersState: UiState<Any> = UiState.Idle,
    val simulationResult: SimulationResult? = null,
    val simulationState: UiState<SimulationResult> = UiState.Idle
)
 */