package com.unimib.ignitionfinance.domain.simulation

import android.util.Log
import com.unimib.ignitionfinance.domain.simulation.model.SimulationConfig
import com.unimib.ignitionfinance.domain.simulation.model.SimulationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

object FireSimulator {
    private const val TAG = "FIRE_SIMULATOR"

    suspend fun simulatePortfolio(
        config: SimulationConfig,
        marketReturnsMatrix: Array<DoubleArray>,
        withdrawalMatrix: Array<DoubleArray>
    ): SimulationResult = withContext(Dispatchers.Default) {
        val simulationLength = 100
        val numSimulations = config.settings.numberOfSimulations.toInt()

        val totalCapital = config.capital.invested + config.capital.cash
        val percCash = if (totalCapital > 0) config.capital.cash / totalCapital else 0.0

        val simulationResults = (0 until numSimulations).map { sim ->
            async {
                val investedSim = DoubleArray(simulationLength)
                val cashSim = DoubleArray(simulationLength)

                investedSim[0] = config.capital.invested * (1 - percCash)
                cashSim[0] = config.capital.cash
                var capitalLoad = config.capital.invested * config.settings.expenses.loadPercentage.toDouble()

                for (t in 0 until simulationLength - 1) {
                    val reqWithdrawal = withdrawalMatrix[t][sim]
                    val withdrawCash = minOf(cashSim[t], reqWithdrawal)
                    val remainingReq = reqWithdrawal - withdrawCash

                    val tax = if (investedSim[t] > 0)
                        remainingReq * (1 - capitalLoad / investedSim[t]) * config.settings.expenses.taxRatePercentage.toDouble()
                    else 0.0

                    val oldFire = investedSim[t] + remainingReq + tax
                    investedSim[t] -= (remainingReq + tax)
                    capitalLoad = if (oldFire > 0) capitalLoad * (investedSim[t] / oldFire) else 0.0
                    cashSim[t] -= withdrawCash

                    investedSim[t + 1] = investedSim[t] * marketReturnsMatrix[t + 1][sim]
                    cashSim[t + 1] = cashSim[t] * (1 + config.simulationParams.cashInterestRate)

                    val totalPortfolio = investedSim[t + 1] + cashSim[t + 1]
                    val stampDuty = totalPortfolio * config.settings.expenses.stampDutyPercentage.toDouble()
                    if (totalPortfolio > 0) {
                        val fireShare = investedSim[t + 1] / totalPortfolio
                        investedSim[t + 1] -= stampDuty * fireShare
                        cashSim[t + 1] -= stampDuty * (1 - fireShare)
                    }
                }
                Pair(investedSim, cashSim)
            }
        }.awaitAll()


        val invested = Array(simulationLength) { DoubleArray(numSimulations) }
        val cash = Array(simulationLength) { DoubleArray(numSimulations) }
        simulationResults.forEachIndexed { sim, pair ->
            val (investedSim, cashSim) = pair
            for (t in 0 until simulationLength) {
                invested[t][sim] = investedSim[t]
                cash[t][sim] = cashSim[t]
            }
        }

        val targetYear = config.settings.intervals.yearsInFIRE.toInt()
        val successCount = (0 until numSimulations).count { sim ->
            (invested[targetYear][sim] + cash[targetYear][sim]) > 0
        }
        val successRate = successCount.toDouble() / numSimulations

        Log.d(TAG, "Success rate: $successRate, Capital = ${config.capital.total}")

        SimulationResult(
            successRate = successRate,

        )
    }
}
