package com.unimib.ignitionfinance.domain.simulation

import android.util.Log
import kotlin.math.*
import kotlin.random.Random

object InflationModel {

    private fun List<Double>.variance(): Double {
        val mean = this.average()
        return this.map { (it - mean).pow(2) }.average()
    }

    fun generateInflationMatrix(
        scenarioInflation: String,
        inflationMean: Double,
        historicalInflation: List<Double>,
        numSimulations: Int,
        simulationLength: Int = 100
    ): Array<DoubleArray> {
        val TAG = "INFLATION_MODEL_LOG"
        Log.d(TAG, "Historical inflation data: ${historicalInflation.joinToString(", ")}")


        val inflationMatrix: Array<DoubleArray> = when (scenarioInflation.lowercase()) {
            "normal" -> {
                Array(simulationLength) { DoubleArray(numSimulations) {
                    historicalInflation[Random.nextInt(historicalInflation.size)]
                } }
            }
            "scale" -> {
                val scaleFactor = inflationMean / historicalInflation.average()
                Array(simulationLength) { DoubleArray(numSimulations) {
                    historicalInflation[Random.nextInt(historicalInflation.size)] * scaleFactor
                } }
            }
            "lognormal" -> {
                val variance = historicalInflation.variance()
                var muCalc = ln(inflationMean)
                var sigmaCalc = ln((1 + sqrt(1 + 4 * variance / exp(2 * muCalc))) / 2)
                muCalc = ln(inflationMean) - (sigmaCalc * sigmaCalc) / 2
                sigmaCalc = ln((1 + sqrt(1 + 4 * variance / exp(2 * muCalc))) / 2)
                muCalc = ln(inflationMean) - (sigmaCalc * sigmaCalc) / 2

                val rand = java.util.Random()
                Array(simulationLength) { DoubleArray(numSimulations) {
                    exp(muCalc + sigmaCalc * rand.nextGaussian())
                } }
            }
            else -> {
                Array(simulationLength) { DoubleArray(numSimulations) { inflationMean } }
            }
        }
        Log.d(TAG, "Inflation Matrix: ${inflationMatrix.contentDeepToString()}")

        return inflationMatrix
    }
}