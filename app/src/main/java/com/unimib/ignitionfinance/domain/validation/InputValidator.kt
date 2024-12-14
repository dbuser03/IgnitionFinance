package com.unimib.ignitionfinance.domain.validation

import com.unimib.ignitionfinance.domain.validation.rules.InputValidationRules

object InputValidator {
    fun validate(value: String?, prefix: String): InputValidationResult {
        if (value.isNullOrBlank()) return InputValidationResult.Success

        return when {
            !InputValidationRules.validateInput(value, prefix) -> {
                when (prefix) {
                    "€" -> InputValidationResult.Failure("Input should be greater than 0 €")
                    "%" -> InputValidationResult.Failure("Input should be between 0 and 100 %")
                    "YRS" -> InputValidationResult.Failure("Input should be < 100 YRS")
                    "N°" -> InputValidationResult.Failure("Input should be between 1 and 10000 N°")
                    else -> InputValidationResult.Failure("Invalid input for prefix $prefix")
                }
            }
            else -> InputValidationResult.Success
        }
    }
}

sealed class InputValidationResult {
    data object Success : InputValidationResult()
    data class Failure(val message: String) : InputValidationResult()
}
