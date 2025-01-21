package com.unimib.ignitionfinance.domain.validation

import android.os.Build
import androidx.annotation.RequiresApi
import com.unimib.ignitionfinance.domain.validation.utils.ValidationErrors
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DatasetValidator {

    @RequiresApi(Build.VERSION_CODES.O)
    fun validate(historicalData: Map<String, List<Map<String, Any>>>): DatasetValidationResult {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val currentDate = LocalDate.now()

        for (productData in historicalData.values) {
            if (productData.isNotEmpty()) {
                val firstDateStr = productData.first()["date"] as? String
                if (firstDateStr.isNullOrBlank()) {
                    return DatasetValidationResult.Failure(ValidationErrors.Input.INVALID_INPUT.format("date"))
                }

                try {
                    val firstDate = LocalDate.parse(firstDateStr, formatter)
                    if (firstDate.isBefore(currentDate.minusYears(10))) {
                        return DatasetValidationResult.Success
                    }
                } catch (e: Exception) {
                    return DatasetValidationResult.Failure(ValidationErrors.Input.INVALID_INPUT.format("date"))
                }
            }
        }
        return DatasetValidationResult.Failure(ValidationErrors.Input.YEARS_LIMIT)
    }
}

sealed class DatasetValidationResult {
    object Success : DatasetValidationResult()
    data class Failure(val message: String) : DatasetValidationResult()
}
