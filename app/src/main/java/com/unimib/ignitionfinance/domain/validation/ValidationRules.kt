package com.unimib.ignitionfinance.domain.validation

import android.util.Patterns

object ValidationRules {
    // Validation Configurations
    private object Configs {
        val EMAIL_PATTERN = Patterns.EMAIL_ADDRESS

        val PASSWORD_RULES = PasswordRule(
            minLength = 8,
            requireUppercase = true,
            requireLowercase = true,
            requireDigit = true,
            specialCharacters = "@#\$%^&?!+=",
            allowSingleWordOnly = true
        )

        val NAME_RULES = NameRule(
            minLength = 3,
            allowSingleWordOnly = true
        )
    }

    data class PasswordRule(
        val minLength: Int,
        val requireUppercase: Boolean = false,
        val requireLowercase: Boolean = false,
        val requireDigit: Boolean = false,
        val specialCharacters: String = "",
        val allowSingleWordOnly: Boolean = false
    )

    data class NameRule(
        val minLength: Int,
        val allowSingleWordOnly: Boolean = false
    )

    private fun String.isSingleWord(): Boolean = this.trim().split(" ").size == 1

    private fun String.validatePassword(rules: PasswordRule): Boolean {
        if (this.isBlank()) return false

        return (this.length >= rules.minLength) &&
                (!rules.requireUppercase || this.any { it.isUpperCase() }) &&
                (!rules.requireLowercase || this.any { it.isLowerCase() }) &&
                (!rules.requireDigit || this.any { it.isDigit() }) &&
                (!rules.specialCharacters.isNotEmpty() || this.any { it in rules.specialCharacters }) &&
                (!rules.allowSingleWordOnly || this.isSingleWord())
    }

    private fun String.validateName(rules: NameRule): Boolean {
        if (this.isBlank()) return false

        return (this.length >= rules.minLength) &&
                (!rules.allowSingleWordOnly || this.isSingleWord())
    }

    fun validateName(name: String): Boolean {
        return name.validateName(Configs.NAME_RULES)
    }

    fun validatePassword(password: String): Boolean {
        return password.validatePassword(Configs.PASSWORD_RULES)
    }

    fun validateInput(value: String?, prefix: String): Boolean {
        if (value.isNullOrBlank()) return true

        return when (prefix) {
            "€" -> value.toDoubleOrNull()?.let { it >= 0 } == true
            "%" -> value.toDoubleOrNull()?.let { it in 0.0..100.0 } == true
            "YRS" -> value.toIntOrNull()?.let { it < 100 } == true
            "N°" -> value.toIntOrNull()?.let { it in 1..10000 } == true
            else -> false
        }
    }

    fun validateEmail(email: String, requireSingleWord: Boolean = false): Boolean {
        return email.isNotBlank() &&
                Configs.EMAIL_PATTERN.matcher(email).matches() &&
                (!requireSingleWord || email.isSingleWord())
    }

    fun validateLoginForm(email: String?, password: String?): Boolean {
        return !email.isNullOrBlank() &&
                !password.isNullOrBlank() &&
                validateEmail(email) &&
                password.validatePassword(Configs.PASSWORD_RULES)
    }

    fun validateRegistrationForm(
        name: String?,
        surname: String?,
        email: String?,
        password: String?
    ): Boolean {
        return !name.isNullOrBlank() &&
                !surname.isNullOrBlank() &&
                !email.isNullOrBlank() &&
                !password.isNullOrBlank() &&
                name.validateName(Configs.NAME_RULES) &&
                surname.validateName(Configs.NAME_RULES) &&
                validateEmail(email, requireSingleWord = true) &&
                password.validatePassword(Configs.PASSWORD_RULES)
    }

    fun validateResetForm(email: String?): Boolean {
        return !email.isNullOrBlank() && validateEmail(email)
    }
}