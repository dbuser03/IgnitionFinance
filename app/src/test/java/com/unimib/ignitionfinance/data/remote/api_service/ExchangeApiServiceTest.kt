package com.unimib.ignitionfinance.data.remote.api_service

import com.google.gson.Gson
import com.unimib.ignitionfinance.data.remote.api_response.inflation.DataSet
import com.unimib.ignitionfinance.data.remote.api_response.inflation.Header
import com.unimib.ignitionfinance.data.remote.api_response.inflation.Sender
import com.unimib.ignitionfinance.data.remote.api_response.inflation.SeriesData
import com.unimib.ignitionfinance.data.remote.api_response.inflation.Structure
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
import java.util.concurrent.TimeUnit
import kotlin.text.get

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
    fun `test getExchangeRate with Euro to Dollar exchange rate`() = runBlocking {
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

        val response = exchangeApiService.getExchangeRate(seriesKey = "D.USD.EUR.SP00.A")

        assertNotNull(response)
        assertEquals(200, response.code())

        val responseBody = response.body()!!

        assertEquals("4f7728af-b162-4c1c-b05a-f5640d4622ab", Header.id)
        assertEquals(false, Header.test)
        assertEquals("2024-11-25T09:08:57.093+01:00", Header.prepared)
        assertEquals("ECB", Sender.id)
        val dataSet = responseBody.dataSets[0]
        assertEquals("Replace", DataSet.action)
        assertEquals("2024-11-25T09:08:57.093+01:00", DataSet.validFrom)
        val seriesData = DataSet.series["0:0:0:0:0"]
        assertNotNull(seriesData)
        assertEquals(1, SeriesData.observations?.size)
        assertEquals("Exchange Rates", Structure.name)
    }

    @Test
    fun `test getExchangeRate with Euro to Swiss Franc exchange rate`() = runBlocking {
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

        val response = exchangeApiService.getExchangeRate(seriesKey = "D.CHF.EUR.SP00.A")

        assertNotNull(response)
        assertEquals(200, response.code())

        val responseBody = response.body()!!

        assertEquals("4f7728af-b162-4c1c-b05a-f5640d4622ab", Header.id)
        assertEquals(false, Header.test)
        assertEquals("2024-11-25T09:08:57.093+01:00", Header.prepared)
        assertEquals("ECB", Sender.id)
        val dataSet = responseBody.dataSets[0]
        assertEquals("Replace", DataSet.action)
        assertEquals("2024-11-25T09:08:57.093+01:00", DataSet.validFrom)
        val seriesData = DataSet.series["EXR.D.CHF.EUR.SP00.A"]
        assertNotNull(seriesData)
        assertEquals(1, SeriesData.observations?.size)
        assertEquals("Exchange Rates", Structure.name)
    }

    // Additional test cases for real API calls
    @Test
    fun `test getExchangeRate real Euro to Dollar exchange rate`() = runBlocking {
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

        val exchangeApiService = retrofit.create(ExchangeApiService::class.java)

        val response = exchangeApiService.getExchangeRate(seriesKey = "D.USD.EUR.SP00.A")

        assertEquals(200, response.code())

        val responseBody = response.body()
        assertNotNull(responseBody)
        println("Response JSON (Euro to Dollar): ${Gson().toJson(responseBody)}")
    }

    @Test
    fun `test getExchangeRate real Euro to Swiss Franc exchange rate`() = runBlocking {
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

        val exchangeApiService = retrofit.create(ExchangeApiService::class.java)

        val response = exchangeApiService.getExchangeRate(seriesKey = "D.CHF.EUR.SP00.A")

        assertEquals(200, response.code())

        val responseBody = response.body()
        assertNotNull(responseBody)
        println("Response JSON (Euro to Swiss Franc): ${Gson().toJson(responseBody)}")
    }
}