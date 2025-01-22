package com.unimib.ignitionfinance.domain.utils

import com.unimib.ignitionfinance.data.model.user.DailyReturn
import com.unimib.ignitionfinance.data.model.dataset.HistoricalData
import java.math.BigDecimal
import java.math.RoundingMode

class ReturnCalculator {
    /**
     * Calculates daily returns from historical data for multiple products
     * @param historicalData Map of product ID to its historical data
     * @param capitals Map of product ID to its capital amount
     * @return List of DailyReturn objects
     */
    fun calculateDailyReturns(
        historicalData: Map<String, List<HistoricalData>>,
        capitals: Map<String, BigDecimal>
    ): List<DailyReturn> {
        // Get all unique dates from all products
        val allDates = historicalData.values.flatten()
            .map { it.date }
            .distinct()
            .sorted()

        return allDates.map { date ->
            // Get data for all products on this date
            val dailyData = historicalData.mapNotNull { (productId, data) ->
                val dayData = data.find { it.date == date }
                if (dayData != null) {
                    Pair(productId, dayData)
                } else null
            }

            when {
                // Case 4a: Only one product available for this date
                dailyData.size == 1 -> {
                    val (productId, data) = dailyData.first()
                    val dailyReturn = calculateDailyReturn(data)
                    DailyReturn(date, dailyReturn)
                }
                // Case 4b: Multiple products available - calculate weighted average
                dailyData.isNotEmpty() -> {
                    val weightedReturn = calculateWeightedReturn(dailyData, capitals)
                    DailyReturn(date, weightedReturn)
                }
                // No data available for this date
                else -> DailyReturn(date, BigDecimal.ZERO)
            }
        }
    }

    /**
     * Calculates daily return for a single product
     */
    private fun calculateDailyReturn(data: HistoricalData): BigDecimal {
        return ((data.close - data.open) / data.open)
            .setScale(6, RoundingMode.HALF_UP)
    }

    /**
     * Calculates weighted average return for multiple products
     */
    private fun calculateWeightedReturn(
        dailyData: List<Pair<String, HistoricalData>>,
        capitals: Map<String, BigDecimal>
    ): BigDecimal {
        val totalCapital = dailyData
            .sumOf { (productId, _) -> capitals[productId] ?: BigDecimal.ZERO }

        if (totalCapital == BigDecimal.ZERO) {
            return BigDecimal.ZERO
        }

        val weightedSum = dailyData.sumOf { (productId, data) ->
            val capital = capitals[productId] ?: BigDecimal.ZERO
            val dailyReturn = calculateDailyReturn(data)
            capital * dailyReturn
        }

        return (weightedSum / totalCapital)
            .setScale(6, RoundingMode.HALF_UP)
    }
    /*
    To use this calculator:
val calculator = ReturnCalculator()

// Example usage
val historicalData = mapOf(
    "product1" to listOf(HistoricalData(...)),
    "product2" to listOf(HistoricalData(...))
)

val capitals = mapOf(
    "product1" to BigDecimal("1000"),
    "product2" to BigDecimal("2000")
)

val dailyReturns = calculator.calculateDailyReturns(historicalData, capitals)
     */
}