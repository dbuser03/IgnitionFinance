package com.unimib.ignitionfinance.domain.simulation.inflation

import com.unimib.ignitionfinance.domain.utils.RandomUtils
import kotlin.math.*
import kotlin.random.Random
import javax.inject.Inject

class InflationScenarioGenerator @Inject constructor(
    private val inflationDataProvider: InflationDataProvider,
    private val numSimulations: Int = 1000,
    private val averageInflation: Double = 0.03
) {
    suspend fun generateInflationScenarios(scenarioType: String): Array<DoubleArray> {
        val realInflation = inflationDataProvider.getHistoricalInflationData()

        if (realInflation.isEmpty()) {
            return Array(100) { DoubleArray(numSimulations) { averageInflation } }
        }

        return when (scenarioType.lowercase()) {
            "real" -> generateRealInflationScenario(realInflation)
            "scale" -> generateScaledRealInflationScenario(realInflation)
            "lognormal" -> generateLogNormalInflationScenario(realInflation)
            else -> generateDefaultInflationScenario()
        }
    }

    private fun generateRealInflationScenario(realInflation: DoubleArray): Array<DoubleArray> {
        return Array(100) {
            DoubleArray(numSimulations) {
                realInflation[Random.nextInt(realInflation.size)]
            }
        }
    }

    private fun generateScaledRealInflationScenario(realInflation: DoubleArray): Array<DoubleArray> {
        val meanReal = realInflation.average()
        val scaleFactor = averageInflation / meanReal
        val scaledInflation = realInflation.map { it * scaleFactor }.toDoubleArray()

        return Array(100) {
            DoubleArray(numSimulations) {
                scaledInflation[Random.nextInt(scaledInflation.size)]
            }
        }
    }

    private fun generateLogNormalInflationScenario(realInflation: DoubleArray): Array<DoubleArray> {
        val variance = realInflation.map { it * it }.average() - realInflation.average().pow(2)
        var mu = ln(averageInflation)
        var sigma = ln((1 + sqrt(1 + 4 * variance / exp(2 * mu))) / 2)
        mu = ln(averageInflation) - sigma.pow(2) / 2
        sigma = ln((1 + sqrt(1 + 4 * variance / exp(2 * mu))) / 2)
        mu = ln(averageInflation) - sigma.pow(2) / 2

        return Array(100) {
            DoubleArray(numSimulations) {
                val z = RandomUtils.nextGaussian()
                exp(mu + sigma * z)
            }
        }
    }

    private fun generateDefaultInflationScenario(): Array<DoubleArray> {
        return Array(100) { DoubleArray(numSimulations) { averageInflation } }
    }

    fun analyzeInflationScenario(inflation: Array<DoubleArray>) {
        val flatInflation = inflation.flatMap { it.toList() }
        val mean = flatInflation.average()
        val stdDev = sqrt(flatInflation.map { (it - mean).pow(2) }.average())
    }
}