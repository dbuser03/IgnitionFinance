package com.unimib.ignitionfinance.data.remote.mapper

import com.unimib.ignitionfinance.data.remote.response.SearchStockResponse
import com.unimib.ignitionfinance.data.remote.model.SearchStockData
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchStockMapperTest {

    @Test
    fun `test mapToDomain should correctly map SearchStockResponse to SearchStockData`() {
        val searchStockResponse = SearchStockResponse(
            bestMatches = listOf(
                com.unimib.ignitionfinance.data.remote.response.SymbolMatch(
                    symbol = "AAPL",
                    name = "Apple Inc.",
                    type = "Equity",
                    region = "US",
                    marketOpen = "09:30",
                    marketClose = "16:00",
                    timezone = "US/Eastern",
                    currency = "USD",
                    matchScore = "0.99"
                ),
                com.unimib.ignitionfinance.data.remote.response.SymbolMatch(
                    symbol = "GOOG",
                    name = "Alphabet Inc.",
                    type = "Equity",
                    region = "US",
                    marketOpen = "09:30",
                    marketClose = "16:00",
                    timezone = "US/Eastern",
                    currency = "USD",
                    matchScore = "0.95"
                )
            )
        )

        val result = SearchStockMapper().mapToDomain(searchStockResponse)

        val expected = listOf(
            SearchStockData(
                symbol = "AAPL",
                currency = "USD",
                matchScore = "0.99"
            ),
            SearchStockData(
                symbol = "GOOG",
                currency = "USD",
                matchScore = "0.95"
            )
        )

        assertEquals(expected, result)
    }
}
