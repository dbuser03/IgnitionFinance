package com.unimib.ignitionfinance.domain.validation

import com.unimib.ignitionfinance.data.model.StockData
import com.unimib.ignitionfinance.domain.validation.utils.ValidationErrors
import com.unimib.ignitionfinance.domain.validation.utils.ValidationRules
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DatasetValidator {

    fun validate(stockData: Map<String, List<Map<String, Any>>>): String {
        val currentDate = LocalDate.now()

        for (productData in stockData.values) {
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
