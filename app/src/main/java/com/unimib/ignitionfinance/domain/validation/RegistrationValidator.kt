package com.unimib.ignitionfinance.domain.validation

import com.unimib.ignitionfinance.domain.validation.rules.RegistrationValidationRules

object RegistrationValidator {
    fun validateName(name: String?): RegistrationValidationResult {
        return when {
            name.isNullOrBlank() -> RegistrationValidationResult.Success
            !RegistrationValidationRules.validateName(name) -> RegistrationValidationResult.Failure("Name should be at least 2 characters and not contain numbers")
            else -> RegistrationValidationResult.Success
        }
    }

    fun validateSurname(surname: String?): RegistrationValidationResult {
        return when {
            surname.isNullOrBlank() -> RegistrationValidationResult.Success
            !RegistrationValidationRules.validateSurname(surname) -> RegistrationValidationResult.Failure("Surname should be at least 2 characters and not contain numbers")
            else -> RegistrationValidationResult.Success
        }
    }

    fun validateEmail(email: String?): RegistrationValidationResult {
        return when {
            email.isNullOrBlank() -> RegistrationValidationResult.Success
            !RegistrationValidationRules.validateEmail(email) -> RegistrationValidationResult.Failure("Email should be valid and follow the standard format (e.g., user@example.com)")
            else -> RegistrationValidationResult.Success
        }
    }

    fun validatePassword(password: String?): RegistrationValidationResult {
        return when {
            password.isNullOrBlank() -> RegistrationValidationResult.Success
            !RegistrationValidationRules.validatePassword(password) -> RegistrationValidationResult.Failure("Password must be at least 8 characters long, include at least one uppercase letter, one lowercase letter, one number, and one special character (e.g., @, !, ?, #, \$, etc.).\"")
            else -> RegistrationValidationResult.Success
        }
    }

    fun validateRegistrationForm(name: String?, surname: String?, email: String?, password: String?): RegistrationValidationResult {
        return when {
            !RegistrationValidationRules.validateRegistrationForm(name, surname, email, password) -> RegistrationValidationResult.Failure("")
            else -> RegistrationValidationResult.Success

        }
    }

}

sealed class RegistrationValidationResult {
    data object Success : RegistrationValidationResult()
    data class Failure(val message: String) : RegistrationValidationResult()
}

