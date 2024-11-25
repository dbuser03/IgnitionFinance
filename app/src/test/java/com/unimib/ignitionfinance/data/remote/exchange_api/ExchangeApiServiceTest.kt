package com.unimib.ignitionfinance.data.remote.exchange_api

import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import java.util.concurrent.TimeUnit


class ExchangeApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var exchangeApiService: ExchangeApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        exchangeApiService = retrofit.create(ExchangeApiService::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test getDailyEuroToDollarExchangeRate API call with mock response`() = runBlocking {
        val mockResponse = """
        {
          "header": {
            "id": "4f7728af-b162-4c1c-b05a-f5640d4622ab",
            "test": false,
            "prepared": "2024-11-25T09:08:57.093+01:00",
            "sender": {
              "id": "ECB"
            }
          },
          "dataSets": [
            {
              "action": "Replace",
              "validFrom": "2024-11-25T09:08:57.093+01:00",
              "series": {
                "0:0:0:0:0": {
                  "attributes": [
                    0, null, 0, null, null, null, null, null, null, null, null, null, 0, null, 0, null, 0, 0, 0, 0
                  ],
                  "observations": {
                    "0": [null, 0, null, null, null]
                  }
                }
              }
            }
          ],
          "structure": {
            "links": [
              {
                "title": "Exchange Rates",
                "rel": "dataflow",
                "href": "http://data-api.ecb.europa.eu:80/service/dataflow/ECB/EXR/1.0"
              }
            ],
            "name": "Exchange Rates",
            "dimensions": {
              "series": [
                {
                  "id": "FREQ",
                  "name": "Frequency",
                  "values": [
                    {
                      "id": "D",
                      "name": "Daily"
                    }
                  ]
                },
                {
                  "id": "CURRENCY",
                  "name": "Currency",
                  "values": [
                    {
                      "id": "USD",
                      "name": "US dollar"
                    }
                  ]
                }
              ]
            }
          }
        }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setBody(mockResponse)
                .setResponseCode(200)
        )

        val response = exchangeApiService.getDailyEuroToDollarExchangeRate()

        assertNotNull(response)
        assertEquals(200, response.code())

        val responseBody = response.body()!!

        assertEquals("4f7728af-b162-4c1c-b05a-f5640d4622ab", responseBody.header.id)
        assertEquals(false, responseBody.header.test)
        assertEquals("2024-11-25T09:08:57.093+01:00", responseBody.header.prepared)
        assertEquals("ECB", responseBody.header.sender.id)
        val dataSet = responseBody.dataSets[0]
        assertEquals("Replace", dataSet.action)
        assertEquals("2024-11-25T09:08:57.093+01:00", dataSet.validFrom)
        val seriesData = dataSet.series["0:0:0:0:0"]
        assertNotNull(seriesData)
        assertEquals(1, seriesData?.observations?.size)
        assertEquals("Exchange Rates", responseBody.structure.name)
    }

    @Test
    fun `test getDailyEuroToSwissFrancExchangeRate API call with mock response`() = runBlocking {
        val mockResponse = """
        {
          "header": {
            "id": "4f7728af-b162-4c1c-b05a-f5640d4622ab",
            "test": false,
            "prepared": "2024-11-25T09:08:57.093+01:00",
            "sender": {
              "id": "ECB"
            }
          },
          "dataSets": [
            {
              "action": "Replace",
              "validFrom": "2024-11-25T09:08:57.093+01:00",
              "series": {
                "EXR.D.CHF.EUR.SP00.A": {
                  "attributes": [
                    0, null, 0, null, null, null, null, null, null, null, null, null, 0, null, 0, null, 0, 0, 0, 0
                  ],
                  "observations": {
                    "0": [null, 0, null, null, null]
                  }
                }
              }
            }
          ],
          "structure": {
            "links": [
              {
                "title": "Exchange Rates",
                "rel": "dataflow",
                "href": "http://data-api.ecb.europa.eu:80/service/dataflow/ECB/EXR/1.0"
              }
            ],
            "name": "Exchange Rates",
            "dimensions": {
              "series": [
                {
                  "id": "FREQ",
                  "name": "Frequency",
                  "values": [
                    {
                      "id": "D",
                      "name": "Daily"
                    }
                  ]
                },
                {
                  "id": "CURRENCY",
                  "name": "Currency",
                  "values": [
                    {
                      "id": "CHF",
                      "name": "Swiss Franc"
                    }
                  ]
                },
                {
                  "id": "CURRENCY_DENOM",
                  "name": "Currency denominator",
                  "values": [
                    {
                      "id": "EUR",
                      "name": "Euro"
                    }
                  ]
                }
              ]
            }
          }
        }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setBody(mockResponse)
                .setResponseCode(200)
        )

        val response = exchangeApiService.getDailyEuroToSwissFrancExchangeRate()

        assertNotNull(response)
        assertEquals(200, response.code())

        val responseBody = response.body()!!

        assertEquals("4f7728af-b162-4c1c-b05a-f5640d4622ab", responseBody.header.id)
        assertEquals(false, responseBody.header.test)
        assertEquals("2024-11-25T09:08:57.093+01:00", responseBody.header.prepared)
        assertEquals("ECB", responseBody.header.sender.id)
        val dataSet = responseBody.dataSets[0]
        assertEquals("Replace", dataSet.action)
        assertEquals("2024-11-25T09:08:57.093+01:00", dataSet.validFrom)
        val seriesData = dataSet.series["EXR.D.CHF.EUR.SP00.A"]
        assertNotNull(seriesData)
        assertEquals(1, seriesData?.observations?.size)
        assertEquals("Exchange Rates", responseBody.structure.name)
    }
    @Test
    fun `test getDailyEuroToDollarExchangeRate API call response is JSON with status 200`() = runBlocking {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://data-api.ecb.europa.eu/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create the API service
        val exchangeApiService = retrofit.create(ExchangeApiService::class.java)

        // Make the real API call for Euro-to-Dollar exchange rate
        val response = exchangeApiService.getDailyEuroToDollarExchangeRate("EXR.D.USD.EUR.SP00.A", "JSONDATA")

        // Verify that the response code is 200 (OK)
        assertEquals(200, response.code())

        // Check that the response body is not null and print the JSON
        val responseBody = response.body()
        assertNotNull(responseBody)

        // Print the JSON response for debugging/inspection
        println("Response JSON (Euro to Dollar): ${Gson().toJson(responseBody)}")
    }

    @Test
    fun `test getDailyEuroToSwissFrancExchangeRate API call response is JSON with status 200`() = runBlocking {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://data-api.ecb.europa.eu/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create the API service
        val exchangeApiService = retrofit.create(ExchangeApiService::class.java)

        // Make the real API call for Euro-to-Swiss-Franc exchange rate
        val response = exchangeApiService.getDailyEuroToSwissFrancExchangeRate("EXR.D.CHF.EUR.SP00.A", "JSONDATA")

        // Verify that the response code is 200 (OK)
        assertEquals(200, response.code())

        // Check that the response body is not null and print the JSON
        val responseBody = response.body()
        assertNotNull(responseBody)

        // Print the JSON response for debugging/inspection
        println("Response JSON (Euro to Swiss Franc): ${Gson().toJson(responseBody)}")
    }

}
