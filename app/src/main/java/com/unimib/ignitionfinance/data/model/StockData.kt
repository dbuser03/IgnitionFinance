package com.unimib.ignitionfinance.data.model

import java.math.BigDecimal

data class StockData(
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val volume: Long,
    val percentageChange: BigDecimal
) {
    val changePercentage: BigDecimal
        get() = ((close - open) / open) * BigDecimal(100)
}
