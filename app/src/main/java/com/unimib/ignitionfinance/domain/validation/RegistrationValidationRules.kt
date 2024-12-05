package com.unimib.ignitionfinance.domain.validation

object RegistrationValidationRules {
    fun validateName(name: String): Boolean {
        val isValidLength = name.length >= 2
        val containsNumbers = name.any { it.isDigit() }
        return isValidLength && !containsNumbers
    }

    fun validateSurname(surname: String): Boolean {
        val isValidLength = surname.length >= 2
        val containsNumbers = surname.any { it.isDigit() }
        return isValidLength && !containsNumbers
    }

}
