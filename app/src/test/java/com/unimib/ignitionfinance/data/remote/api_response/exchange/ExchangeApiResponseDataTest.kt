package com.unimib.ignitionfinance.data.remote.api_response.exchange

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ExchangeApiResponseDataTest {

    private val gson = Gson()

    @Test
    fun `test deserialization of ExchangeApiResponseData`() {
        val jsonResponse = """
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
                            "attributes": [
                                0,
                                null,
                                0,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                0,
                                null,
                                0,
                                null,
                                0,
                                0,
                                0,
                                0
                            ],
                            "observations": {
                                "0": [
                                    1.0412,
                                    0,
                                    0,
                                    null,
                                    null
                                ]
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
                        }
                    ],
                    "observation": [
                        {
                            "id": "TIME_PERIOD",
                            "name": "Time period or range",
                            "role": "time",
                            "values": [
                                {
                                    "id": "2024-11-22",
                                    "name": "2024-11-22"
                                }
                            ]
                        }
                    ]
                }
            }
        }
        """.trimIndent()

        // Deserialize the JSON into the Kotlin data class
        val response = gson.fromJson(jsonResponse, ExchangeApiResponseData::class.java)

        // Verify the header object
        assertNotNull(response.header)
        assertEquals("a5a97a39-281d-4513-978b-a91dad969502", response.header.id)
        assertEquals(false, response.header.test)
        assertEquals("2024-11-24T12:09:44.123+01:00", response.header.prepared)
        assertEquals("ECB", response.header.sender.id)

        // Verify dataSets
        val dataSet = response.dataSets.firstOrNull()
        assertNotNull(dataSet)
        assertEquals("Replace", dataSet?.action)
        assertEquals("2024-11-24T12:09:44.123+01:00", dataSet?.validFrom)

        // Verify series
        val series = dataSet?.series?.get("0:0:0:0:0")
        assertNotNull(series)
        assertEquals(20, series?.attributes?.size)
        assertEquals(1.0412, series?.observations?.get("0")?.get(0))

        // Verify structure details
        assertNotNull(response.structure)
        assertEquals("Exchange Rates", response.structure.name)

        // Verify dimensions
        val freqDimension = response.structure.dimensions.series.firstOrNull()
        assertNotNull(freqDimension)
        assertEquals("FREQ", freqDimension?.id)
        assertEquals("Frequency", freqDimension?.name)
        assertEquals("D", freqDimension?.values?.firstOrNull()?.id)
        assertEquals("Daily", freqDimension?.values?.firstOrNull()?.name)

        // Verify observation dimensions
       val timeDimension = response.structure.observation.firstOrNull()
        assertNotNull(timeDimension)
        assertEquals("TIME_PERIOD", timeDimension?.id)
        assertEquals("Time period or range", timeDimension?.name)
        assertEquals("2024-11-22", timeDimension?.values?.firstOrNull()?.id)
    }
}
