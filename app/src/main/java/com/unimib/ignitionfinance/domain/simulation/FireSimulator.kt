package com.unimib.ignitionfinance.domain.simulation

import android.util.Log
import com.unimib.ignitionfinance.domain.simulation.model.SimulationConfig
import com.unimib.ignitionfinance.domain.simulation.model.SimulationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min

object FireSimulator {
    private const val TAG = "FIRE_SIMULATOR"

    suspend fun simulatePortfolio(
        config: SimulationConfig,
        marketReturnsMatrix: Array<DoubleArray>,
        withdrawalMatrix: Array<DoubleArray>
    ): SimulationResult = withContext(Dispatchers.Default) {

        val simulationYears = 100
        val numSimulations = config.settings.numberOfSimulations.toInt()
        val initialCapital = config.capital.invested.toDouble()
        val loadPercentage = config.settings.expenses.loadPercentage.toDouble()
        val bufferYears = config.settings.intervals.yearsOfBuffer.toDouble()
        val taxRate = config.settings.expenses.taxRatePercentage.toDouble()
        val stampDuty = config.settings.expenses.stampDutyPercentage.toDouble()
        val retirementYear = config.settings.intervals.yearsInPaidRetirement.toInt()

        val simulationResults = (0 until numSimulations).map { simIndex ->
            async {
                val isFirstSimulation = simIndex == 0

                var portfolio = initialCapital
                var buffer = 0.0
                var taxableCapital = initialCapital * loadPercentage
                var portfolioAtRetirement = portfolio
                var bufferAtRetirement = buffer

                for (t in 0 until simulationYears - 1) {
                    val currentMarketReturn = marketReturnsMatrix[t][simIndex]
                    val currentWithdrawal = withdrawalMatrix[t][simIndex]

                    val condition = if (t > 0) {
                        marketReturnsMatrix[t][simIndex] > marketReturnsMatrix[t - 1][simIndex]
                    } else {
                        true
                    }
                    val withdrawalLimit = if (condition) {
                        currentWithdrawal * bufferYears
                    } else {
                        currentWithdrawal
                    }
                    val availableFunds = portfolio + buffer
                    val newBuffer = min(availableFunds, withdrawalLimit)
                    val withdrawalBuffer = max(0.0, newBuffer - buffer)

                    val capitalGain = if (portfolio > 0) {
                        withdrawalBuffer * (1.0 - (taxableCapital / portfolio))
                    } else {
                        0.0
                    }
                    val tax = capitalGain * taxRate

                    if (isFirstSimulation) {
                        Log.d(
                            TAG, """
                            Simulazione $simIndex, Anno ${t + 1}:
                            - Valore Portafoglio: €${String.format("%,.2f", portfolio)}
                            - Prelievo: €${String.format("%,.2f", withdrawalBuffer)}
                            - Rendimento Mercato: ${String.format("%.2f%%", (currentMarketReturn - 1.0) * 100)}
                            - Capital Gain: €${String.format("%,.2f", capitalGain)}
                            - Tasse Capital Gain: €${String.format("%,.2f", tax)}
                            - Saldo Buffer: €${String.format("%,.2f", buffer)}
                            """.trimIndent()
                        )
                    }

                    if (tax != 0.0 && portfolio > 0.0) {
                        taxableCapital *= (1.0 - withdrawalBuffer / portfolio)
                    }
                    portfolio -= withdrawalBuffer
                    buffer = buffer + withdrawalBuffer - tax - min(portfolio + buffer, currentWithdrawal)

                    if (portfolio < currentWithdrawal * 1e-6) {
                        portfolio = 0.0
                        if (isFirstSimulation) {
                            Log.w(TAG, "Simulazione $simIndex, Anno ${t + 1}: Portafoglio esaurito")
                        }
                    }

                    if (t + 1 == retirementYear) {
                        portfolioAtRetirement = portfolio
                        bufferAtRetirement = buffer
                    }

                    val nextMarketReturn = marketReturnsMatrix[t + 1][simIndex]
                    portfolio *= nextMarketReturn
                    val stampDutyAmount = (portfolio + buffer) * stampDuty
                    buffer -= stampDutyAmount
                }

                val success = (portfolioAtRetirement + bufferAtRetirement) > 0
                if (isFirstSimulation) {
                    if (success) {
                        Log.i(
                            TAG, """
                            Simulazione $simIndex SUCCESSO:
                            - Valore Finale Portafoglio: €${String.format("%,.2f", portfolioAtRetirement)}
                            - Valore Finale Buffer: €${String.format("%,.2f", bufferAtRetirement)}
                            """.trimIndent()
                        )
                    } else {
                        Log.w(TAG, "Simulazione $simIndex FALLITA: Portafoglio esaurito")
                    }
                }
                success
            }
        }.awaitAll()

        val successCount = simulationResults.count { it }
        val successRate = successCount.toDouble() / numSimulations.toDouble()
        Log.i(TAG, "Tasso di Successo Complessivo: ${String.format("%.1f%%", successRate * 100)}")

        SimulationResult(successRate)
    }
}
