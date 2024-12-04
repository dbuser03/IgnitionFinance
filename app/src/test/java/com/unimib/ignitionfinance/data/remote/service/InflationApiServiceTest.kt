package com.unimib.ignitionfinance.data.remote.service

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

class InflationApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var inflationService: InflationService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        inflationService = retrofit.create(InflationService::class.java)
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
            "id": "c6c01a61-e8c1-4b79-8266-23c8238e038f",
            "test": false,
            "prepared": "2024-11-22T22:45:53.238+01:00",
            "sender": {
                "id": "ECB"
            }
        },
        "dataSets": [
            {
                "action": "Replace",
                "validFrom": "2024-11-22T22:45:53.238+01:00",
                "series": {
                    "0:0:0:0:0:0": {
                        "attributes": [0.0, null, 0.0, null, null, null, 0.0, null, null, null, null, null, null, 0.0, null, 0.0, 0.0, 0.0, 0.0],
                        "observations": {
                            "0": [0.8, 0.0, 0.0, null, null],
                            "1": [1.6, 0.0, 0.0, null, null],
                            "2": [2.9, 0.0, 0.0, null, null]
                        }
                    }
                }
            }
        ],
        "structure": {
            "links": [
                {
                    "title": "Indices of Consumer prices",
                    "rel": "dataflow",
                    "href": "http://data-api.ecb.europa.eu:80/service/dataflow/ECB/ICP/1.0"
                }
            ],
            "name": "Indices of Consumer prices",
            "dimensions": {
                "series": [
                    {
                        "id": "FREQ",
                        "name": "Frequency",
                        "values": [
                            {"id": "A", "name": "Annual"}
                        ]
                    }
                ]
            },
            "observation": []
        }
    }
    """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setBody(mockResponse)
                .setResponseCode(200)
        )

        // Call the API with the symbol and no API key
        val response = inflationService.getInflationData()

        assertNotNull(response.body())
        assertEquals(200, response.code())

        val responseBody = response.body()!!

        // Verify header values
        assertEquals("c6c01a61-e8c1-4b79-8266-23c8238e038f", responseBody.header.id)
        assertEquals(false, responseBody.header.test)
        assertEquals("2024-11-22T22:45:53.238+01:00", responseBody.header.prepared)
        assertEquals("ECB", responseBody.header.sender.id)

        // Verify dataSets values
        val dataSet = responseBody.dataSets[0]
        assertEquals("Replace", dataSet.action)
        assertEquals("2024-11-22T22:45:53.238+01:00", dataSet.validFrom)

        val seriesData = dataSet.series["0:0:0:0:0:0"]
        assertNotNull(seriesData)
        assertEquals(3, seriesData?.observations?.size)

        // Verify observations
        val firstObservation = seriesData?.observations?.get("0")
        assertEquals(0.8, firstObservation?.get(0))

        // Verify structure name and dimensions
        assertEquals("Indices of Consumer prices", responseBody.structure.name)

        val dimension = responseBody.structure.dimensions.series[0]
        assertEquals("FREQ", dimension.id)
        assertEquals("Frequency", dimension.name)
        assertEquals("A", dimension.values[0].id)
        assertEquals("Annual", dimension.values[0].name)
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

        // Create the API service
        val inflationService = retrofit.create(InflationService::class.java)

        // Make the real API call
        val response = inflationService.getInflationData()

        // Verify that the response code is 200 (OK)
        assertEquals(200, response.code())

        // Check that the response body is not null and print the JSON
        val responseBody = response.body()
        assertNotNull(responseBody)

        // Print the JSON response for debugging/inspection
        println("Response JSON: ${Gson().toJson(responseBody)}")
    }


}

