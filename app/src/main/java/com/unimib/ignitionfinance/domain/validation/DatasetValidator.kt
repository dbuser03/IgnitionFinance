package com.unimib.ignitionfinance.domain.validation

import android.os.Build
import androidx.annotation.RequiresApi
import com.unimib.ignitionfinance.domain.validation.utils.ValidationErrors
import com.unimib.ignitionfinance.domain.validation.utils.ValidationRules
import com.unimib.ignitionfinance.data.model.StockData

object DatasetValidator {

    @RequiresApi(Build.VERSION_CODES.O)
    fun validate(stockDataList: List<Map<String, StockData>>): DatasetValidationResult {
        // Iterate over all the product data
        for (productData in stockDataList) {
            // Check if there are any entries in this product's data
            if (productData.isNotEmpty()) {
                var hasValidDate = false

                // Iterate over each data entry (key is the date)
                for (dateStr in productData.keys) {
                    // Validate the date string
                    if (!dateStr.isNullOrBlank() && ValidationRules.isDateOlderThan(dateStr, 10)) {
                        hasValidDate = true
                        break
                    }
                }

                // If we found at least one valid date, return success
                if (hasValidDate) {
                    return DatasetValidationResult.Success
                }
            }
        }

        // If no valid date found, return failure
        return DatasetValidationResult.Failure(ValidationErrors.Input.YEARS_LIMIT)
    }
}

sealed class DatasetValidationResult {
    object Success : DatasetValidationResult()
    data class Failure(val message: String) : DatasetValidationResult()
}
