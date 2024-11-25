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
   /* private val baseUrl = "https://data-api.ecb.europa.eu/"

    private fun createRetrofitClient(): Retrofit {
// Configurazione client OkHttp con logging
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor) // Aggiungi l'interceptor per il logging
            .connectTimeout(60, TimeUnit.SECONDS) // Timeout per la connessione
            .readTimeout(30, TimeUnit.SECONDS)    // Timeout per la lettura della risposta
            .writeTimeout(30, TimeUnit.SECONDS)   // Timeout per la scrittura della richiesta
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Test
    fun `test getDailyEuroToDollarExchangeRate response is valid`() = runBlocking {
        val retrofit = createRetrofitClient()
        val exchangeApiService = retrofit.create(ExchangeApiService::class.java)

        // Chiamata API per Euro -> Dollaro
        val response = exchangeApiService.getDailyEuroToDollarExchangeRate("EXR.D.USD.EUR.SP00.A", "JSONDATA")

        // Verifica che il codice di stato sia 200 (OK)
        assertEquals(200, response.code())

        // Verifica che il corpo della risposta non sia nullo
        val responseBody = response.body()
        assertNotNull("La risposta non deve essere nulla", responseBody)

        // Log della risposta JSON per debug
        println("Risposta JSON (Euro -> Dollaro): ${Gson().toJson(responseBody)}")
    }
}
@Test
fun `test getDailyEuroToSwissFrancExchangeRate response is valid`() = runBlocking {
    val retrofit = createRetrofitClient()
    val exchangeApiService = retrofit.create(ExchangeApiService::class.java)

    // Chiamata API per Euro -> Franco Svizzero
    val response = exchangeApiService.getDailyEuroToSwissFrancExchangeRate("EXR.D.CHF.EUR.SP00.A", "JSONDATA")

    // Verifica che il codice di stato sia 200 (OK)
    assertEquals(200, response.code())

    // Verifica che il corpo della risposta non sia nullo
    val responseBody = response.body()
    assertNotNull("La risposta non deve essere nulla", responseBody)

    // Log della risposta JSON per debug
    println("Risposta JSON (Euro -> Franco Svizzero): ${Gson().toJson(responseBody)}")
}
}
*/

}
