package com.unimib.ignitionfinance.data.remote.datasetTest

import com.unimib.ignitionfinance.domain.validation.DatasetValidationResult
import com.unimib.ignitionfinance.domain.validation.DatasetValidator
import com.unimib.ignitionfinance.domain.validation.utils.ValidationErrors
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DatasetValidatorTest {

    @Test
    fun `validate dataset with insufficient historical data returns failure`() {
        // Prepare test data with only recent dates (less than 10 years old)
        val currentDate = LocalDate.now()
        val recentData = mapOf(
            "IBM" to listOf(
                mapOf(
                    "date" to currentDate.minusYears(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    "open" to "100.0",
                    "close" to "110.0"
                )
            )
        )

        val result = DatasetValidator.validate(recentData)
        assertTrue(result is DatasetValidationResult.Failure)
        assertEquals(ValidationErrors.Input.YEARS_LIMIT, (result as DatasetValidationResult.Failure).message)
    }

    @Test
    fun `validate dataset with data older than 10 years returns success`() {
        val currentDate = LocalDate.now()
        val historicalData = mapOf(
            "IBM" to listOf(
                mapOf(
                    "date" to currentDate.minusYears(15).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    "open" to "100.0",
                    "close" to "110.0"
                ),
                mapOf(
                    "date" to currentDate.minusYears(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    "open" to "200.0",
                    "close" to "210.0"
                )
            )
        )

        val result = DatasetValidator.validate(historicalData)
        assertTrue(result is DatasetValidationResult.Success)
    }

    @Test
    fun `validate empty dataset returns success`() {
        val emptyData = mapOf<String, List<Map<String, Any>>>()
        val result = DatasetValidator.validate(emptyData)
        assertTrue(result is DatasetValidationResult.Success)
    }

    @Test
    fun `validate dataset with multiple products returns correct result`() {
        val currentDate = LocalDate.now()
        val multiProductData = mapOf(
            "IBM" to listOf(
                mapOf(
                    "date" to currentDate.minusYears(15).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    "open" to "100.0",
                    "close" to "110.0"
                )
            ),
            "APPLE" to listOf(
                mapOf(
                    "date" to currentDate.minusYears(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    "open" to "200.0",
                    "close" to "210.0"
                )
            )
        )

        val result = DatasetValidator.validate(multiProductData)
        assertTrue(result is DatasetValidationResult.Failure)
        assertEquals(ValidationErrors.Input.YEARS_LIMIT, (result as DatasetValidationResult.Failure).message)
    }
}