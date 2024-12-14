package com.unimib.ignitionfinance.domain.validation

import com.unimib.ignitionfinance.domain.validation.rules.LoginValidationRules

object LoginValidator {

    fun validateEmail(email: String?): LoginValidationResult {
        return when {
            email.isNullOrBlank() -> LoginValidationResult.Success
            !LoginValidationRules.validateLoginEmail(email) -> LoginValidationResult.Failure("Email should be valid and follow the standard format (e.g., user@example.com)")
            else -> LoginValidationResult.Success
        }
    }

    fun validatePassword(password: String?): LoginValidationResult {
        return when {
            password.isNullOrBlank() -> LoginValidationResult.Success
            !LoginValidationRules.validateLoginPassword(password) -> LoginValidationResult.Failure("Password must be at least 8 characters long, include at least one uppercase letter, one lowercase letter, one number, and one special character (e.g., @, !, ?, #, \$, etc.).\"")
            else -> LoginValidationResult.Success
        }
    }

    fun validateLoginForm(email: String?, password: String?): LoginValidationResult {
        return when {
            !LoginValidationRules.validateLoginForm(email, password) -> LoginValidationResult.Failure("")
            else -> LoginValidationResult.Success

        }
    }

}

sealed class LoginValidationResult {
    data object Success : LoginValidationResult()
    data class Failure(val message: String) : LoginValidationResult()
}

