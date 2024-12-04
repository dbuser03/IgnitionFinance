package com.unimib.ignitionfinance.domain.validation

object InputBoxValidationRules {
    fun validateInput(value: String, prefix: String): Boolean {
        return when (prefix) {
            "€" -> validateEuro(value)
            "%" -> validatePercentage(value)
            "YRS" -> validateYears(value)
            else -> false
        }
    }

    private fun validateEuro(value: String): Boolean {
        return value.toDoubleOrNull()?.let { it >= 0 } ?: false
    }

    private fun validatePercentage(value: String): Boolean {
        return value.toDoubleOrNull()?.let { it in 0.0..100.0 } ?: false
    }

    private fun validateYears(value: String): Boolean {
        return value.toDoubleOrNull()?.let { it <= 100 } ?: false
    }
}