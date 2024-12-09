package com.unimib.ignitionfinance.data.model

import java.math.BigDecimal

data class SearchStockData(
    val symbol: String,
    val currency: String,
    val matchScore: String
)
