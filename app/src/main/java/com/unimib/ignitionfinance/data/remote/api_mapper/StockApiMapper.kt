package com.unimib.ignitionfinance.data.remote.api_mapper

import com.unimib.ignitionfinance.data.remote.api_response.StockApiResponseData
import com.unimib.ignitionfinance.domain.model.StockData
import java.math.BigDecimal

class StockApiMapper {

    fun mapToDomain(response: StockApiResponseData): List<StockData> {
        return response.timeSeries.map { (dateString, timeSeriesData) ->
            StockData(
                date = dateString,
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
