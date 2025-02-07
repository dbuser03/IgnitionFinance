package com.unimib.ignitionfinance.domain.simulation

import com.unimib.ignitionfinance.domain.simulation.model.Capital
import com.unimib.ignitionfinance.domain.simulation.model.SimulationConfig
import com.unimib.ignitionfinance.domain.simulation.model.SimulationResult

object FireSimulator {
    private const val SIMULATION_YEARS = 100
    private const val TARGET_SUCCESS_RATE = 95.0
    private const val CAPITAL_SEARCH_PRECISION = 10000.0
    private const val INITIAL_UPPER_BOUND_MULTIPLIER = 5.0

    fun simulatePortfolio(
        config: SimulationConfig,
        marketReturnsMatrix: Array<DoubleArray>,
        withdrawalMatrix: Array<DoubleArray>
    ): SimulationResult {
        val baseSuccessRate = simulateWithCapital(config, marketReturnsMatrix, withdrawalMatrix)

        val plus100kRate = simulateWithCapital(config, marketReturnsMatrix, withdrawalMatrix, extraCapital = 100_000.0)
        val plus200kRate = simulateWithCapital(config, marketReturnsMatrix, withdrawalMatrix, extraCapital = 200_000.0)
        val plus300kRate = simulateWithCapital(config, marketReturnsMatrix, withdrawalMatrix, extraCapital = 300_000.0)

        return SimulationResult(
            successRate = baseSuccessRate,
            fuckYouMoney = calculateFuckYouMoney(config, marketReturnsMatrix, withdrawalMatrix),
            successRatePlus100k = plus100kRate,
            successRatePlus200k = plus200kRate,
            successRatePlus300k = plus300kRate
        )
    }

    private fun simulateWithCapital(
        config: SimulationConfig,
        marketReturnsMatrix: Array<DoubleArray>,
        withdrawalMatrix: Array<DoubleArray>,
        extraCapital: Double = 0.0
    ): Double {
        val effectiveConfig = if (extraCapital > 0.0) {
            val totalCapital = config.capital.invested + config.capital.cash
            val investedPercentage = config.capital.invested / totalCapital
            val cashPercentage = config.capital.cash / totalCapital
            val newTotalCapital = totalCapital + extraCapital
            config.copy(
                capital = Capital(
                    invested = newTotalCapital * investedPercentage,
                    cash = newTotalCapital * cashPercentage
                )
            )
        } else {
            config
        }

        val numSimulations = effectiveConfig.settings.numberOfSimulations.toInt()

        val investedPortfolio = Array(SIMULATION_YEARS) { DoubleArray(numSimulations) }
        val cashPortfolio = Array(SIMULATION_YEARS) { DoubleArray(numSimulations) }
        val investedCostBasis = DoubleArray(numSimulations) {
            effectiveConfig.capital.invested * effectiveConfig.settings.expenses.loadPercentage.toDouble() / 100
        }

        investedPortfolio[0] = DoubleArray(numSimulations) { effectiveConfig.capital.invested }
        cashPortfolio[0] = DoubleArray(numSimulations) { effectiveConfig.capital.cash }

        val taxRate = effectiveConfig.settings.expenses.taxRatePercentage.toDouble() / 100
        val stampDuty = effectiveConfig.settings.expenses.stampDutyPercentage.toDouble() / 100
        val cashInterestRate = effectiveConfig.simulationParams.cashInterestRate

        for (year in 0 until SIMULATION_YEARS - 1) {
            simulateYearlyPortfolio(
                year = year,
                investedPortfolio = investedPortfolio,
                cashPortfolio = cashPortfolio,
                investedCostBasis = investedCostBasis,
                withdrawals = withdrawalMatrix[year],
                marketReturns = marketReturnsMatrix[year + 1],
                taxRate = taxRate,
                stampDuty = stampDuty,
                cashInterestRate = cashInterestRate
            )
        }


        return calculateSuccessRate(
            investedPortfolio = investedPortfolio,
            cashPortfolio = cashPortfolio,
            targetYear = effectiveConfig.settings.intervals.yearsInFIRE.toInt()
        )
    }

    private fun simulateYearlyPortfolio(
        year: Int,
        investedPortfolio: Array<DoubleArray>,
        cashPortfolio: Array<DoubleArray>,
        investedCostBasis: DoubleArray,
        withdrawals: DoubleArray,
        marketReturns: DoubleArray,
        taxRate: Double,
        stampDuty: Double,
        cashInterestRate: Double
    ) {
        for (sim in withdrawals.indices) {
            val requiredWithdrawal = withdrawals[sim]

            val withdrawalFromCash = minOf(cashPortfolio[year][sim], requiredWithdrawal)
            val remainingWithdrawal = requiredWithdrawal - withdrawalFromCash

            if (remainingWithdrawal > 0 && investedPortfolio[year][sim] > 0) {

                val taxableGain = remainingWithdrawal * (1 - investedCostBasis[sim] / investedPortfolio[year][sim])
                val taxAmount = taxableGain * taxRate

                val totalWithdrawal = remainingWithdrawal + taxAmount

                val oldInvested = investedPortfolio[year][sim]

                investedPortfolio[year][sim] -= totalWithdrawal

                if (oldInvested > 0) {
                    investedCostBasis[sim] *= (investedPortfolio[year][sim] / oldInvested)
                } else {
                    investedCostBasis[sim] = 0.0
                }
            }

            cashPortfolio[year][sim] -= withdrawalFromCash

            investedPortfolio[year + 1][sim] = investedPortfolio[year][sim] * marketReturns[sim]

            cashPortfolio[year + 1][sim] = cashPortfolio[year][sim] * (1 + cashInterestRate)

            val totalPortfolio = investedPortfolio[year + 1][sim] + cashPortfolio[year + 1][sim]
            if (totalPortfolio > 0) {
                val fee = totalPortfolio * stampDuty
                val investedProportion = investedPortfolio[year + 1][sim] / totalPortfolio

                investedPortfolio[year + 1][sim] -= fee * investedProportion
                cashPortfolio[year + 1][sim] -= fee * (1 - investedProportion)
            }
        }
    }


    private fun calculateSuccessRate(
        investedPortfolio: Array<DoubleArray>,
        cashPortfolio: Array<DoubleArray>,
        targetYear: Int
    ): Double {
        var successCount = 0
        val numSimulations = investedPortfolio[0].size

        for (sim in 0 until numSimulations) {
            if (investedPortfolio[targetYear][sim] + cashPortfolio[targetYear][sim] > 0) {
                successCount++
            }
        }

        return successCount.toDouble() / numSimulations * 100
    }

    private fun calculateFuckYouMoney(
        config: SimulationConfig,
        marketReturnsMatrix: Array<DoubleArray>,
        withdrawalMatrix: Array<DoubleArray>
    ): Double {
        var lowerBound = config.capital.invested + config.capital.cash
        var upperBound = lowerBound * INITIAL_UPPER_BOUND_MULTIPLIER

        while (upperBound - lowerBound > CAPITAL_SEARCH_PRECISION) {
            val midPoint = (lowerBound + upperBound) / 2
            val currentCapital = config.capital.invested + config.capital.cash
            val extraCapital = midPoint - currentCapital

            val successRate = simulateWithCapital(
                config = config,
                marketReturnsMatrix = marketReturnsMatrix,
                withdrawalMatrix = withdrawalMatrix,
                extraCapital = extraCapital
            )

            if (successRate >= TARGET_SUCCESS_RATE) {
                upperBound = midPoint
            } else {
                lowerBound = midPoint
            }
        }

        return upperBound
    }
}