package com.unimib.ignitionfinance.data.remote.inflation_api


import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Test

class InflationApiResponseDataTest {
    private val gson = Gson()

    @Test
    fun `test deserialization of InflationApiResponseData `() {

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
                    },
                    {
                        "id": "REF_AREA",
                        "name": "Reference area",
                        "values": [
                            {
                                "id": "IT",
                                "name": "Italy"
                            }
                        ]
                    },
                    {
                        "id": "ADJUSTMENT",
                        "name": "Adjustment indicator",
                        "values": [
                            {
                                "id": "N",
                                "name": "Neither seasonally nor working day adjusted"
                            }
                        ]
                    },
                    {
                        "id": "ICP_ITEM",
                        "name": "Classification - ICP context",
                        "values": [
                            {
                                "id": "000000",
                                "name": "HICP - Overall index"
                            }
                        ]
                    },
                    {
                        "id": "STS_INSTITUTION",
                        "name": "Institution originating the data flow",
                        "values": [
                            {
                                "id": "4",
                                "name": "Eurostat"
                            }
                        ]
                    },
                    {
                        "id": "ICP_SUFFIX",
                        "name": "Series variation - ICP context",
                        "values": [
                            {
                                "id": "AVR",
                                "name": "Annual average rate of change"
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
                            },
                            {
                                "id": "2011",
                                "name": "2011",
                                "start": "2011-01-01T00:00:00.000+01:00",
                                "end": "2011-12-31T23:59:59.999+01:00"
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
                        "id": "DATA_COMP",
                        "name": "Underlying compilation",
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
                        "values": [
                            {
                                "name": "ICPT.A.VAL.HICP.RCH_A_AVG.IT.00.A"
                            }
                        ]
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
                                "id": "1",
                                "name": "One"
                            }
                        ]
                    },
                    {
                        "id": "SOURCE_AGENCY",
                        "name": "Source agency",
                        "values": []
                    },
                    {
                        "id": "TITLE",
                        "name": "Title",
                        "values": [
                            {
                                "name": "HICP - Overall index"
                            }
                        ]
                    },
                    {
                        "id": "TITLE_COMPL",
                        "name": "Title complement",
                        "values": [
                            {
                                "name": "Italy - HICP - Overall index, Annual average rate of change, 
                                Eurostat, Neither seasonally nor working day adjusted"
                            }
                        ]
                    },
                    {
                        "id": "UNIT",
                        "name": "Unit",
                        "values": [
                            {
                                "id": "PCCH",
                                "name": "Percentage change"
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
                                "id": "A",
                                "name": "Normal value"
                            }
                        ]
                    },
                    {
                        "id": "OBS_CONF",
                        "name": "Observation confidentiality",
                        "values": [
                            {
                                "id": "F",
                                "name": "Free"
                            }
                        ]
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
    """.trimIndent()

        // Deserialize the JSON into the Kotlin data class
        val response = gson.fromJson(jsonResponse, InflationApiResponseData::class.java)

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