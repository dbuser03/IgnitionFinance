package com.unimib.ignitionfinance.domain.inflation

import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.sqrt

class LiquidityErosionCalculator @Inject constructor(
    private val inflationScenarioGenerator: InflationScenarioGenerator
) {
    suspend fun calculateLiquidityErosion(
        liquiditaIniziale: Double,
        scenarioInflazione: String
    ): Array<DoubleArray> {
        val inflazione = inflationScenarioGenerator.generateInflationScenarios(scenarioInflazione)
        val liquiditaErosa = Array(100) { DoubleArray(inflazione[0].size) }

        for (j in inflazione[0].indices) {
            var liquiditaCorrente = liquiditaIniziale
            for (i in 0 until 100) {
                liquiditaCorrente /= (1 + inflazione[i][j])
                liquiditaErosa[i][j] = liquiditaCorrente
            }
        }

        analyzeErosionResults(liquiditaIniziale, liquiditaErosa)

        return liquiditaErosa
    }

    private fun analyzeErosionResults(liquiditaIniziale: Double, liquiditaErosa: Array<DoubleArray>) {
        val liquiditaFinale = liquiditaErosa[99].toList()
        val mediaFinale = liquiditaFinale.average()
        val devStFinale = sqrt(liquiditaFinale.map { (it - mediaFinale).pow(2) }.average())

        println("Statistiche finale dopo 100 anni:")
        println("Liquidita' iniziale: $liquiditaIniziale")
        println("Media liquidita' finale: $mediaFinale")
        println("Deviazione standard finale: $devStFinale")
        println("Perdita media: ${(1 - mediaFinale/liquiditaIniziale) * 100}%")
    }
}