package com.unimib.ignitionfinance.domain.utils

import android.util.Log
import com.unimib.ignitionfinance.data.remote.model.StockData
import com.unimib.ignitionfinance.data.remote.model.user.DailyReturn
import com.unimib.ignitionfinance.data.remote.model.user.Product
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

class DailyReturnCalculator @Inject constructor() {
    private val days = 253

    fun calculateDailyReturns(
        historicalData: List<Map<String, StockData>>,
        products: List<Product>? = null
    ): List<DailyReturn> {
        val dailyReturns = mutableListOf<DailyReturn>()

        if (products.isNullOrEmpty() || historicalData.size == 1) {
            return calculateSimpleReturns(historicalData.first())
        }

        val allDates: List<String> = historicalData.flatMap { it.keys }
            .distinct()
            .sorted()

        for (i in days until allDates.size) {
            val currentDate = allDates[i]
            val pastDate = allDates[i - days]

            val commonIndices = mutableListOf<Int>()
            for (index in historicalData.indices) {
                if (historicalData[index].containsKey(currentDate) && historicalData[index].containsKey(pastDate)) {
                    commonIndices.add(index)
                }
            }

            if (commonIndices.isEmpty()) {
                continue
            }

            var compositeCurrentSum = BigDecimal.ZERO
            var compositePastSum = BigDecimal.ZERO
            var totalWeightCurrent = BigDecimal.ZERO
            var totalWeightPast = BigDecimal.ZERO

            for (index in commonIndices) {
                val productWeight = BigDecimal(products[index].amount.ifEmpty { "0" })
                if (productWeight.compareTo(BigDecimal.ZERO) == 0) {
                    continue
                }

                val currentCloseData = historicalData[index][currentDate]?.close
                val pastCloseData = historicalData[index][pastDate]?.close
                if (currentCloseData == null || pastCloseData == null) {
                    continue
                }
                val currentClose = BigDecimal(currentCloseData.toString())
                val pastClose = BigDecimal(pastCloseData.toString())

                compositeCurrentSum = compositeCurrentSum.add(currentClose.multiply(productWeight))
                totalWeightCurrent = totalWeightCurrent.add(productWeight)

                compositePastSum = compositePastSum.add(pastClose.multiply(productWeight))
                totalWeightPast = totalWeightPast.add(productWeight)
            }

            if (totalWeightCurrent.compareTo(BigDecimal.ZERO) == 0 ||
                totalWeightPast.compareTo(BigDecimal.ZERO) == 0
            ) {
                continue
            }

            val compositeCurrent = compositeCurrentSum.divide(totalWeightCurrent, 10, RoundingMode.HALF_UP)
            val compositePast = compositePastSum.divide(totalWeightPast, 10, RoundingMode.HALF_UP)

            if (compositePast.compareTo(BigDecimal.ZERO) == 0) {
                continue
            }
            val returnValue = compositeCurrent.subtract(compositePast)
                .divide(compositePast, 10, RoundingMode.HALF_UP)

            dailyReturns.add(DailyReturn(date = currentDate, weightedReturn = returnValue))
        }

        dailyReturns.sortBy { it.date }
        return dailyReturns
    }

    private fun calculateSimpleReturns(data: Map<String, StockData>): List<DailyReturn> {
        val dailyReturns = mutableListOf<DailyReturn>()
        val sortedDates = data.keys.sorted()

        for (i in days until sortedDates.size) {
            val pastDate = sortedDates[i - days]
            val currentDate = sortedDates[i]

            val pastClose = data[pastDate]?.close?.toString()?.let { BigDecimal(it) } ?: continue
            val currentClose = data[currentDate]?.close?.toString()?.let { BigDecimal(it) } ?: continue

            if (pastClose.compareTo(BigDecimal.ZERO) == 0) {
                continue
            }

            val ret = currentClose.subtract(pastClose)
                .divide(pastClose, 10, RoundingMode.HALF_UP)
            dailyReturns.add(DailyReturn(date = currentDate, weightedReturn = ret))
        }
        return dailyReturns
    }
}
