package com.unimib.ignitionfinance.data.model.user

data class Product(
    val isin: String,
    val ticker: String,
    val purchaseDate: String,
    val amount: String,
    val symbol: String,
    val averagePerformance: String,
    val shares: Double,
    val currency: String
)
