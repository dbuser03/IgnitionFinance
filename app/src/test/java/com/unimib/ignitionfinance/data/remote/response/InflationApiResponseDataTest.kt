package com.unimib.ignitionfinance.data.remote.response

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Test

class InflationApiResponseDataTest {
    private val gson = Gson()

    @Test
    fun `test deserialization of InflationResponse`() {

        val jsonResponse = """
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
                            "attributes": [
                                0,
                                null,
                                0,
                                null,
                                null,
                                null,
                                0,
                                null,
                                null,
                                null,
                                null,
                                null,
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
                                    0.8,
                                    0,
                                    0,
                                    null,
                                    null
                                ],
                                "1": [
                                    1.6,
                                    0,
                                    0,
                                    null,
                                    null
                                ],
                                "2": [
                                    2.9,
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
                                {
                                    "id": "A",
                                    "name": "Annual"
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
                                    "id": "2009",
                                    "name": "2009",
                                    "start": "2009-01-01T00:00:00.000+01:00",
                                    "end": "2009-12-31T23:59:59.999+01:00"
                                },
                                {
                                    "id": "2010",
                                    "name": "2010",
                                    "start": "2010-01-01T00:00:00.000+01:00",
                                    "end": "2010-12-31T23:59:59.999+01:00"
                                }
                            ]
                        }
                    ]
                },
                "attributes": {
                    "series": [
                        {
                            "id": "TIME_FORMAT",
                            "name": "Time format code",
                            "values": [
                                {
                                    "name": "P1Y"
                                }
                            ]
                        }
                    ]
                }
            }
        }
        """.trimIndent()

        // Deserialize the JSON into the Kotlin data class
        val response = gson.fromJson(jsonResponse, InflationResponse::class.java)

        // Verify the header values
        assertEquals("c6c01a61-e8c1-4b79-8266-23c8238e038f", response.header.id)
        assertEquals(false, response.header.test)
        assertEquals("2024-11-22T22:45:53.238+01:00", response.header.prepared)
        assertEquals("ECB", response.header.sender.id)

        // Verify dataSets values
        val dataSet = response.dataSets[0]
        assertEquals("Replace", dataSet.action)
        assertEquals("2024-11-22T22:45:53.238+01:00", dataSet.validFrom)

        val seriesData = dataSet.series["0:0:0:0:0:0"]
        assertEquals(3, seriesData?.observations?.size)

        // Verify attributes and observations
        val firstObservation = seriesData?.observations?.get("0")
        assertEquals(0.8, firstObservation?.get(0))

        // Verify structure name and dimension values
        assertEquals("Indices of Consumer prices", response.structure.name)

        val dimension = response.structure.dimensions.series[0]
        assertEquals("FREQ", dimension.id)
        assertEquals("Frequency", dimension.name)
        assertEquals("A", dimension.values[0].id)
        assertEquals("Annual", dimension.values[0].name)
    }
}
