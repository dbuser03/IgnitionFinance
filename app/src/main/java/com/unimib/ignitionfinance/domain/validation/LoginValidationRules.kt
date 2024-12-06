package com.unimib.ignitionfinance.domain.validation

object LoginValidationRules {

    fun validateLoginEmail(email: String): Boolean {
        if (email.isBlank()) return true
        val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }

    fun validateLoginPassword(password: String): Boolean {
        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&?!+=]).{8,}$".toRegex()
        return passwordRegex.matches(password)
    }

    fun validateLoginForm(email: String?, password: String?): Boolean {
        return !email.isNullOrBlank() &&
                !password.isNullOrBlank() &&
                validateLoginEmail(email) &&
                validateLoginPassword(password)
    }
}
