package com.unimib.ignitionfinance.data.remote.inflation_api

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

class NewInflationApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var inflationApiService: InflationApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        inflationApiService = retrofit.create(InflationApiService::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test getInflationData API call with mock response`() = runBlocking {
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
                        "attributes": [0, null, 0, null, null, null, null, null, null, null, null, null, 0, null, 0, null, 0, 0, 0, 0],
                        "observations": {
                            "0": [1.0412, 0, 0, null, null]
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

        val response = inflationApiService.getInflationData()

        assertNotNull(response.body())
        assertEquals(200, response.code())

        val responseBody = response.body()!!

        // Header assertions
        assertEquals("a5a97a39-281d-4513-978b-a91dad969502", responseBody.header.id)
        assertEquals(false, responseBody.header.test)
        assertEquals("2024-11-24T12:09:44.123+01:00", responseBody.header.prepared)
        assertEquals("ECB", responseBody.header.sender.id)

        // DataSets assertions
        val dataSet = responseBody.dataSets[0]
        assertEquals("Replace", dataSet.action)
        assertEquals("2024-11-24T12:09:44.123+01:00", dataSet.validFrom)

        val seriesData = dataSet.series["0:0:0:0:0"]
        assertNotNull(seriesData)
        assertEquals(1, seriesData?.observations?.size)

        val firstObservation = seriesData?.observations?.get("0")
        assertEquals(1.0412, firstObservation?.get(0))

        // Structure assertions
        assertEquals("Exchange Rates", responseBody.structure.name)

        val dimension = responseBody.structure.dimensions.series[0]
        assertEquals("FREQ", dimension.id)
        assertEquals("Frequency", dimension.name)
        assertEquals("D", dimension.values[0].id)
        assertEquals("Daily", dimension.values[0].name)
    }

    @Test
    fun `test getInflationData API call response is JSON with status 200`() = runBlocking {
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

        val inflationApiService = retrofit.create(InflationApiService::class.java)

        val response = inflationApiService.getInflationData()

        assertEquals(200, response.code())

        val responseBody = response.body()
        assertNotNull(responseBody)

        println("Response JSON: ${Gson().toJson(responseBody)}")
    }

}
