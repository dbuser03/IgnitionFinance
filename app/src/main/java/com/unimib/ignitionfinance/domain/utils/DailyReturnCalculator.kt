package com.unimib.ignitionfinance.domain.utils

import com.unimib.ignitionfinance.data.remote.model.StockData
import com.unimib.ignitionfinance.data.remote.model.user.DailyReturn
import java.math.BigDecimal
import javax.inject.Inject


class DailyReturnCalculator @Inject constructor() {
    fun calculateDailyReturns(
        historicalDataList: List<Map<String, StockData>>,
        products: List<String>,
        productCapitals: Map<String, BigDecimal>
    ): List<DailyReturn> {
        val allDates = getAllUniqueDates(historicalDataList)
        val dailyReturns = mutableListOf<DailyReturn>()

        for (date in allDates) {
            val productsWithDataForDate = getProductsWithDataForDate(
                date,
                historicalDataList
            )

            val dailyReturn = when (productsWithDataForDate.size) {
                1 -> {
                    val productIndex = productsWithDataForDate.first()
                    val stockData = historicalDataList[productIndex][date]!!
                    DailyReturn(
                        date = date,
                        weightedReturn = calculatePercentageChange(stockData.open, stockData.close)
                    )
                }
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

        return dailyReturns.sortedBy { it.date }
    }

    private fun getAllUniqueDates(historicalDataList: List<Map<String, StockData>>): Set<String> {
        return historicalDataList.flatMap { it.keys }.toSet()
    }

    private fun getProductsWithDataForDate(
        date: String,
        historicalDataList: List<Map<String, StockData>>
    ): List<Int> {
        return historicalDataList.indices.filter { index ->
            historicalDataList[index].containsKey(date)
        }
    }

    private fun calculateWeightedReturn(
        date: String,
        productsWithData: List<Int>,
        historicalDataList: List<Map<String, StockData>>,
        products: List<String>,
        productCapitals: Map<String, BigDecimal>
    ): DailyReturn {
        var weightedSum = BigDecimal.ZERO
        var totalCapital = BigDecimal.ZERO

        for (productIndex in productsWithData) {
            val productName = products[productIndex]
            val stockData = historicalDataList[productIndex][date]!!
            val capital = productCapitals[productName] ?: BigDecimal.ZERO

            val percentageChange = calculatePercentageChange(stockData.open, stockData.close)
            weightedSum += percentageChange * capital
            totalCapital += capital
        }

        val weightedReturn = if (totalCapital > BigDecimal.ZERO) {
            weightedSum / totalCapital
        } else {
            BigDecimal.ZERO
        }

        return DailyReturn(date = date, weightedReturn = weightedReturn)
    }

    private fun calculatePercentageChange(open: BigDecimal, close: BigDecimal): BigDecimal {
        return if (open > BigDecimal.ZERO) {
            ((close - open) / open) * BigDecimal(100)
        } else {
            BigDecimal.ZERO
        }
    }
}