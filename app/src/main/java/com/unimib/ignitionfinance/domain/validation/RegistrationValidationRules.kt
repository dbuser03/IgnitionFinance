package com.unimib.ignitionfinance.domain.validation

import android.util.Patterns

object RegistrationValidationRules {

    fun validateName(name: String): Boolean {
        return name.isNotBlank() && name.length >= 3
    }

    fun validateSurname(surname: String): Boolean {
        return surname.isNotBlank() && surname.length >= 3
    }

    fun validateEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validatePassword(password: String): Boolean {
        return password.isNotBlank() &&
                password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() } &&
                password.any { it in "@#\$%^&?!+= " }
    }

    fun validateRegistrationForm(name: String?, surname: String?, email: String?, password: String?): Boolean {
        return !name.isNullOrBlank() &&
                !surname.isNullOrBlank() &&
                !email.isNullOrBlank() &&
                !password.isNullOrBlank() &&
                validateName(name) &&
                validateSurname(surname) &&
                validateEmail(email) &&
                validatePassword(password)
    }
}

