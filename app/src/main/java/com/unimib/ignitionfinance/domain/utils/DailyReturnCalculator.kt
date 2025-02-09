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

        val totalAmount = products.sumOf {
            BigDecimal(it.amount.ifEmpty { "0" })
        }
        if (totalAmount == BigDecimal.ZERO) return calculateSimpleReturns(historicalData.first())

        val commonDates = historicalData.map { it.keys }
            .reduce { acc, dates -> acc.intersect(dates) }
            .sorted()

        val weightedPrices = commonDates.associateWith { date ->
            var weightedPrice = BigDecimal.ZERO
            products.forEachIndexed { index, product ->
                val productData = historicalData.getOrNull(index) ?: return@forEachIndexed
                val closePrice = BigDecimal(productData[date]?.close?.toString() ?: return@forEachIndexed)
                val weight = BigDecimal(product.amount.ifEmpty { "0" }).divide(totalAmount, 10, RoundingMode.HALF_UP)
                weightedPrice = weightedPrice.plus(closePrice.multiply(weight))
            }
            weightedPrice
        }

        for (i in days until commonDates.size) {
            val pastDate = commonDates[i - days]
            val currentDate = commonDates[i]

            val pastWeightedPrice = weightedPrices[pastDate] ?: continue
            val currentWeightedPrice = weightedPrices[currentDate] ?: continue

            val return253Days = if (pastWeightedPrice != BigDecimal.ZERO) {
                currentWeightedPrice.minus(pastWeightedPrice)
                    .divide(pastWeightedPrice, 10, RoundingMode.HALF_UP)
            } else {
                BigDecimal.ZERO
            }

            dailyReturns.add(
                DailyReturn(
                    date = currentDate,
                    weightedReturn = return253Days
                )
            )
        }

        Log.d("DailyReturnCalculator", "${dailyReturns.reversed()}")
        return dailyReturns
    }

    private fun calculateSimpleReturns(data: Map<String, StockData>): List<DailyReturn> {
        val dailyReturns = mutableListOf<DailyReturn>()
        val sortedDates = data.keys.sorted()

        for (i in days until sortedDates.size) {
            val pastDate = sortedDates[i - days]
            val currentDate = sortedDates[i]

            val pastClose = BigDecimal(data[pastDate]?.close?.toString() ?: continue)
            val currentClose = BigDecimal(data[currentDate]?.close?.toString() ?: continue)

            val annualReturn = if (pastClose != BigDecimal.ZERO) {
                currentClose.minus(pastClose)
                    .divide(pastClose, 10, RoundingMode.HALF_UP)
            } else {
                BigDecimal.ZERO
            }

            dailyReturns.add(
                DailyReturn(
                    date = currentDate,
                    weightedReturn = annualReturn
                )
            )
        }

        return dailyReturns
    }
}