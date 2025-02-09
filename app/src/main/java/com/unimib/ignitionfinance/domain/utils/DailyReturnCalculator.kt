package com.unimib.ignitionfinance.domain.utils

import android.util.Log
import com.unimib.ignitionfinance.data.remote.model.StockData
import com.unimib.ignitionfinance.data.remote.model.user.DailyReturn
import javax.inject.Inject

class DailyReturnCalculator @Inject constructor() {
    private val days = 253

    fun calculateDailyReturns(
        historicalData: List<Map<String, StockData>>,
    ): List<DailyReturn> {
        val dailyReturns = mutableListOf<DailyReturn>()

        for (data in historicalData) {
            val sortedDates = data.keys.sorted()
            for (i in days until sortedDates.size) {
                val pastDate = sortedDates[i - days]
                val currentDate = sortedDates[i]

                val pastClose = data[pastDate]?.close ?: continue
                val currentClose = data[currentDate]?.close ?: continue

                val annualReturn = (currentClose - pastClose) / pastClose

                dailyReturns.add(
                    DailyReturn(
                        date = currentDate,
                        weightedReturn = annualReturn
                    )
                )
            }
        }
        Log.d("DailyReturnCalculator", "${dailyReturns.reversed()}")
        return dailyReturns
    }
}