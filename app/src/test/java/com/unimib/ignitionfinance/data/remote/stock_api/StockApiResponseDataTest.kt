package com.unimib.ignitionfinance.data.remote.stock_api

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class StockApiResponseDataTest {

    private val gson = Gson()

    @Test
    fun `test deserialization of StockApiResponseData with BigDecimal and Long`() {

        val jsonResponse = """
        {
            "Meta Data": {
                "1. Information": "Daily Prices (open, high, low, close) and Volumes",
                "2. Symbol": "IBM",
                "3. Last Refreshed": "2024-11-22",
                "4. Output Size": "Compact",
                "5. Time Zone": "US/Eastern"
            },
            "Time Series (Daily)": {
                "2024-11-22": {
                    "1. open": "223.3500",
                    "2. high": "227.2000",
                    "3. low": "220.8900",
                    "4. close": "222.9700",
                    "5. volume": "5320740"
                }
            }
        }
        """.trimIndent()

        val stockApiResponse = gson.fromJson(jsonResponse, StockApiResponseData::class.java)

        assertEquals("Daily Prices (open, high, low, close) and Volumes", stockApiResponse.metaData.information)
        assertEquals("IBM", stockApiResponse.metaData.symbol)
        assertEquals("2024-11-22", stockApiResponse.metaData.lastRefreshed)
        assertEquals("Compact", stockApiResponse.metaData.outputSize)
        assertEquals("US/Eastern", stockApiResponse.metaData.timeZone)

        val dailyData = stockApiResponse.timeSeries["2024-11-22"]
        assertEquals(BigDecimal("223.3500"), dailyData?.open)
        assertEquals(BigDecimal("227.2000"), dailyData?.high)
        assertEquals(BigDecimal("220.8900"), dailyData?.low)
        assertEquals(BigDecimal("222.9700"), dailyData?.close)
        assertEquals(5320740L, dailyData?.volume)
    }
}
