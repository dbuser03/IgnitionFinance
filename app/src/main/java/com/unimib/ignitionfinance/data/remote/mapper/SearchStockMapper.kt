package com.unimib.ignitionfinance.data.remote.mapper

import com.unimib.ignitionfinance.data.remote.response.SearchStockResponse
import com.unimib.ignitionfinance.data.model.SearchStockData

class SearchStockMapper {

    fun mapToDomain(response: SearchStockResponse): List<SearchStockData> {
        return response.bestMatches.map { symbolMatch ->
            SearchStockData(
                symbol = symbolMatch.symbol,
                currency = symbolMatch.currency,
                matchScore = symbolMatch.matchScore
            )
        }
    }
}
