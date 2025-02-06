package com.unimib.ignitionfinance.data.remote.model

data class SearchStockData(
    val symbol: String,
    val currency: String,
    val matchScore: String
)