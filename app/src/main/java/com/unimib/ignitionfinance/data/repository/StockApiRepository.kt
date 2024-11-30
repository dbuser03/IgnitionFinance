package com.unimib.ignitionfinance.data.repository

import com.unimib.ignitionfinance.data.remote.response.StockApiResponseData
import com.unimib.ignitionfinance.data.remote.service.StockApiService
import java.math.BigDecimal

class StockRepository(private val stockApiService: StockApiService) {

    suspend fun fetchStockData(symbol: String, apiKey: String): Result<Map<String, StockData>> {
        val response = stockApiService.getStockData(symbol = symbol, apiKey = apiKey)

        if (response.isSuccessful) {
            val stockData = response.body()

            if (stockData != null) {
                return Result.success(processStockData(stockData))
            }
        }

        return Result.failure(Throwable("Failed to fetch stock data"))
    }

    private fun processStockData(stockData: StockApiResponseData): Map<String, StockData> {
        val processedData = mutableMapOf<String, StockData>()

        stockData.timeSeries.forEach { (date, timeSeriesData) ->
            val stockDataForDay = StockData(
                open = timeSeriesData.open,
                high = timeSeriesData.high,
                low = timeSeriesData.low,
                close = timeSeriesData.close,
                volume = timeSeriesData.volume
            )
            processedData[date] = stockDataForDay
        }

        return processedData
    }
}

data class StockData(
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val volume: Long
)
