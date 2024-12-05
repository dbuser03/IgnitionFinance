package com.unimib.ignitionfinance.domain.validation

object InputBoxValidationRules {
    fun validateInput(value: String?, prefix: String): Boolean {
        if (value.isNullOrBlank()) return true

        return when (prefix) {
            "€" -> validateEuro(value)
            "%" -> validatePercentage(value)
            "YRS" -> validateYears(value)
            "N°" -> validateSimulations(value)
            else -> false
        }
    }

    private fun validateEuro(value: String): Boolean {
        return value.toDoubleOrNull()?.let { it >= 0 } == true
    }

    private fun validatePercentage(value: String): Boolean {
        return value.toDoubleOrNull()?.let { it in 0.0..100.0 } == true
    }

    private fun validateYears(value: String): Boolean {
        return value.toIntOrNull()?.let { it < 100 } == true
    }

    private fun validateSimulations(value: String): Boolean {
        return value.toIntOrNull()?.let { it in 1..10000 } == true
    }
}