package com.unimib.ignitionfinance.data.remote.mapper

import com.unimib.ignitionfinance.data.remote.response.SearchStockResponse
import com.unimib.ignitionfinance.data.model.SearchStockData

class SearchStockMapper {

    fun mapToDomain(response: SearchStockResponse): List<SearchStockData> {
        return response.bestMatches.map { symbolMatch ->
            SearchStockData(
                symbol = symbolMatch.symbol,
                name = symbolMatch.name,
                type = symbolMatch.type,
                region = symbolMatch.region,
                marketOpen = symbolMatch.marketOpen,
                marketClose = symbolMatch.marketClose,
                timezone = symbolMatch.timezone,
                currency = symbolMatch.currency,
                matchScore = symbolMatch.matchScore
            )
        }
    }
}
