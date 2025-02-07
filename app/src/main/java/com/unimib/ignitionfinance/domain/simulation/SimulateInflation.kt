package com.unimib.ignitionfinance.domain.simulation

import com.unimib.ignitionfinance.domain.usecase.fetch.FetchInflationUseCase
import com.unimib.ignitionfinance.domain.utils.RandomUtils.nextGaussian
import kotlinx.coroutines.flow.first
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.math.pow
import javax.inject.Inject

class SimulateInflation @Inject constructor(
    private val fetchInflationUseCase: FetchInflationUseCase,
    private val numSimulations: Int,
    private val averageInflation: Double
) {
    private suspend fun getRealInflation(): DoubleArray {
        return try {
            val inflationData = fetchInflationUseCase.execute().first()
            inflationData.fold(
                onSuccess = { data ->
                    data.values.map { it / 100.0 }.toDoubleArray()
                },
                onFailure = { exception ->
                    println("Error fetching inflation data: ${exception.message}")
                    doubleArrayOf()
                }
            )
        } catch (e: Exception) {
            println("Error fetching inflation data: ${e.message}")
            doubleArrayOf()
        }
    }

    private suspend fun setInflation(inflationScenario: String): Array<DoubleArray> {
        val inflation = Array(100) { DoubleArray(numSimulations) }
        val realInflation = getRealInflation()

        if (realInflation.isEmpty()) {
            println("No inflation data available. Using average inflation.")
            return Array(100) { DoubleArray(numSimulations) { averageInflation } }
        }

        when (inflationScenario.lowercase()) {
            "real" -> {
                for (i in 0 until 100) {
                    for (j in 0 until numSimulations) {
                        inflation[i][j] = realInflation[Random.nextInt(realInflation.size)]
                    }
                }
            }

            "scaled real" -> {
                val meanReal = realInflation.average()
                val scaleFactor = averageInflation / meanReal
                val scaledInflation = realInflation.map { it * scaleFactor }.toDoubleArray()

                for (i in 0 until 100) {
                    for (j in 0 until numSimulations) {
                        inflation[i][j] = scaledInflation[Random.nextInt(scaledInflation.size)]
                    }
                }
            }

            "lognormal" -> {
                val variance = realInflation.map { it * it }.average() - realInflation.average().pow(2)
                var mu = ln(averageInflation)
                var sigma = ln((1 + sqrt(1 + 4 * variance / exp(2 * mu))) / 2)
                mu = ln(averageInflation) - sigma.pow(2) / 2
                sigma = ln((1 + sqrt(1 + 4 * variance / exp(2 * mu))) / 2)
                mu = ln(averageInflation) - sigma.pow(2) / 2

                for (i in 0 until 100) {
                    for (j in 0 until numSimulations) {
                        val z = nextGaussian()
                        inflation[i][j] = exp(mu + sigma * z)
                    }
                }
            }

            else -> {
                println("Unrecognized inflation scenario. Using average inflation.")
                for (i in 0 until 100) {
                    for (j in 0 until numSimulations) {
                        inflation[i][j] = averageInflation
                    }
                }
            }
        }

        val flatInflation = mutableListOf<Double>()
        for (i in 0 until 100) {
            for (j in 0 until numSimulations) {
                flatInflation.add(inflation[i][j])
            }
        }
        val mean = flatInflation.average()
        val stdDev = sqrt(flatInflation.map { (it - mean).pow(2) }.average())
        println("Mean: $mean Std Dev: $stdDev")

        return inflation
    }

    suspend fun calculateLiquidityErosion(initialLiquidity: Double, inflationScenario: String): Array<DoubleArray> {
        val inflation = setInflation(inflationScenario)
        val erodedLiquidity = Array(100) { DoubleArray(numSimulations) }

        for (j in 0 until numSimulations) {
            var currentLiquidity = initialLiquidity
            for (i in 0 until 100) {
                currentLiquidity /= (1 + inflation[i][j])
                erodedLiquidity[i][j] = currentLiquidity
            }
        }

        val finalLiquidity = mutableListOf<Double>()
        for (j in 0 until numSimulations) {
            finalLiquidity.add(erodedLiquidity[99][j])
        }

        val meanFinal = finalLiquidity.average()
        val stdDevFinal = sqrt(finalLiquidity.map { (it - meanFinal).pow(2) }.average())

        return erodedLiquidity
    }
}