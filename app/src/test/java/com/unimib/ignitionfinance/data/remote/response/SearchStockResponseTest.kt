package com.unimib.ignitionfinance.data.remote.response

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchStockResponseTest {

    private val gson = Gson()

    @Test
    fun `test deserialization of SearchStockResponse`() {

        val jsonResponse = """
        {
            "bestMatches": [
                {
                    "1. symbol": "AAPL",
                    "2. name": "Apple Inc.",
                    "3. type": "Equity",
                    "4. region": "US",
                    "5. marketOpen": "09:30",
                    "6. marketClose": "16:00",
                    "7. timezone": "US/Eastern",
                    "8. currency": "USD",
                    "9. matchScore": "0.99"
                },
                {
                    "1. symbol": "GOOG",
                    "2. name": "Alphabet Inc.",
                    "3. type": "Equity",
                    "4. region": "US",
                    "5. marketOpen": "09:30",
                    "6. marketClose": "16:00",
                    "7. timezone": "US/Eastern",
                    "8. currency": "USD",
                    "9. matchScore": "0.95"
                }
            ]
        }
        """.trimIndent()

        val searchStockResponse = gson.fromJson(jsonResponse, SearchStockResponse::class.java)

        assertEquals(2, searchStockResponse.bestMatches.size)

        val firstMatch = searchStockResponse.bestMatches[0]
        assertEquals("AAPL", firstMatch.symbol)
        assertEquals("Apple Inc.", firstMatch.name)
        assertEquals("Equity", firstMatch.type)
        assertEquals("US", firstMatch.region)
        assertEquals("09:30", firstMatch.marketOpen)
        assertEquals("16:00", firstMatch.marketClose)
        assertEquals("US/Eastern", firstMatch.timezone)
        assertEquals("USD", firstMatch.currency)
        assertEquals("0.99", firstMatch.matchScore)

        val secondMatch = searchStockResponse.bestMatches[1]
        assertEquals("GOOG", secondMatch.symbol)
        assertEquals("Alphabet Inc.", secondMatch.name)
        assertEquals("Equity", secondMatch.type)
        assertEquals("US", secondMatch.region)
        assertEquals("09:30", secondMatch.marketOpen)
        assertEquals("16:00", secondMatch.marketClose)
        assertEquals("US/Eastern", secondMatch.timezone)
        assertEquals("USD", secondMatch.currency)
        assertEquals("0.95", secondMatch.matchScore)
    }
}
