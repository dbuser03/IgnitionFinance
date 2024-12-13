package com.unimib.ignitionfinance.domain.validation


object ResetValidator {

    fun validateEmail(email: String?): ResetValidationResult {
        return when {
            email.isNullOrBlank() -> ResetValidationResult.Success
            !ResetValidationRules.validateResetEmail(email) -> ResetValidationResult.Failure("Email should be valid and follow the standard format (e.g., user@example.com)")
            else -> ResetValidationResult.Success
        }
    }

    fun validateResetForm(email: String?): ResetValidationResult {
        return when {
            !ResetValidationRules.validateResetForm(email) -> ResetValidationResult.Failure("")
            else -> ResetValidationResult.Success

        }
    }
}

sealed class ResetValidationResult {
    data object Success : ResetValidationResult()
    data class Failure(val message: String) : ResetValidationResult()
}