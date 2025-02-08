package com.unimib.ignitionfinance.domain.simulation

object WithdrawalCalculator {
    private const val MONTHS_PER_YEAR = 13

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

        for (t in 1 .. yearsWithoutPension) {
            for (sim in 0 until numSimulations) {
                withdrawals[t][sim] = withdrawals[t - 1][sim] * (1 + inflationMatrix[t][sim])
                coef[sim] *= (1 + inflationMatrix[t][sim])
            }
        }

        if (yearsWithoutPension + 1 < simulationLength) {
            for (sim in 0 until numSimulations) {
                withdrawals[yearsWithoutPension + 1][sim] =
                    annualPensionWithdrawal * coef[sim] * (1 + inflationMatrix[yearsWithoutPension + 1][sim])
            }
        }

        for (t in (yearsWithoutPension + 2) until simulationLength) {
            for (sim in 0 until numSimulations) {
                withdrawals[t][sim] = withdrawals[t - 1][sim] * (1 + inflationMatrix[t][sim])
            }
        }

        return withdrawals
    }
}