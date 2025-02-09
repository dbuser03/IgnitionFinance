package com.unimib.ignitionfinance.domain.simulation

import android.util.Log
import com.unimib.ignitionfinance.domain.simulation.model.SimulationConfig
import com.unimib.ignitionfinance.domain.simulation.model.SimulationResult

object FireSimulator {
    private const val TAG = "FIRE_SIMULATOR"

    fun simulatePortfolio(
        config: SimulationConfig,
        marketReturnsMatrix: Array<DoubleArray>,
        withdrawalMatrix: Array<DoubleArray>
    ): SimulationResult {
        val simulationLength = 100
        val numSimulations = config.settings.numberOfSimulations.toInt()

        val invested = Array(simulationLength) { DoubleArray(numSimulations) }
        val cash = Array(simulationLength) { DoubleArray(numSimulations) }

        val totalCapital = config.capital.invested + config.capital.cash
        val percCash = if (totalCapital > 0) config.capital.cash / totalCapital else 0.0
        for (sim in 0 until numSimulations) {
            invested[0][sim] = config.capital.invested * (1 - percCash)
            cash[0][sim] = config.capital.cash
        }

        val capitalLoad = DoubleArray(numSimulations) {
            config.capital.invested * config.settings.expenses.loadPercentage.toDouble()
        }

        for (t in 0 until simulationLength - 1) {
            for (sim in 0 until numSimulations) {
                val reqWithdrawal = withdrawalMatrix[t][sim]
                val withdrawCash = minOf(cash[t][sim], reqWithdrawal)
                val remainingReq = reqWithdrawal - withdrawCash

                val tax = if (invested[t][sim] > 0)
                    remainingReq * (1 - capitalLoad[sim] / invested[t][sim]) * config.settings.expenses.taxRatePercentage.toDouble()
                else 0.0

                val oldFire = invested[t][sim] + remainingReq + tax
                invested[t][sim] -= (remainingReq + tax)

                capitalLoad[sim] = if (oldFire > 0) capitalLoad[sim] * (invested[t][sim] / oldFire) else 0.0
                cash[t][sim] -= withdrawCash

                invested[t + 1][sim] = invested[t][sim] * marketReturnsMatrix[t + 1][sim]
                cash[t + 1][sim] = cash[t][sim] * (1 + config.simulationParams.cashInterestRate)

                val totalPortfolio = invested[t + 1][sim] + cash[t + 1][sim]
                val stampDuty = totalPortfolio * config.settings.expenses.stampDutyPercentage.toDouble()
                if (totalPortfolio > 0) {
                    val fireShare = invested[t + 1][sim] / totalPortfolio
                    invested[t + 1][sim] -= stampDuty * fireShare
                    cash[t + 1][sim] -= stampDuty * (1 - fireShare)
                }
            }
        }

        val targetYear = config.settings.intervals.yearsInFIRE.toInt()
        val successCount = (0 until numSimulations).count { sim ->
            (invested[targetYear][sim] + cash[targetYear][sim]) > 0
        }
        val successRate = (successCount.toDouble() / numSimulations)

        Log.d(TAG, "Success rate: $successRate")

        return SimulationResult(
            successRate = successRate,
            investedPortfolio = invested,
            cashPortfolio = cash,
            totalSimulations = numSimulations,
            simulationLength = simulationLength
        )
    }
}
