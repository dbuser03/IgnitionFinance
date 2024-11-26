package com.unimib.ignitionfinance.data.repository

import com.unimib.ignitionfinance.data.remote.stock_api.StockApiResponseData
import com.unimib.ignitionfinance.data.remote.stock_api.StockApiService
import retrofit2.Response
import java.math.BigDecimal

class StockRepository(private val stockApiService: StockApiService) {

    // Metodo per recuperare i dati
    suspend fun fetchStockData(symbol: String, apiKey: String): Result<Map<String, StockData>> {
        // Effettua la chiamata all'API
        val response = stockApiService.getStockData(symbol = symbol, apiKey = apiKey)

        // Verifica se la risposta è valida e contiene i dati
        if (response.isSuccessful) {
            val stockData = response.body()

            // Se i dati sono validi, processali
            if (stockData != null) {
                return Result.success(processStockData(stockData))
            }
        }

        // Se la risposta è fallita, restituisci un errore
        return Result.failure(Throwable("Failed to fetch stock data"))
    }

    // Metodo che pulisce e trasforma i dati in una mappa più semplice
    private fun processStockData(stockData: StockApiResponseData): Map<String, StockData> {
        val processedData = mutableMapOf<String, StockData>()

        // Itera sulle date e raccoglie i dati di apertura, chiusura, massimo, minimo e volume
        stockData.timeSeries.forEach { (date, timeSeriesData) ->
            val stockDataForDay = StockData(
                open = timeSeriesData.open,
                high = timeSeriesData.high,
                low = timeSeriesData.low,
                close = timeSeriesData.close,
                volume = timeSeriesData.volume
            )
            processedData[date] = stockDataForDay
        }

        return processedData
    }
}

// Data class per rappresentare i dati di azioni di un giorno
data class StockData(
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val volume: Long
)
