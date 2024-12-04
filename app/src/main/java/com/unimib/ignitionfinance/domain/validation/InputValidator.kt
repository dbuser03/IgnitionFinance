package com.unimib.ignitionfinance.domain.validation

object InputValidator {

    // Metodo che usa InputBoxValidationRules per validare l'input
    fun validate(value: String, prefix: String): ValidationResult {
        return when {
            !InputBoxValidationRules.validateInput(value, prefix) -> {
                // Se la validazione fallisce, restituisci un errore specifico per il prefisso
                when (prefix) {
                    "€" -> ValidationResult.Failure("Input should be greater than 0 €")
                    "%" -> ValidationResult.Failure("Input should be between 0 and 100 %")
                    "YRS" -> ValidationResult.Failure("Input should be < 100 YRS")
                    else -> ValidationResult.Failure("Invalid input for prefix $prefix")
                }
            }
            else -> ValidationResult.Success
        }
    }
}


// Risultato della validazione
sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Failure(val message: String) : ValidationResult()
}
