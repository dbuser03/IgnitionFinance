package com.unimib.ignitionfinance.data.model.user

data class Product(
    val isin: String,
    val ticker: String, // identificativo
    val purchaseDate: String,
    val amount: String,// how much you invested
    val symbol: String,
// val averagePerformance: String
)
