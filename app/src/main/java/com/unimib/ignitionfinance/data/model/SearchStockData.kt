package com.unimib.ignitionfinance.data.model

import java.math.BigDecimal

data class SearchStockData(
    val symbol: String,
    val name: String,
    val type: String,
    val region: String,
    val marketOpen: String,
    val marketClose: String,
    val timezone: String,
    val currency: String,
    val matchScore: String
) {
    val matchPercentage: BigDecimal
        get() = calculateMatchPercentage()

    private fun calculateMatchPercentage(): BigDecimal {
        return try {
            val score = BigDecimal(matchScore)
            score * BigDecimal(100)
        } catch (e: Exception) {
            BigDecimal.ZERO
        }
    }
}
