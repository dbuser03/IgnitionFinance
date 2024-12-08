package com.unimib.ignitionfinance.domain.validation

import android.util.Patterns

object LoginValidationRules {

    fun validateLoginEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validateLoginPassword(password: String): Boolean {
        return password.isNotBlank() &&
                password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() } &&
                password.any { it in "@#\$%^&?!+= " }
    }

    fun validateLoginForm(email: String?, password: String?): Boolean {
        return !email.isNullOrBlank() &&
                !password.isNullOrBlank() &&
                validateLoginEmail(email) &&
                validateLoginPassword(password)
    }
}
