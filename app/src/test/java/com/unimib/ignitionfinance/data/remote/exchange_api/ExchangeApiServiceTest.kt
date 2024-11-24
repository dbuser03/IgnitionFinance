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
    fun test getDailyEuroToDollarExchangeRate() = runBlocking {
        val mockResponse = """
            {
                "header": {
                    "id": "a5a97a39-281d-4513-978b-a91dad969502",
                    "test": false,
                    "prepared": "2024-11-24T12:09:44.123+01:00",
                    "sender": {
                        "id": "ECB"
                    }
                },
                "dataSets": [
                    {
                        "action": "Replace",
                        "validFrom": "2024-11-24T12:09:44.123+01:00",
                        "series": {
                            "0:0:0:0:0": {
                                "observations": {
                                    "0": [1.0412, 0, 0, null, null]
                                }
                            }
                        }
                    }
                ],
                "structure": {
                    "name": "Exchange Rates",
                    "dimensions": {
                        "series": [
                            {
                                "id": "FREQ",
                                "name": "Frequency",
                                "values": [
                                    {"id": "D", "name": "Daily"}
                                ]
                            },
                            {
                                "id": "CURRENCY",
                                "name": "Currency",
                                "values": [
                                    {"id": "USD", "name": "US dollar"}
                                ]
                            },
                            {
                                "id": "CURRENCY_DENOM",
                                "name": "Currency denominator",
                                "values": [
                                    {"id": "EUR", "name": "Euro"}
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

        val responseBody = response.body()
        assertNotNull(responseBody)

        // Validate header details
        assertEquals("a5a97a39-281d-4513-978b-a91dad969502", responseBody?.header?.id)
        assertEquals(false, responseBody?.header?.test)
        assertEquals("2024-11-24T12:09:44.123+01:00", responseBody?.header?.prepared)
        assertEquals("ECB", responseBody?.header?.sender?.id)

        // Validate dataSets values
        val dataSet = responseBody?.dataSets?.get(0)
        assertEquals("Replace", dataSet?.action)
        assertEquals("2024-11-24T12:09:44.123+01:00", dataSet?.validFrom)

        // Validate observations in the response
        val seriesData = dataSet?.series?.get("0:0:0:0:0")
        assertNotNull(seriesData)
        assertEquals(1, seriesData?.observations?.size)

        val firstObservation = seriesData?.observations?.get("0")
        assertNotNull(firstObservation)
        assertEquals(1.0412, firstObservation?.get(0))

        // Validate structure and dimensions
        assertEquals("Exchange Rates", responseBody?.structure?.name)

        val dimensions = responseBody?.structure?.dimensions?.series
        assertNotNull(dimensions)
        assertEquals(3, dimensions?.size)

        // Validate frequency dimension
        val frequency = dimensions?.find { it.id == "FREQ" }
        assertNotNull(frequency)
        assertEquals("D", frequency?.values?.get(0)?.id)
        assertEquals("Daily", frequency?.values?.get(0)?.name)

        // Validate currency dimension
        val currency = dimensions?.find { it.id == "CURRENCY" }
        assertNotNull(currency)
        assertEquals("USD", currency?.values?.get(0)?.id)
        assertEquals("US dollar", currency?.values?.get(0)?.name)

        // Validate currency denominator dimension
        val currencyDenom = dimensions?.find { it.id == "CURRENCY_DENOM" }
        assertNotNull(currencyDenom)
        assertEquals("EUR", currencyDenom?.values?.get(0)?.id)
        assertEquals("Euro", currencyDenom?.values?.get(0)?.name)
    }

    @Test
    fun test getDailyEuroToSwissFrancExchangeRate() = runBlocking {
        val mockResponse = """
            {
                "header": {
                    "id": "a5a97a39-281d-4513-978b-a91dad969502",
                    "test": false,
                    "prepared": "2024-11-24T12:09:44.123+01:00",
                    "sender": {
                        "id": "ECB"
                    }
                },
                "dataSets": [
                    {
                        "action": "Replace",
                        "validFrom": "2024-11-24T12:09:44.123+01:00",
                        "series": {
                            "0:0:0:0:0": {
                                "observations": {
                                    "0": [0.95, 0, 0, null, null]
                                }
                            }
                        }
                    }
                ],
                "structure": {
                    "name": "Exchange Rates",
                    "dimensions": {
                        "series": [
                            {
                                "id": "FREQ",
                                "name": "Frequency",
                                "values": [
                                    {"id": "D", "name": "Daily"}
                                ]
                            },
                            {
                                "id": "CURRENCY",
                                "name": "Currency",
                                "values": [
                                    {"id": "CHF", "name": "Swiss Franc"}
                                ]
                            },
                            {
                                "id": "CURRENCY_DENOM",
                                "name": "Currency denominator",
                                "values": [
                                    {"id": "EUR", "name": "Euro"}
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

        val responseBody = response.body()
        assertNotNull(responseBody)

        // Validate header details
        assertEquals("a5a97a39-281d-4513-978b-a91dad969502", responseBody?.header?.id)
        assertEquals(false, responseBody?.header?.test)
        assertEquals("2024-11-24T12:09:44.123+01:00", responseBody?.header?.prepared)
        assertEquals("ECB", responseBody?.header?.sender?.id)

        // Validate dataSets values
        val dataSet = responseBody?.dataSets?.get(0)
        assertEquals("Replace", dataSet?.action)
        assertEquals("2024-11-24T12:09:44.123+01:00", dataSet?.validFrom)

        // Validate observations in the response
        val seriesData = dataSet?.series?.get("0:0:0:0:0")
        assertNotNull(seriesData)
        assertEquals(1, seriesData?.observations?.size)

        val firstObservation = seriesData?.observations?.get("0")
        assertNotNull(firstObservation)
        assertEquals(0.95, firstObservation?.get(0))

        // Validate structure and dimensions
        assertEquals("Exchange Rates", responseBody?.structure?.name)

        val dimensions = responseBody?.structure?.dimensions?.series
        assertNotNull(dimensions)
        assertEquals(3, dimensions?.size)

        // Validate frequency dimension
        val frequency = dimensions?.find { it.id == "FREQ" }
        assertNotNull(frequency)
        assertEquals("D", frequency?.values?.get(0)?.id)
        assertEquals("Daily", frequency?.values?.get(0)?.name)

        // Validate currency dimension
        val currency = dimensions?.find { it.id == "CURRENCY" }
        assertNotNull(currency)
        assertEquals("CHF", currency?.values?.get(0)?.id)
        assertEquals("Swiss Franc", currency?.values?.get(0)?.name)

        // Validate currency denominator dimension
        val currencyDenom = dimensions?.find { it.id == "CURRENCY_DENOM" }
        assertNotNull(currencyDenom)
        assertEquals("EUR", currencyDenom?.values?.get(0)?.id)
        assertEquals("Euro", currencyDenom?.values?.get(0)?.name)
    }
}
