package com.unimib.ignitionfinance.data.remote.api_response.exchange

import com.google.gson.Gson
import com.unimib.ignitionfinance.data.remote.api_response.inflation.DataSet
import com.unimib.ignitionfinance.data.remote.api_response.inflation.Dimension
import com.unimib.ignitionfinance.data.remote.api_response.inflation.Dimensions
import com.unimib.ignitionfinance.data.remote.api_response.inflation.Header
import com.unimib.ignitionfinance.data.remote.api_response.inflation.Sender
import com.unimib.ignitionfinance.data.remote.api_response.inflation.SeriesData
import com.unimib.ignitionfinance.data.remote.api_response.inflation.Structure
import com.unimib.ignitionfinance.data.remote.api_response.inflation.Value
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.collections.get
import kotlin.text.get

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
                                    "id": "2024-11-22",
                                    "name": "2024-11-22",
                                    "start": "2024-11-22T00:00:00.000+01:00",
                                    "end": "2024-11-22T23:59:59.999+01:00"
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
                val response = gson.fromJson(jsonResponse, ExchangeApiResponseData::class.java)

                // Verify header values
                assertEquals("a5a97a39-281d-4513-978b-a91dad969502", Header.id)
                assertEquals(false, Header.test)
                assertEquals("2024-11-24T12:09:44.123+01:00", Header.prepared)
                assertEquals("ECB", Sender.id)

                // Verify dataSets values
                val dataSet = response.dataSets[0]
                assertEquals("Replace", DataSet.action)
                assertEquals("2024-11-24T12:09:44.123+01:00", DataSet.validFrom)

                val seriesData = DataSet.series["0:0:0:0:0"]
                assertEquals(20, SeriesData.attributes?.size)
                assertEquals(1, SeriesData.observations?.size)

                // Verify attributes and observations
                val firstObservation = SeriesData.observations?.get("0")
            assertEquals(1.0412, firstObservation?.get(0))

                // Verify structure name and dimension values
                assertEquals("Exchange Rates", Structure.name)

                val dimension = Dimensions.series[0]
                assertEquals("FREQ", Dimension.id)
                assertEquals("Frequency", Dimension.name)
                assertEquals("D", Value.id)
                assertEquals("Daily", Value.name)

                val observationDimension = Dimensions.observation[0]
                assertEquals("TIME_PERIOD", ObservationDimension.id)
                assertEquals("Time period or range", ObservationDimension.name)
                assertEquals("time", ObservationDimension.role)
                assertEquals("2024-11-22", ObservationValue.id)

                val attribute = Attributes.series[0]
                assertEquals("TIME_FORMAT", Attribute.id)
                assertEquals("Time format code", Attribute.name)
                assertEquals("P1D", Value.name)
        }
}
