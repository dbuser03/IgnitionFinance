package com.unimib.ignitionfinance.domain.validation.rules

import android.util.Patterns

object RegistrationValidationRules {

    private fun isSingleWord(input: String): Boolean {
        return input.trim().split(" ").size == 1
    }

    fun validateName(name: String): Boolean {
        return name.isNotBlank() && name.length >= 3 && isSingleWord(name)
    }

    fun validateSurname(surname: String): Boolean {
        return surname.isNotBlank() && surname.length >= 3 && isSingleWord(surname)
    }

    fun validateEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches() && isSingleWord(email)
    }

    fun validatePassword(password: String): Boolean {
        return password.isNotBlank() &&
                password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() } &&
                password.any { it in "@#\$%^&?!+= " } &&
                isSingleWord(password)
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
