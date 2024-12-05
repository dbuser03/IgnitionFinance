package com.unimib.ignitionfinance.domain.validation

object RegistrationValidator {
    fun validateName(name: String?): InputValidationResult {
        return when {
            name.isNullOrBlank() -> InputValidationResult.Failure("Name cannot be empty")
            !RegistrationValidationRules.validateName(name) -> InputValidationResult.Failure("Name should be at least 2 characters and not contain numbers")
            else -> InputValidationResult.Success
        }
    }

    fun validateSurname(surname: String?): InputValidationResult {
        return when {
            surname.isNullOrBlank() -> InputValidationResult.Failure("Surname cannot be empty")
            !RegistrationValidationRules.validateSurname(surname) -> InputValidationResult.Failure("Surname should be at least 2 characters and not contain numbers")
            else -> InputValidationResult.Success
        }
    }
}

sealed class ValidationResult {
    data object Success : InputValidationResult()
    data class Failure(val message: String) : InputValidationResult()
}
