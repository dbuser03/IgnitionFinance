package com.unimib.ignitionfinance.data.remote.api_mapper

import com.unimib.ignitionfinance.domain.model.StockData
import com.unimib.ignitionfinance.data.remote.api_response.StockApiResponseData

object StockApiMapper {

    fun mapToDomain(stockApiResponseData: StockApiResponseData): Map<String, StockData> {
        val processedData = mutableMapOf<String, StockData>()

        stockApiResponseData.timeSeries.forEach { (date, timeSeriesData) ->
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
