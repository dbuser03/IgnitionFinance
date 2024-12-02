import com.google.gson.Gson
import com.unimib.ignitionfinance.data.remote.api_response.exchange.ExchangeApiResponseData
import org.junit.Assert.*
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

        // Verify header values
        assertEquals("a5a97a39-281d-4513-978b-a91dad969502", response.header.id)
        assertEquals(false, response.header.test)
        assertEquals("2024-11-24T12:09:44.123+01:00", response.header.prepared)
        assertEquals("ECB", response.header.sender.id)

        // Verify dataSets values
        val dataSet = response.dataSets[0]
        assertEquals("Replace", dataSet.action)
        assertEquals("2024-11-24T12:09:44.123+01:00", dataSet.validFrom)

        val seriesData = dataSet.series["0:0:0:0:0"]
        assertEquals(20, seriesData?.attributes?.size)
        assertEquals(1, seriesData?.observations?.size)

        // Verify attributes and observations
        val firstObservation = seriesData?.observations?.get("0")
        assertEquals(1.0412, firstObservation?.get(0))

        // Verify structure name and dimension values
        assertEquals("Exchange Rates", response.structure.name)

        val dimension = response.structure.dimensions.series[0]
        assertEquals("FREQ", dimension.id)
        assertEquals("Frequency", dimension.name)
        assertEquals("D", dimension.values[0].id)
        assertEquals("Daily", dimension.values[0].name)

        // Verify observation dimensions, ensure structure and dimensions are not null or empty
        val timeDimension = response.structure.observation?.firstOrNull()

        // Explicit null check for timeDimension
        assertNotNull(timeDimension)

        assertEquals("TIME_PERIOD", timeDimension?.id)
        assertEquals("Time period or range", timeDimension?.name)

        // Ensure values list is not null or empty before accessing first element
        assertTrue(timeDimension?.values?.isNotEmpty() == true)
        assertEquals("2024-11-22", timeDimension?.values?.firstOrNull()?.id)
    }
}
