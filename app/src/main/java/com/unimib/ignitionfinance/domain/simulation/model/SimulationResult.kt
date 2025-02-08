package com.unimib.ignitionfinance.domain.simulation.model

data class SimulationResult(
    val successRate: Double,
    val investedPortfolio: Array<DoubleArray>,
    val cashPortfolio: Array<DoubleArray>,
    val totalSimulations: Int,
    val simulationLength: Int
)