package com.unimib.ignitionfinance.data.remote.datasetTest

import com.unimib.ignitionfinance.data.calculator.DailyReturnCalculator
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal

class DailyReturnCalculatorTest {

    @Test
    fun testCalculateDailyReturns() {
        // Simulazione della risposta dell'API per due prodotti (IBM e AAPL)
        val apiResponse = mapOf(
            "IBM" to mapOf(
                "2025-01-21" to mapOf(
                    "open" to "224.9900",
                    "high" to "227.4500",
                    "low" to "222.8302",
                    "close" to "224.2600",
                    "volume" to "3982203"
                ),
                "2025-01-17" to mapOf(
                    "open" to "225.9550",
                    "high" to "225.9550",
                    "low" to "223.6400",
                    "close" to "224.7900",
                    "volume" to "5506837"
                )
            ),
            "AAPL" to mapOf(
                "2025-01-21" to mapOf(
                    "open" to "145.0000",
                    "high" to "147.0000",
                    "low" to "144.0000",
                    "close" to "146.5000",
                    "volume" to "10000000"
                ),
                "2025-01-20" to mapOf(
                    "open" to "142.5000",
                    "high" to "144.5000",
                    "low" to "141.5000",
                    "close" to "143.5000",
                    "volume" to "8000000"
                )
            )
        )

        // Creazione dell'oggetto DailyReturnCalculator
        val calculator = DailyReturnCalculator()

        // Mappatura della risposta API a StockData per ciascun prodotto
        val productsData = apiResponse.mapValues { (product, dailyData) ->
            calculator.mapApiResponseToStockData(dailyData)
        }

        // Simulazione dei capitali per ciascun prodotto
        val productCapitals = mapOf(
            "IBM" to BigDecimal("1000000.00"),  // 1 milione per IBM
            "AAPL" to BigDecimal("500000.00")   // 500k per AAPL
        )

        // Calcolare i daily returns
        val dailyReturns = calculator.calculateDailyReturns(productsData, productCapitals)

        // Stampa del dataset ottenuto
        println("Dataset ottenuto:")
        dailyReturns.forEach { dailyReturn ->
            println("Data: ${dailyReturn.dates}, Weighted Return: ${dailyReturn.weightedReturns}")
        }

        // Margine di errore per i confronti
        val tolerance = BigDecimal("0.006")  // Tolleranza di ±0.001

        // Funzione di supporto per verificare con tolleranza
        fun assertWithTolerance(expected: BigDecimal, actual: BigDecimal) {
            val difference = expected.subtract(actual).abs()
            assertTrue(
                "Valore atteso $expected, ottenuto $actual, differenza $difference supera la tolleranza $tolerance",
                difference <= tolerance
            )
        }

        // Verifica dei risultati
        assertEquals(3, dailyReturns.size)  // Ci aspettiamo 3 date

        // Verifica per il 2025-01-21
        val dailyReturn2025_01_21 = dailyReturns.find { it.dates == "2025-01-21" }
        assertNotNull(dailyReturn2025_01_21)
        assertWithTolerance(
            BigDecimal("0.130"),  // Valore atteso
            dailyReturn2025_01_21?.weightedReturns ?: BigDecimal.ZERO
        )

        // Verifica per il 2025-01-17
        val dailyReturn2025_01_17 = dailyReturns.find { it.dates == "2025-01-17" }
        assertNotNull(dailyReturn2025_01_17)
        assertWithTolerance(
            BigDecimal("-0.515"),  // Valore atteso
            dailyReturn2025_01_17?.weightedReturns ?: BigDecimal.ZERO
        )

        // Verifica per il 2025-01-20 (solo AAPL)
        val dailyReturn2025_01_20 = dailyReturns.find { it.dates == "2025-01-20" }
        assertNotNull(dailyReturn2025_01_20)
        assertWithTolerance(
            BigDecimal("0.702"),  // Valore atteso
            dailyReturn2025_01_20?.weightedReturns ?: BigDecimal.ZERO
        )
    }
}
