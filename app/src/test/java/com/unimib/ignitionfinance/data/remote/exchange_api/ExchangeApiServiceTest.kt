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
            },
            {
              "id": "EXR_TYPE",
              "name": "Exchange rate type",
              "values": [
                {
                  "id": "SP00",
                  "name": "Spot"
                }
              ]
            },
            {
              "id": "EXR_SUFFIX",
              "name": "Series variation - EXR context",
              "values": [
                {
                  "id": "A",
                  "name": "Average"
                }
              ]
            }
          ],
          "observation": [
            {
              "id": "TIME_PERIOD",
              "name": "Time period or range",
              "role": "time",
              "values": [
                {
                  "id": "2009-05-01",
                  "name": "2009-05-01",
                  "start": "2009-05-01T00:00:00.000+02:00",
                  "end": "2009-05-01T23:59:59.999+02:00"
                }
              ]
            }
          ],
          "attributes": {
            "series": [
              {
                "id": "TIME_FORMAT",
                "name": "Time format code",
                "values": [
                  {
                    "name": "P1D"
                  }
                ]
              },
              {
                "id": "BREAKS",
                "name": "Breaks",
                "values": []
              },
              {
                "id": "COLLECTION",
                "name": "Collection indicator",
                "values": [
                  {
                    "id": "A",
                    "name": "Average of observations through period"
                  }
                ]
              },
              {
                "id": "COMPILING_ORG",
                "name": "Compiling organisation",
                "values": []
              },
              {
                "id": "DISS_ORG",
                "name": "Data dissemination organisation",
                "values": []
              },
              {
                "id": "DOM_SER_IDS",
                "name": "Domestic series ids",
                "values": []
              },
              {
                "id": "PUBL_ECB",
                "name": "Source publication (ECB only)",
                "values": []
              },
              {
                "id": "PUBL_MU",
                "name": "Source publication (Euro area only)",
                "values": []
              },
              {
                "id": "PUBL_PUBLIC",
                "name": "Source publication (public)",
                "values": []
              },
              {
                "id": "UNIT_INDEX_BASE",
                "name": "Unit index base",
                "values": []
              },
              {
                "id": "COMPILATION",
                "name": "Compilation",
                "values": []
              },
              {
                "id": "COVERAGE",
                "name": "Coverage",
                "values": []
              },
              {
                "id": "DECIMALS",
                "name": "Decimals",
                "values": [
                  {
                    "id": "4",
                    "name": "Four"
                  }
                ]
              },
              {
                "id": "NAT_TITLE",
                "name": "National language title",
                "values": []
              },
              {
                "id": "SOURCE_AGENCY",
                "name": "Source agency",
                "values": [
                  {
                    "id": "4F0",
                    "name": "European Central Bank (ECB)"
                  }
                ]
              },
              {
                "id": "SOURCE_PUB",
                "name": "Publication source",
                "values": []
              },
              {
                "id": "TITLE",
                "name": "Title",
                "values": [
                  {
                    "name": "US dollar/Euro"
                  }
                ]
              },
              {
                "id": "TITLE_COMPL",
                "name": "Title complement",
                "values": [
                  {
                    "name": "ECB reference exchange rate, US dollar/Euro, 2:15 pm (C.E.T.)"
                  }
                ]
              },
              {
                "id": "UNIT",
                "name": "Unit",
                "values": [
                  {
                    "id": "USD",
                    "name": "US dollar"
                  }
                ]
              },
              {
                "id": "UNIT_MULT",
                "name": "Unit multiplier",
                "values": [
                  {
                    "id": "0",
                    "name": "Units"
                  }
                ]
              }
            ],
            "observation": [
              {
                "id": "OBS_STATUS",
                "name": "Observation status",
                "values": [
                  {
                    "id": "H",
                    "name": "Missing value; holiday or weekend"
                  }
                ]
              },
              {
                "id": "OBS_CONF",
                "name": "Observation confidentiality",
                "values": []
              },
              {
                "id": "OBS_PRE_BREAK",
                "name": "Pre-break observation value",
                "values": []
              },
              {
                "id": "OBS_COM",
                "name": "Observation comment",
                "values": []
              }
            ]
          }
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

        // Header assertions
        assertEquals("4f7728af-b162-4c1c-b05a-f5640d4622ab", responseBody.header.id)
        assertEquals(false, responseBody.header.test)
        assertEquals("2024-11-25T09:08:57.093+01:00", responseBody.header.prepared)
        assertEquals("ECB", responseBody.header.sender.id)

        // DataSets assertions
        val dataSet = responseBody.dataSets[0]
        assertEquals("Replace", dataSet.action)
        assertEquals("2024-11-25T09:08:57.093+01:00", dataSet.validFrom)

        val seriesData = dataSet.series["0:0:0:0:0"]
        assertNotNull(seriesData)
        assertEquals(1, seriesData?.observations?.size)

        // Structure assertions
        assertEquals("Exchange Rates", responseBody.structure.name)

        val dimension = responseBody.structure.dimensions.series[0]
        assertEquals("FREQ", dimension.id)
        assertEquals("Frequency", dimension.name)
        assertEquals("D", dimension.values[0].id)
        assertEquals("Daily", dimension.values[0].name)
    }

    //@Test
    //fun `test getDailyEuroToSwissFrancExchangeRate API call response is JSON with status 200`() = runBlocking {
    //    val okHttpClient = OkHttpClient.Builder()
    //        .connectTimeout(60, TimeUnit.SECONDS)
    //        .readTimeout(30, TimeUnit.SECONDS)
    //        .writeTimeout(30, TimeUnit.SECONDS)
    //        .build()

    //    val retrofit = Retrofit.Builder()
    //        .baseUrl("https://data-api.ecb.europa.eu/")
    //        .client(okHttpClient)
    //        .addConverterFactory(GsonConverterFactory.create())
    //        .build()

    //    val exchangeApiService = retrofit.create(ExchangeApiService::class.java)

    //    val response = exchangeApiService.getDailyEuroToDollarExchangeRate()

    //    assertEquals(200, response.code())

    //    val responseBody = response.body()
    //    assertNotNull(responseBody)

    //    println("Response JSON: ${Gson().toJson(responseBody)}")
    //}
}
