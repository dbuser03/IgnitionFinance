package com.unimib.ignitionfinance.data.model

import java.math.BigDecimal

data class HistoricalData(
    val date: String,
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val volume: Long
)
