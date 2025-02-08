package com.unimib.ignitionfinance.domain.simulation

import android.util.Log
import com.unimib.ignitionfinance.data.remote.model.user.DailyReturn
import kotlin.random.Random

object AnnualReturnsMatrixGenerator {

    private const val TAG = "AnnualReturnsMatrix"

    fun generateMatrices(
        dataset: List<DailyReturn>,
        numSimulations: Int,
        simulationLength: Int = 100,
        blockYears: Int = 1,
        daysPerYear: Int = 253
    ): Pair<Array<DoubleArray>, Array<DoubleArray>> {
        if (dataset.size < blockYears * daysPerYear + 1) {
            throw IllegalArgumentException("The dataset is too small for blockYears=$blockYears and daysPerYear=$daysPerYear")
        }

        val maxStartIndex = dataset.size - blockYears * daysPerYear - 1

        val annualReturnsMatrix = Array(simulationLength) { DoubleArray(numSimulations) { 0.0 } }
        val cumulativeReturnsMatrix = Array(simulationLength) { DoubleArray(numSimulations) { 0.0 } }

        for (c in 0 until numSimulations) {
            annualReturnsMatrix[0][c] = 1.0
            cumulativeReturnsMatrix[0][c] = 1.0
        }

        for (c in 0 until numSimulations) {
            var currentIndex = 0
            for (t in 1 until simulationLength) {
                if ((t - 1) % blockYears == 0) {
                    currentIndex = Random.nextInt(0, maxStartIndex + 1)
                }
                val annualReturnValue = dataset[currentIndex].weightedReturn.toDouble()
                val multiplier = 1 + annualReturnValue

                annualReturnsMatrix[t][c] = multiplier
                cumulativeReturnsMatrix[t][c] = cumulativeReturnsMatrix[t - 1][c] * multiplier

                currentIndex += daysPerYear
            }
        }

        return Pair(cumulativeReturnsMatrix, annualReturnsMatrix)
    }
}