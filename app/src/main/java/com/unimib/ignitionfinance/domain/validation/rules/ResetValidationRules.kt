package com.unimib.ignitionfinance.domain.validation.rules

import android.util.Patterns

object ResetValidationRules {

    fun validateResetEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validateResetForm(email: String?): Boolean {
        return !email.isNullOrBlank() &&
                validateResetEmail(email)
    }
}