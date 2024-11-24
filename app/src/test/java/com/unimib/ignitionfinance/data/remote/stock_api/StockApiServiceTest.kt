package com.unimib.ignitionfinance.data.remote.stock_api

import com.google.gson.Gson
import com.google.gson.internal.GsonBuildConfig
import com.unimib.ignitionfinance.BuildConfig
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import java.math.BigDecimal

class StockApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var stockApiService: StockApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        stockApiService = retrofit.create(StockApiService::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test getStockData API call with mock response`() = runBlocking {
        val mockResponse = """
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

        mockWebServer.enqueue(
            MockResponse()
                .setBody(mockResponse)
                .setResponseCode(200)
        )

        val response = stockApiService.getStockData(symbol = "IBM", apiKey = "demo")

        assertNotNull(response.body())
        assertEquals(200, response.code())

        val responseBody = response.body()!!
        assertEquals("Daily Prices (open, high, low, close) and Volumes", responseBody.metaData.information)
        assertEquals("IBM", responseBody.metaData.symbol)
        assertEquals("2024-11-22", responseBody.metaData.lastRefreshed)

        val dailyData = responseBody.timeSeries["2024-11-22"]
        assertNotNull(dailyData)
        assertEquals(BigDecimal("223.3500"), dailyData?.open)
        assertEquals(BigDecimal("227.2000"), dailyData?.high)
        assertEquals(BigDecimal("220.8900"), dailyData?.low)
        assertEquals(BigDecimal("222.9700"), dailyData?.close)
        assertEquals(5320740L, dailyData?.volume)
    }

    @Test
    fun `test getStockData API call response is JSON with status 200`() = runBlocking {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.alphavantage.co/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val stockApiService = retrofit.create(StockApiService::class.java)
        val realApiKey = BuildConfig.ALPHAVANTAGE_API_KEY

        // Make the real API call
        val response = stockApiService.getStockData(symbol = "IBM", apiKey = realApiKey)

        // Verify that the response code is 200 (OK)
        assertEquals(200, response.code())

        // Check that the response body is not null and print the JSON
        val responseBody = response.body()
        assertNotNull(responseBody)

        // Print the JSON response for debugging/inspection
        println("Response JSON: ${Gson().toJson(responseBody)}")
    }

}
