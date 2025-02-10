package com.unimib.ignitionfinance.presentation.viewmodel.state

import com.unimib.ignitionfinance.domain.simulation.model.SimulationResult

data class SimulationScreenState(
    val currentPortfolioValue: Double = 0.0,
    val portfolioValueState: UiState<Double> = UiState.Idle,
    val initialInvestment: Double = 0.0,
    val simulationDuration: Int = 1,
    val parametersState: UiState<Any> = UiState.Idle,
    val simulationResult: SimulationResult? = null,
    // Aggiornato per gestire la coppia di risultati e fuckYouMoney
    val simulationState: UiState<Pair<List<SimulationResult>, Double>> = UiState.Idle
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