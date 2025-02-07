package com.unimib.ignitionfinance.domain.portfolio

import com.unimib.ignitionfinance.domain.inflation.InflationScenarioGenerator
import com.unimib.ignitionfinance.domain.models.FireSimulationConfig
import com.unimib.ignitionfinance.domain.models.PerformanceResult
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.max

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
        // Arrays per simulazioni
        val fire = Array(100) { DoubleArray(config.numSimulazioni) }
        val cash = Array(100) { DoubleArray(config.numSimulazioni) }
        val capitaleCarico = DoubleArray(config.numSimulazioni) { config.capitale * config.percentualeCarico }

        // Allocazione iniziale
        for (j in 0 until config.numSimulazioni) {
            fire[0][j] = config.capitale * (1 - config.percCash)
            cash[0][j] = config.capitale * config.percCash
        }

        // Generazione scenari
        val inflazione = inflationScenarioGenerator.generateInflationScenarios("fissa")
        val rendimenti = portfolioReturnsStrategy.simulaRendimenti(config.rendimentoMedio, config.numSimulazioni)

        // Simulazione per ogni anno e ogni simulazione
        for (anno in 1 until 100) {
            for (simulazione in 0 until config.numSimulazioni) {
                // Prelievo annuale
                val prelievoAnnuo = config.prelievo * (1 + inflazione[anno][simulazione] / 100)

                // Rendimento investimenti
                val rendimentoFire = fire[anno-1][simulazione] * (1 + rendimenti[anno][simulazione] / 100)
                val rendimentoCash = cash[anno-1][simulazione] * (1 + config.rendimentoCash / 100)

                // Ribilanciamento
                val fireDopoPrelievo = max(rendimentoFire - prelievoAnnuo * config.percFire, 0.0)
                val cashDopoPrelievo = max(rendimentoCash - prelievoAnnuo * config.percCash, 0.0)

                // Ri-allocazione se necessario
                if (fireDopoPrelievo == 0.0) {
                    fire[anno][simulazione] = 0.0
                    cash[anno][simulazione] = cashDopoPrelievo
                } else {
                    fire[anno][simulazione] = fireDopoPrelievo
                    cash[anno][simulazione] = cashDopoPrelievo
                }
            }
        }

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

        // Calcolo Fuck You Money (quando success rate > 95%)
        val fuckYouMoney = (0 until config.numSimulazioni)
            .filter { totale[config.anniRendita][it] > 0 && successRate > 95 }
            .map { totale[config.anniRendita][it] }
            .takeIf { it.isNotEmpty() }?.average() ?: 0.0

        // Calcolo success rate per capitali extra
        val successRateAt100k = calculateSuccessRateWithExtraCapital(totale, 100_000.0)
        val successRateAt200k = calculateSuccessRateWithExtraCapital(totale, 200_000.0)
        val successRateAt300k = calculateSuccessRateWithExtraCapital(totale, 300_000.0)

        return PerformanceResult(
            successRate = successRate,
            mediaTotale = mediaTotale,
            deviazioneStandardTotale = deviazioneStandardTotale,
            fuckYouMoney = fuckYouMoney,
            successRateAt100k = successRateAt100k,
            successRateAt200k = successRateAt200k,
            successRateAt300k = successRateAt300k
        )
    }

    private fun calculateSuccessRateWithExtraCapital(
        totale: Array<DoubleArray>,
        extraCapital: Double
    ): Double {
        val simulazioniConExtraCapital = (0 until config.numSimulazioni)
            .map { simulazioneIndex ->
                // Simula con capitale aumentato di extraCapital
                val totaleSimulazione = totale[config.anniRendita][simulazioneIndex] + extraCapital
                totaleSimulazione > 0
            }

        return simulazioniConExtraCapital.count { it }.toDouble() / config.numSimulazioni * 100
    }
}