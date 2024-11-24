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
            "id": "mock-id-1234",
            "prepared": "2024-11-22T22:45:53.238+01:00",
            "sender": {
                "id": "ECB"
            }
        },
        "data": {
            "items": [
                {"value": 2.5, "date": "2024-11-01"},
                {"value": 3.1, "date": "2024-10-01"}
            ]
        },
        "info": {
            "name": "Inflation Data",
            "description": "Monthly inflation rates"
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

        // Verifica dei campi della struttura JSON mock
        assertEquals("mock-id-1234", responseBody.header.id)
        assertEquals("ECB", responseBody.header.sender.id)

        val items = responseBody.data.items
        assertNotNull(items)
        assertEquals(2, items.size)
        assertEquals(2.5, items[0].value)
        assertEquals("2024-11-01", items[0].date)

        assertEquals("Inflation Data", responseBody.info.name)
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
