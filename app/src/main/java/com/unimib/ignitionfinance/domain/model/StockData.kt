package com.unimib.ignitionfinance.domain.model

import java.math.BigDecimal

data class StockData(
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val volume: Long
)
