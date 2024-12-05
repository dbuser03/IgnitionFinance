package com.unimib.ignitionfinance.domain.validation

import java.util.Calendar

object RegistrationValidationRules {

    fun validateName(name: String): Boolean {
        if (name.isBlank()) return true
        val nameRegex = "^[a-zA-Z]{3,}$".toRegex()
        return nameRegex.matches(name)
    }

    fun validateSurname(surname: String): Boolean {
        if (surname.isBlank()) return true
        val surnameRegex = "^[a-zA-Z]{3,}$".toRegex()
        return surnameRegex.matches(surname)
    }

    fun validateEmail(email: String): Boolean {
        if (email.isBlank()) return true
        val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }

    fun validatePassword(password: String): Boolean {
        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&?!+=]).{8,}$".toRegex()
        return passwordRegex.matches(password)
    }

}
