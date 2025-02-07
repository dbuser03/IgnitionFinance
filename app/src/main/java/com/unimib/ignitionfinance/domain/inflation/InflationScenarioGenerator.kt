package com.unimib.ignitionfinance.domain.inflation

import com.unimib.ignitionfinance.domain.utils.RandomUtils
import kotlin.math.*
import kotlin.random.Random
import javax.inject.Inject

class InflationScenarioGenerator @Inject constructor(
    private val inflationDataProvider: InflationDataProvider,
    private val numSimulazioni: Int = 1000,
    private val inflazioneMedia: Double = 0.03
) {
    suspend fun generateInflationScenarios(scenarioType: String): Array<DoubleArray> {
        val inflazioneReale = inflationDataProvider.getHistoricalInflationData()

        if (inflazioneReale.isEmpty()) {
            println("Nessun dato di inflazione disponibile. Uso l'inflazione media.")
            return Array(100) { DoubleArray(numSimulazioni) { inflazioneMedia } }
        }

        return when (scenarioType.lowercase()) {
            "reale" -> generateRealInflationScenario(inflazioneReale)
            "reale riscalata" -> generateScaledRealInflationScenario(inflazioneReale)
            "lognormale" -> generateLogNormalInflationScenario(inflazioneReale)
            else -> generateDefaultInflationScenario()
        }
    }

    private fun generateRealInflationScenario(inflazioneReale: DoubleArray): Array<DoubleArray> {
        return Array(100) {
            DoubleArray(numSimulazioni) {
                inflazioneReale[Random.nextInt(inflazioneReale.size)]
            }
        }
    }

    private fun generateScaledRealInflationScenario(inflazioneReale: DoubleArray): Array<DoubleArray> {
        val meanReale = inflazioneReale.average()
        val scaleFactor = inflazioneMedia / meanReale
        val inflazioneRiscalata = inflazioneReale.map { it * scaleFactor }.toDoubleArray()

        return Array(100) {
            DoubleArray(numSimulazioni) {
                inflazioneRiscalata[Random.nextInt(inflazioneRiscalata.size)]
            }
        }
    }

    private fun generateLogNormalInflationScenario(inflazioneReale: DoubleArray): Array<DoubleArray> {
        val variance = inflazioneReale.map { it * it }.average() - inflazioneReale.average().pow(2)
        var mu = ln(inflazioneMedia)
        var sigma = ln((1 + sqrt(1 + 4 * variance / exp(2 * mu))) / 2)
        mu = ln(inflazioneMedia) - sigma.pow(2) / 2
        sigma = ln((1 + sqrt(1 + 4 * variance / exp(2 * mu))) / 2)
        mu = ln(inflazioneMedia) - sigma.pow(2) / 2

        return Array(100) {
            DoubleArray(numSimulazioni) {
                val z = RandomUtils.nextGaussian()
                exp(mu + sigma * z)
            }
        }
    }

    private fun generateDefaultInflationScenario(): Array<DoubleArray> {
        println("Scenario di inflazione non riconosciuto. Uso l'inflazione media.")
        return Array(100) { DoubleArray(numSimulazioni) { inflazioneMedia } }
    }

    fun analyzeInflationScenario(inflazione: Array<DoubleArray>) {
        val flatInflazione = inflazione.flatMap { it.toList() }
        val media = flatInflazione.average()
        val devSt = sqrt(flatInflazione.map { (it - media).pow(2) }.average())

        println("Media inflazione: $media Dev st: $devSt")
    }
}