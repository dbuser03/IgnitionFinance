package com.unimib.ignitionfinance.domain.portfolio

import com.unimib.ignitionfinance.domain.inflation.InflationScenarioGenerator
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.sqrt

class FireSimulation @Inject constructor(
    private val inflationScenarioGenerator: InflationScenarioGenerator,
    private val portfolioReturnsStrategy: PortfolioReturnsStrategy,
    private val config: FireSimulationConfig = FireSimulationConfig()
) {
    suspend fun simulate(): PerformanceResult {
        val (capitaleCarico, fire, cash) = simulaConCash()
        val totale = calculateTotalPortfolio(fire, cash)

        return calculatePerformanceMetrics(totale)
    }

    private suspend fun simulaConCash(): Triple<DoubleArray, Array<DoubleArray>, Array<DoubleArray>> {
        // Similar to previous implementation, but using injected dependencies
        val fire = Array(100) { DoubleArray(config.numSimulazioni) }
        val cash = Array(100) { DoubleArray(config.numSimulazioni) }
        val capitaleCarico = DoubleArray(config.numSimulazioni) { config.capitale * config.percentualeCarico }

        // Initial allocation setup
        for (j in 0 until config.numSimulazioni) {
            fire[0][j] = config.capitale * (1 - config.percCash)
            cash[0][j] = config.capitale * config.percCash
        }

        // Inflation and returns simulation
        val inflazione = inflationScenarioGenerator.generateInflationScenarios("fissa")
        val rendimenti = portfolioReturnsStrategy.simulaRendimenti(config.rendimentoMedio, config.numSimulazioni)

        // Simulation logic remains similar to previous implementation
        // ... (detailed implementation would go here)

        return Triple(capitaleCarico, fire, cash)
    }

    private fun calculateTotalPortfolio(fire: Array<DoubleArray>, cash: Array<DoubleArray>): Array<DoubleArray> {
        return Array(100) { i ->
            DoubleArray(fire[0].size) { j ->
                fire[i][j] + cash[i][j]
            }
        }
    }

    private fun calculatePerformanceMetrics(totale: Array<DoubleArray>): PerformanceResult {
        val successRate = (0 until config.numSimulazioni)
            .count { totale[config.anniRendita][it] > 0 }
            .toDouble() / config.numSimulazioni * 100

        val mediaTotale = (0 until config.numSimulazioni)
            .map { totale[config.anniRendita][it] }
            .average()

        val deviazioneStandardTotale = sqrt(
            (0 until config.numSimulazioni)
                .map { (totale[config.anniRendita][it] - mediaTotale).pow(2) }
                .average()
        )

        return PerformanceResult(successRate, mediaTotale, deviazioneStandardTotale)
    }
}