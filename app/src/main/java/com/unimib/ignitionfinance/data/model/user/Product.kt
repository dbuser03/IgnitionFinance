package com.unimib.ignitionfinance.data.model.user

import com.unimib.ignitionfinance.data.model.StockData

data class Product(
    val isin: String,
    val ticker: String,
    val purchaseDate: String,
    val amount: String,
    val symbol: String,
    val averagePerformance: String,
    val shares: Double,
    val currency: String,
    val historicalData: Map<String, StockData>
)