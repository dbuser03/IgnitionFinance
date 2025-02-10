package com.unimib.ignitionfinance.domain.simulation

import android.util.Log
import kotlinx.coroutines.*

object WithdrawalCalculator {
    private const val MONTHS_PER_YEAR = 13
    private const val TAG = "WithdrawalCalculator"
    private const val AVERAGE_TAG = "WITHDRAWAL_AVERAGE"

    suspend fun calculateWithdrawals(
        initialWithdrawal: Double,
        yearsWithoutPension: Int,
        pensionWithdrawal: Double,
        inflationMatrix: Array<DoubleArray>
    ): Array<DoubleArray> {
        val simulationLength = inflationMatrix.size
        if (simulationLength == 0) return arrayOf()
        val numSimulations = inflationMatrix[0].size

        val annualInitialWithdrawal = initialWithdrawal * MONTHS_PER_YEAR
        val annualPensionWithdrawal = pensionWithdrawal * MONTHS_PER_YEAR

        val withdrawals = Array(simulationLength) { DoubleArray(numSimulations) { 0.0 } }
        val coef = DoubleArray(numSimulations) { 1.0 }

        // Initialize first row
        for (sim in 0 until numSimulations) {
            withdrawals[0][sim] = annualInitialWithdrawal
        }

        withContext(Dispatchers.Default) {
            // Phase 1: Years without pension (parallel processing by simulation)
            val jobs1 = List(numSimulations) { sim ->
                async {
                    for (t in 1..yearsWithoutPension) {
                        withdrawals[t][sim] = withdrawals[t - 1][sim] * (1 + inflationMatrix[t][sim])
                        coef[sim] *= (1 + inflationMatrix[t][sim])
                    }
                }
            }
            jobs1.awaitAll()

            // Phase 2: Transition year (parallel processing)
            if (yearsWithoutPension + 1 < simulationLength) {
                val jobs2 = List(numSimulations) { sim ->
                    async {
                        withdrawals[yearsWithoutPension + 1][sim] =
                            annualPensionWithdrawal * coef[sim] * (1 + inflationMatrix[yearsWithoutPension + 1][sim])
                    }
                }
                jobs2.awaitAll()
            }

            // Phase 3: Remaining years (parallel processing by simulation)
            val jobs3 = List(numSimulations) { sim ->
                async {
                    for (t in (yearsWithoutPension + 2) until simulationLength) {
                        withdrawals[t][sim] = withdrawals[t - 1][sim] * (1 + inflationMatrix[t][sim])
                    }
                }
            }
            jobs3.awaitAll()
        }

        // Log average withdrawals (can be done after parallel computation)
        for (t in 0 until simulationLength) {
            val averageWithdrawal = withdrawals[t].average()
            Log.d(AVERAGE_TAG, "Year $t - Average Annual Withdrawal: $averageWithdrawal")
        }

        return withdrawals
    }
}