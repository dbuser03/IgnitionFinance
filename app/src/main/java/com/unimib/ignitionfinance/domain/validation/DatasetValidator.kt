package com.unimib.ignitionfinance.domain.validation

import com.unimib.ignitionfinance.domain.validation.utils.ValidationErrors
import com.unimib.ignitionfinance.domain.validation.utils.ValidationRules
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DatasetValidator {

    fun validate(historicalData: Map<String, List<Map<String, Any>>>): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val currentDate = LocalDate.now()

        for (productData in historicalData.values) {
            if (productData.isNotEmpty()) {
                val firstDateStr = productData.first()["date"] as? String
                if (firstDateStr.isNullOrBlank()) {
                    return ValidationErrors.Input.INVALID_INPUT.format("date")
                }

                // Use the new validation rule to check for the 10-year threshold
                if (ValidationRules.isDateOlderThan(firstDateStr, 10)) {
                    return "Validation successful"
                }
            }
        }
        return ValidationErrors.Input.YEARS_LIMIT
    }
}
