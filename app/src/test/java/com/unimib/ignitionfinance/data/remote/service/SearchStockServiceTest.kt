package com.unimib.ignitionfinance.data.remote.service

import com.google.gson.Gson
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

class SearchStockServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var searchStockService: SearchStockService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        searchStockService = retrofit.create(SearchStockService::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test getSearchStockData API call with mock response`() = runBlocking {
        val mockResponse = """
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

        mockWebServer.enqueue(
            MockResponse()
                .setBody(mockResponse)
                .setResponseCode(200)
        )

        val response = searchStockService.getSearchStockData(symbol = "Apple", apiKey = "demo")

        assertNotNull(response.body())
        assertEquals(200, response.code())

        val responseBody = response.body()!!
        assertEquals(2, responseBody.bestMatches.size)

        val firstMatch = responseBody.bestMatches[0]
        assertEquals("AAPL", firstMatch.symbol)
        assertEquals("Apple Inc.", firstMatch.name)
        assertEquals("Equity", firstMatch.type)
        assertEquals("US", firstMatch.region)
        assertEquals("09:30", firstMatch.marketOpen)
        assertEquals("16:00", firstMatch.marketClose)
        assertEquals("US/Eastern", firstMatch.timezone)
        assertEquals("USD", firstMatch.currency)
        assertEquals("0.99", firstMatch.matchScore)

        val secondMatch = responseBody.bestMatches[1]
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

    @Test
    fun `test getSearchStockData API call response is JSON with status 200`() = runBlocking {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.alphavantage.co/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val searchStockService = retrofit.create(SearchStockService::class.java)
        val realApiKey = BuildConfig.ALPHAVANTAGE_API_KEY

        // Make the real API call
        val response = searchStockService.getSearchStockData(symbol = "Apple", apiKey = realApiKey)

        // Verify that the response code is 200 (OK)
        assertEquals(200, response.code())

        // Check that the response body is not null and print the JSON
        val responseBody = response.body()
        assertNotNull(responseBody)

        // Print the JSON response for debugging/inspection
        println("Response JSON: ${Gson().toJson(responseBody)}")
    }
}
