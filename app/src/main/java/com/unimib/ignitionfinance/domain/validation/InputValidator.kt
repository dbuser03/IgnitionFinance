package com.unimib.ignitionfinance.domain.validation

object InputValidator {
    fun validate(value: String?, prefix: String): ValidationResult {
        if (value.isNullOrBlank()) return ValidationResult.Success

        return when {
            !InputBoxValidationRules.validateInput(value, prefix) -> {
                when (prefix) {
                    "€" -> ValidationResult.Failure("Input should be greater than 0 €")
                    "%" -> ValidationResult.Failure("Input should be between 0 and 100 %")
                    "YRS" -> ValidationResult.Failure("Input should be < 100 YRS")
                    "N°" -> ValidationResult.Failure("Input should be between 1 and 10000 N°")
                    else -> ValidationResult.Failure("Invalid input for prefix $prefix")
                }
            }
            else -> ValidationResult.Success
        }
    }
}

sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Failure(val message: String) : ValidationResult()
}
