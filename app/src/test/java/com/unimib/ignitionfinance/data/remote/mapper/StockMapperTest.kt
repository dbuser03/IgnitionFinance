package com.unimib.ignitionfinance.data.remote.mapper

import com.unimib.ignitionfinance.data.remote.model.StockData
import com.unimib.ignitionfinance.data.remote.response.StockResponse
import com.unimib.ignitionfinance.data.remote.response.TimeSeriesData
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class StockMapperTest {

    @Test
    fun `test mapToDomain should correctly map StockResponse to StockData and calculate percentage change`() {
        val stockResponse = StockResponse(
            metaData = mockMetaData(),
            timeSeries = mapOf(
                "2024-01-01" to TimeSeriesData(
                    open = BigDecimal("100.00"),
                    high = BigDecimal("105.00"),
                    low = BigDecimal("95.00"),
                    close = BigDecimal("102.00"),
                    volume = 1500000L
                ),
                "2024-01-02" to TimeSeriesData(
                    open = BigDecimal("102.00"),
                    high = BigDecimal("108.00"),
                    low = BigDecimal("100.00"),
                    close = BigDecimal("104.00"),
                    volume = 1600000L
                )
            )
        )

        val result = StockMapper().mapToDomain(stockResponse)

        val expected = mapOf(
            "2024-01-01" to StockData(
                open = BigDecimal("100.00"),
                high = BigDecimal("105.00"),
                low = BigDecimal("95.00"),
                close = BigDecimal("102.00"),
                volume = 1500000L,
                percentageChange = BigDecimal("2.00")
            ),
            "2024-01-02" to StockData(
                open = BigDecimal("102.00"),
                high = BigDecimal("108.00"),
                low = BigDecimal("100.00"),
                close = BigDecimal("104.00"),
                volume = 1600000L,
                percentageChange = BigDecimal("1.96")
            )
        )

        assertEquals(expected, result)
    }

    private fun mockMetaData() = com.unimib.ignitionfinance.data.remote.response.MetaData(
        information = "Information",
        symbol = "AAPL",
        lastRefreshed = "2024-01-02",
        outputSize = "Compact",
        timeZone = "US/Eastern"
    )
}
