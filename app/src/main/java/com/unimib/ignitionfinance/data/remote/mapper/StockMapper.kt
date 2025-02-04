package com.unimib.ignitionfinance.data.remote.mapper

import com.unimib.ignitionfinance.data.remote.response.StockResponse
import com.unimib.ignitionfinance.data.model.StockData
import java.math.BigDecimal

object StockMapper {
    fun mapToDomain(response: StockResponse): Map<String, StockData> {
        // Controlla che timeSeries non sia null; altrimenti restituisci una mappa vuota
        val timeSeries = response.timeSeries ?: return emptyMap()

        return timeSeries.mapValues { (dateString, timeSeriesData) ->
            StockData(
                open = timeSeriesData.open,
                high = timeSeriesData.high,
                low = timeSeriesData.low,
                close = timeSeriesData.close,
                volume = timeSeriesData.volume,
                percentageChange = calculatePercentageChange(timeSeriesData.open, timeSeriesData.close)
            )
        }
    }

    private fun calculatePercentageChange(open: BigDecimal, close: BigDecimal): BigDecimal {
        return if (open > BigDecimal.ZERO) {
            ((close - open) / open) * BigDecimal(100)
        } else {
            BigDecimal.ZERO
        }
    }
}
