package com.unimib.ignitionfinance.data.calculator

import com.unimib.ignitionfinance.data.model.StockData
import com.unimib.ignitionfinance.data.model.user.DailyReturn
import java.math.BigDecimal
import java.util.*

class DailyReturnCalculator {

    // Funzione principale che calcola i daily returns
    fun calculateDailyReturns(productsData: Map<String, Map<String, StockData>>, productCapitals: Map<String, BigDecimal>): List<DailyReturn> {
        val dates = getAllDates(productsData)
        val dailyReturns = mutableListOf<DailyReturn>()

        for (date in dates) {
            val dailyReturn = calculateDailyReturnForDate(date, productsData, productCapitals)
            dailyReturns.add(dailyReturn)
        }

        return dailyReturns
    }

    // Funzione per ottenere tutte le date comuni tra i vari prodotti
    private fun getAllDates(productsData: Map<String, Map<String, StockData>>): Set<String> {
        val allDates = mutableSetOf<String>()
        for (productData in productsData.values) {
            allDates.addAll(productData.keys)
        }
        return allDates
    }

    // Funzione per calcolare il rendimento giornaliero per una data specifica
    private fun calculateDailyReturnForDate(date: String, productsData: Map<String, Map<String, StockData>>, productCapitals: Map<String, BigDecimal>): DailyReturn {
        var weightedReturnSum = BigDecimal.ZERO
        var capitalSum = BigDecimal.ZERO

        // Calcolare il rendimento pesato per ogni prodotto
        for ((product, data) in productsData) {
            val stockData = data[date]
            stockData?.let {
                val dailyReturn = it.percentageChange
                val capital = productCapitals[product] ?: BigDecimal.ZERO
                weightedReturnSum += dailyReturn * capital
                capitalSum += capital
            }
        }

        // Calcolare il rendimento giornaliero pesato
        val weightedReturn = if (capitalSum > BigDecimal.ZERO) {
            weightedReturnSum / capitalSum
        } else {
            BigDecimal.ZERO
        }

        return DailyReturn(date, weightedReturn)
    }

    // Funzione per mappare i dati dall'API (esempio di JSON)
    fun mapApiResponseToStockData(response: Map<String, Map<String, Any>>): Map<String, StockData> {
        val stockDataMap = mutableMapOf<String, StockData>()

        for ((date, data) in response) {
            val open = (data["open"] as String).toBigDecimal()
            val close = (data["close"] as String).toBigDecimal()
            val volume = (data["volume"] as String).toLong()

            // Calcolare la variazione percentuale
            val percentageChange = calculatePercentageChange(open, close)

            stockDataMap[date] = StockData(
                open = open,
                high = (data["high"] as String).toBigDecimal(),
                low = (data["low"] as String).toBigDecimal(),
                close = close,
                volume = volume,
                percentageChange = percentageChange
            )
        }

        return stockDataMap
    }

    // Funzione per calcolare la variazione percentuale
    private fun calculatePercentageChange(open: BigDecimal, close: BigDecimal): BigDecimal {
        return if (open > BigDecimal.ZERO) {
            ((close - open) / open) * BigDecimal(100)
        } else {
            BigDecimal.ZERO
        }
    }
}
