package com.unimib.ignitionfinance.domain.portfolio

import kotlin.math.ln
import kotlin.math.exp
import kotlin.math.sqrt
import kotlin.math.pow
import kotlin.random.Random

class CashScenarioGenerator {
    companion object {
        private val REAL_INFLATION = doubleArrayOf(
            0.023, 0.034, 0.013, 0.028, -0.004, 0.023, 0.021, 0.047, 0.075, 0.059,
            0.046, 0.023, 0.037, 0.014, 0.026, 0.050, 0.048, 0.057, 0.108, 0.191,
            0.170, 0.168, 0.170, 0.121, 0.148, 0.212, 0.178, 0.165, 0.147, 0.108,
            0.092, 0.058, 0.048, 0.050, 0.063, 0.065, 0.062, 0.053, 0.047, 0.041,
            0.053, 0.040, 0.020, 0.020, 0.017, 0.025, 0.027, 0.025, 0.027, 0.022,
            0.019, 0.021, 0.018, 0.033, 0.008, 0.015, 0.027, 0.030, 0.012, 0.002,
            0.001, -0.001, 0.012, 0.012, 0.006, -0.002, 0.019, 0.081, 0.087
        )
    }

    fun generateInflationScenarios(
        scenarioType: String,
        inflationMean: Double = 0.03,
        numSimulations: Int = 1000
    ): Array<DoubleArray> {
        return when (scenarioType) {
            "fissa" -> Array(100) { DoubleArray(numSimulations) { inflationMean } }
            "reale" -> generateRealScenarios(inflationMean, numSimulations)
            "reale riscalata" -> generateRescaledRealScenarios(inflationMean, numSimulations)
            "lognormale" -> generateLognormalScenarios(inflationMean, numSimulations)
            else -> {
                println("Ciccio, guarda che non so come gestire l'inflazione!")
                Array(3) { doubleArrayOf(inflationMean) }
            }
        }
    }

    private fun generateRealScenarios(
        inflationMean: Double,
        numSimulations: Int
    ): Array<DoubleArray> {
        return Array(100) { DoubleArray(numSimulations) { REAL_INFLATION[Random.nextInt(REAL_INFLATION.size)] } }
    }

    private fun generateRescaledRealScenarios(
        inflationMean: Double,
        numSimulations: Int
    ): Array<DoubleArray> {
        val scaleFactor = inflationMean / REAL_INFLATION.average()
        return Array(100) { DoubleArray(numSimulations) { REAL_INFLATION[Random.nextInt(REAL_INFLATION.size)] * scaleFactor } }
    }

    private fun generateLognormalScenarios(
        inflationMean: Double,
        numSimulations: Int
    ): Array<DoubleArray> {
        val mu = ln(inflationMean) - calculateSigma(inflationMean)
        val sigma = calculateSigma(inflationMean)
        return Array(100) { DoubleArray(numSimulations) { exp(mu + sigma * Random.nextDouble()) } }
    }

    private fun calculateSigma(inflationMean: Double): Double {
        val variance = REAL_INFLATION.map { (it - REAL_INFLATION.average()).pow(2) }.average()
        val logMean = ln(inflationMean)
        return sqrt(ln((1 + sqrt(1 + 4 * variance / exp(2 * logMean))) / 2))
    }
}