package com.unimib.ignitionfinance.data.remote.mapper

import android.util.Log
import com.unimib.ignitionfinance.data.remote.response.SearchStockResponse
import com.unimib.ignitionfinance.data.model.SearchStockData

object SearchStockMapper {

    private const val TAG = "SearchStockMapper"

    fun mapToDomain(response: SearchStockResponse): List<SearchStockData> {
        Log.d(TAG, "SearchStockResponse: $response")

        return response.bestMatches.map { match ->
            SearchStockData(
                symbol = match.symbol,
                currency = match.currency,
                matchScore = match.matchScore
            )
        }
    }
}
