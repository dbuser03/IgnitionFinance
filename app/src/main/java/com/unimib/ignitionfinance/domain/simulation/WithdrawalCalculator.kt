package com.unimib.ignitionfinance.domain.simulation

import android.util.Log

object WithdrawalCalculator {
    private const val MONTHS_PER_YEAR = 13
    private const val TAG = "WithdrawalCalculator"
    private const val AVERAGE_TAG = "WITHDRAWAL_AVERAGE"

    fun calculateWithdrawals(
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
        for (sim in 0 until numSimulations) {
            withdrawals[0][sim] = annualInitialWithdrawal
        }

        val coef = DoubleArray(numSimulations) { 1.0 }

        // Calcolo dei prelievi per i primi anni senza pensione
        for (t in 1..yearsWithoutPension) {
            for (sim in 0 until numSimulations) {
                withdrawals[t][sim] = withdrawals[t - 1][sim] * (1 + inflationMatrix[t][sim])
                coef[sim] *= (1 + inflationMatrix[t][sim])
            }
        }

        // Al cambio dalla fase senza pensione alla fase con pensione
        if (yearsWithoutPension + 1 < simulationLength) {
            for (sim in 0 until numSimulations) {
                withdrawals[yearsWithoutPension + 1][sim] =
                    annualPensionWithdrawal * coef[sim] * (1 + inflationMatrix[yearsWithoutPension + 1][sim])
            }
        }

        // Prelievi per gli anni successivi
        for (t in (yearsWithoutPension + 2) until simulationLength) {
            for (sim in 0 until numSimulations) {
                withdrawals[t][sim] = withdrawals[t - 1][sim] * (1 + inflationMatrix[t][sim])
            }
        }

        // **Log dettagliati: prelievi annuali medi**
        for (t in 0 until simulationLength) {
            val averageWithdrawal = withdrawals[t].average()
            Log.d(AVERAGE_TAG, "Year $t - Average Annual Withdrawal: $averageWithdrawal")
        }

        // **Stampa della matrice completa dei prelievi**
        for (t in 0 until simulationLength) {
            val rowString = withdrawals[t].joinToString(prefix = "[", postfix = "]", separator = ", ")
            Log.d(TAG, "Year $t: $rowString")
        }

        return withdrawals
    }
}
