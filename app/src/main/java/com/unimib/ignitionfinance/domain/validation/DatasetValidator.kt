package com.unimib.ignitionfinance.domain.validation

import com.unimib.ignitionfinance.domain.validation.utils.ValidationErrors
import com.unimib.ignitionfinance.domain.validation.utils.ValidationRules
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DatasetValidator {

    fun validate(stockData: Map<String, List<Map<String, Any>>>): DatasetValidationResult {
        // Cicliamo su tutti i prodotti nel dataset
        for (productData in stockData.values) {
            if (productData.isNotEmpty()) {
                var hasValidDate = false

                // Cicliamo su tutte le date del prodotto (ogni giorno per ogni prodotto)
                for (data in productData) {
                    val dateStr = data["date"] as? String
                    if (!dateStr.isNullOrBlank()) {
                        // Verifica se la data è almeno 10 anni fa
                        if (ValidationRules.isDateOlderThan(dateStr, 10)) {
                            hasValidDate = true
                            break // Se troviamo una data valida, non c'è bisogno di controllare oltre per questo prodotto
                        }
                    }
                }

                // Se non c'è nessuna data valida per questo prodotto, restituiamo errore
                if (!hasValidDate) {
                    return DatasetValidationResult.Failure(ValidationErrors.Input.YEARS_LIMIT)
                }
            }
        }

        // Se tutti i prodotti hanno almeno una data valida, restituiamo successo
        return DatasetValidationResult.Success
    }
}

sealed class DatasetValidationResult {
    data object Success : DatasetValidationResult()
    data class Failure(val message: String) : DatasetValidationResult()
}

