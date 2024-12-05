package com.unimib.ignitionfinance.domain.validation

object RegistrationValidator {
    fun validateName(name: String?): RegistrationValidationResult {
        return when {
            name.isNullOrBlank() -> RegistrationValidationResult.Failure("Name cannot be empty")
            !RegistrationValidationRules.validateName(name) -> RegistrationValidationResult.Failure("Name should be at least 2 characters and not contain numbers")
            else -> RegistrationValidationResult.Success
        }
    }

    fun validateSurname(surname: String?): RegistrationValidationResult {
        return when {
            surname.isNullOrBlank() -> RegistrationValidationResult.Failure("Surname cannot be empty")
            !RegistrationValidationRules.validateSurname(surname) -> RegistrationValidationResult.Failure("Surname should be at least 2 characters and not contain numbers")
            else -> RegistrationValidationResult.Success
        }
    }
}

sealed class RegistrationValidationResult {
    data object Success : RegistrationValidationResult()
    data class Failure(val message: String) : RegistrationValidationResult()
}
