package com.unimib.ignitionfinance.domain.utils

import com.unimib.ignitionfinance.data.model.user.DailyReturn

class ReturnCalculator {
    fun calculateWeightedReturns(historicalData: Map<String, List<Map<String, Any>>>): DailyReturn {
        val weightedReturns = mutableListOf<Map<String, Any>>()

        // Assuming historicalData is a map where the key is the product ID and the value is a list of daily data points
        val dates = historicalData.values.flatten().map { it["date"] as String }.distinct()

        for (date in dates) {
            val dailyData = historicalData.values.mapNotNull { productData ->
                productData.find { it["date"] == date }
            }

            if (dailyData.size == 1) {
                weightedReturns.add(dailyData.first())
            } else if (dailyData.isNotEmpty()) {
                val totalCapital = dailyData.sumOf { it["capital"] as Double }
                val weightedReturn = dailyData.sumOf { (it["capital"] as Double) * (it["return"] as Double) } / totalCapital

                weightedReturns.add(mapOf("date" to date, "return" to weightedReturn))
            }
        }

        return DailyReturn(weightedReturns)
    }
}
