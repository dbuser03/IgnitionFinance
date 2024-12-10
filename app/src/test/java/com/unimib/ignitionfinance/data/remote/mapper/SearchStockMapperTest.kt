package com.unimib.ignitionfinance.data.remote.mapper

import com.unimib.ignitionfinance.data.remote.response.SearchStockResponse
import com.unimib.ignitionfinance.data.model.SearchStockData
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchStockMapperTest {

    @Test
    fun `test mapToDomain should correctly map SearchStockResponse to SearchStockData`() {
        // Crea un'istanza di SearchStockResponse mockata con dei dati di esempio
        val searchStockResponse = SearchStockResponse(
            bestMatches = listOf(
                // Aggiungi i mock dei dati
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

        // Esegui il mapping usando il mapper
        val result = SearchStockMapper().mapToDomain(searchStockResponse)

        // Crea l'output atteso
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

        // Confronta il risultato effettivo con quello atteso
        assertEquals(expected, result)
    }
}
