package com.unimib.ignitionfinance.data.remote.model.user

data class SimulationResult(
    val finalBalance: Double,          // The final balance after the simulation
    val investmentGrowth: Double,      // The total growth of the investment
)

