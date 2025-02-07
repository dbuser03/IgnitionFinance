package com.unimib.ignitionfinance.domain.simulation.portfolio

import com.unimib.ignitionfinance.domain.simulation.models.FireSimulationConfig

class WithdrawalStrategy(private val config: FireSimulationConfig) {
    fun calculateWithdrawals(
        inflationScenarios: Array<DoubleArray>,
        numSimulations: Int
    ): Array<DoubleArray> {
        val withdrawals = Array(100) { DoubleArray(numSimulations) }
        withdrawals[0] = DoubleArray(numSimulations) { config.prelievo }

        val coefficients = DoubleArray(numSimulations) { 1.0 }

        // Adjusting withdrawals for pre-pension years with inflation
        for (t in 1 until config.anniRenditaSenzaPensione + 1) {
            for (j in 0 until numSimulations) {
                withdrawals[t][j] = withdrawals[t-1][j] * (1 + inflationScenarios[t][j])
                coefficients[j] *= (1 + inflationScenarios[t][j])
            }
        }

        // Pension year withdrawal
        withdrawals[config.anniRenditaSenzaPensione + 1] = DoubleArray(numSimulations) { j ->
            config.prelevoPensione * coefficients[j] * (1 + inflationScenarios[config.anniRenditaSenzaPensione + 1][j])
        }

        // Subsequent years adjustments
        for (t in config.anniRenditaSenzaPensione + 2 until 100) {
            for (j in 0 until numSimulations) {
                withdrawals[t][j] = withdrawals[t-1][j] * (1 + inflationScenarios[t][j])
            }
        }

        return withdrawals
    }
}