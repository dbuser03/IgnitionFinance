package com.unimib.ignitionfinance.data.remote.mapper

import com.unimib.ignitionfinance.data.remote.model.InflationData
import com.unimib.ignitionfinance.data.remote.response.InflationResponse
import com.unimib.ignitionfinance.data.remote.response.InflationDataSet
import com.unimib.ignitionfinance.data.remote.response.InflationSeries
import org.junit.Assert.assertEquals
import org.junit.Test

class InflationMapperTest {

    @Test
    fun `test mapToDomain should map InflationResponse to InflationData correctly`() {
        val inflationResponse = InflationResponse(
            header = mockHeader(),
            dataSets = listOf(
                InflationDataSet(
                    action = "action",
                    validFrom = "2024-01-01",
                    series = mapOf(
                        "series1" to InflationSeries(
                            attributes = emptyList(),
                            observations = mapOf(
                                "2024-01-01" to listOf("3.0")
                            )
                        )
                    )
                )
            ),
            structure = mockStructure()
        )

        val result = InflationMapper.mapToDomain(inflationResponse)

        val expected = listOf(
            InflationData(year = "2024-01-01", inflationRate = 3.0)
        )
        assertEquals(expected, result)
    }

    private fun mockHeader() = com.unimib.ignitionfinance.data.remote.response.InflationHeader(
        id = "1",
        test = true,
        prepared = "2024-01-01",
        sender = com.unimib.ignitionfinance.data.remote.response.InflationSender(id = "senderId")
    )

    private fun mockStructure() = com.unimib.ignitionfinance.data.remote.response.InflationStructure(
        links = emptyList(),
        name = "structureName",
        dimensions = com.unimib.ignitionfinance.data.remote.response.InflationDimensions(
            series = emptyList(),
            observation = emptyList()
        ),
        attributes = com.unimib.ignitionfinance.data.remote.response.InflationAttributes(
            series = emptyList(),
            observation = emptyList()
        )
    )
}
