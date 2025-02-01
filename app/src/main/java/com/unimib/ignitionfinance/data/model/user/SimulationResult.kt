package com.unimib.ignitionfinance.data.model.user

data class SimulationResult(
    val finalBalance: Double,          // The final balance after the simulation
    val investmentGrowth: Double,      // The total growth of the investment
    val yearlyBreakdown: List<YearlyResult> // Yearly breakdown of the simulation (optional)
)

// Helper data class for yearly breakdown (optional)
data class YearlyResult(
    val year: Int,
    val balance: Double,
    val growth: Double
)
