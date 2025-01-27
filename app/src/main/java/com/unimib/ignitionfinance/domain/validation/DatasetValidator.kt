package com.unimib.ignitionfinance.domain.validation

import android.os.Build
import androidx.annotation.RequiresApi
import com.unimib.ignitionfinance.domain.validation.utils.ValidationErrors
import com.unimib.ignitionfinance.domain.validation.utils.ValidationRules

object DatasetValidator {

    @RequiresApi(Build.VERSION_CODES.O)
    fun validate(stockData: Map<String, List<Map<String, Any>>>): DatasetValidationResult {
        for (productData in stockData.values) {
            if (productData.isNotEmpty()) {
                var hasValidDate = false

                for (data in productData) {
                    val dateStr = data["date"] as? String
                    if (!dateStr.isNullOrBlank()) {
                        if (ValidationRules.isDateOlderThan(dateStr, 10)) {
                            hasValidDate = true
                            break
                        }
                    }
                }
                if (!hasValidDate) {
                    return DatasetValidationResult.Failure(ValidationErrors.Input.YEARS_LIMIT)
                }
            }
        }

        return DatasetValidationResult.Success
    }
}

sealed class DatasetValidationResult {
    data object Success : DatasetValidationResult()
    data class Failure(val message: String) : DatasetValidationResult()
}

