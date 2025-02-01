package com.unimib.ignitionfinance.data.calculator

import com.unimib.ignitionfinance.data.model.StockData
import com.unimib.ignitionfinance.data.model.user.DailyReturn
import java.math.BigDecimal

class DailyReturnCalculator {
    // Main function that processes the list of historical data for all products
    fun calculateDailyReturns(
        historicalDataList: List<Map<String, StockData>>,
        products: List<String>,
        productCapitals: Map<String, BigDecimal>
    ): List<DailyReturn> {
        // First, collect all unique dates from all products' historical data
        val allDates = getAllUniqueDates(historicalDataList)
        val dailyReturns = mutableListOf<DailyReturn>()

        // For each date, we'll calculate the appropriate return
        for (date in allDates) {
            // Get all products that have data for this specific date
            val productsWithDataForDate = getProductsWithDataForDate(
                date,
                historicalDataList,
                products
            )

            // Calculate return based on number of available products
            val dailyReturn = when (productsWithDataForDate.size) {
                // If we have data for only one product
                1 -> {
                    val productIndex = productsWithDataForDate.first()
                    val stockData = historicalDataList[productIndex][date]!!
                    // Simply calculate the percentage change for this single product
                    DailyReturn(
                        dates = date,
                        weightedReturns = calculatePercentageChange(stockData.open, stockData.close)
                    )
                }
                // If we have data for multiple products
                else -> {
                    calculateWeightedReturn(
                        date,
                        productsWithDataForDate,
                        historicalDataList,
                        products,
                        productCapitals
                    )
                }
            }

            dailyReturns.add(dailyReturn)
        }

        // Return the list sorted by date
        return dailyReturns.sortedBy { it.dates }
    }

    // Helper function to get all unique dates from all historical data
    private fun getAllUniqueDates(historicalDataList: List<Map<String, StockData>>): Set<String> {
        return historicalDataList.flatMap { it.keys }.toSet()
    }

    // Helper function to find which products have data for a specific date
    private fun getProductsWithDataForDate(
        date: String,
        historicalDataList: List<Map<String, StockData>>,
        products: List<String>
    ): List<Int> {
        return historicalDataList.indices.filter { index ->
            historicalDataList[index].containsKey(date)
        }
    }

    // Function to calculate weighted return when we have multiple products
    private fun calculateWeightedReturn(
        date: String,
        productsWithData: List<Int>,
        historicalDataList: List<Map<String, StockData>>,
        products: List<String>,
        productCapitals: Map<String, BigDecimal>
    ): DailyReturn {
        var weightedSum = BigDecimal.ZERO
        var totalCapital = BigDecimal.ZERO

        // Calculate the weighted sum of percentage changes
        for (productIndex in productsWithData) {
            val productName = products[productIndex]
            val stockData = historicalDataList[productIndex][date]!!
            val capital = productCapitals[productName] ?: BigDecimal.ZERO

            // Calculate percentage change and multiply by capital
            val percentageChange = calculatePercentageChange(stockData.open, stockData.close)
            weightedSum += percentageChange * capital
            totalCapital += capital
        }

        // Calculate final weighted average
        val weightedReturn = if (totalCapital > BigDecimal.ZERO) {
            weightedSum / totalCapital
        } else {
            BigDecimal.ZERO
        }

        return DailyReturn(dates = date, weightedReturns = weightedReturn)
    }

    // Core function to calculate percentage change for a single product
    private fun calculatePercentageChange(open: BigDecimal, close: BigDecimal): BigDecimal {
        return if (open > BigDecimal.ZERO) {
            ((close - open) / open) * BigDecimal(100)
        } else {
            BigDecimal.ZERO
        }
    }
}