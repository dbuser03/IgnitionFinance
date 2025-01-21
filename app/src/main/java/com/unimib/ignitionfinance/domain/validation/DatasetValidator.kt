package com.unimib.ignitionfinance.domain.validation


import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DatasetValidator {
    fun validate(historicalData: Map<String, List<Map<String, Any>>>): DatasetValidationResult {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val currentDate = LocalDate.now()

        for (productData in historicalData.values) {
            if (productData.isNotEmpty()) {
                val firstDate = LocalDate.parse(productData.first()["date"] as String, formatter)
                if (firstDate.isBefore(currentDate.minusYears(10))) {
                    return DatasetValidationResult.Success
                }
            }
        }
        return DatasetValidationResult.Failure("No product with more than 10 years of historical data found")
    }
}

sealed class DatasetValidationResult {
    object Success : DatasetValidationResult()
    data class Failure(val message: String) : DatasetValidationResult()
}