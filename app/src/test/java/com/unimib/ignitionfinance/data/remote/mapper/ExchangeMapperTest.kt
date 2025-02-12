package com.unimib.ignitionfinance.data.remote.mapper

import com.unimib.ignitionfinance.data.remote.model.api.ExchangeData
import com.unimib.ignitionfinance.data.remote.response.ExchangeResponse
import com.unimib.ignitionfinance.data.remote.response.ExchangeDataSet
import com.unimib.ignitionfinance.data.remote.response.ExchangeSeries
import org.junit.Assert.assertEquals
import org.junit.Test

class ExchangeMapperTest {

    @Test
    fun `test mapToDomain should map response to domain model correctly`() {
        val exchangeResponse = ExchangeResponse(
            header = mockHeader(),
            dataSets = listOf(
                ExchangeDataSet(
                    action = "action",
                    validFrom = "2024-01-01",
                    series = mapOf(
                        "series1" to ExchangeSeries(
                            attributes = emptyList(),
                            observations = mapOf(
                                "2024-01-01" to listOf("2.5")
                            )
                        )
                    )
                )
            ),
            structure = mockStructure()
        )

        val result = ExchangeMapper.mapToDomain(exchangeResponse)

        val expected = listOf(
            ExchangeData(date = "2024-01-01", inflationRate = 2.5)
        )
        assertEquals(expected, result)
    }

    private fun mockHeader() = com.unimib.ignitionfinance.data.remote.response.ExchangeHeader(
        id = "1",
        test = true,
        prepared = "2024-01-01",
        sender = com.unimib.ignitionfinance.data.remote.response.ExchangeSender(id = "senderId")
    )

    private fun mockStructure() = com.unimib.ignitionfinance.data.remote.response.ExchangeStructure(
        links = emptyList(),
        name = "structureName",
        dimensions = com.unimib.ignitionfinance.data.remote.response.ExchangeDimensions(
            series = emptyList(),
            observation = emptyList()
        )
    )
}
