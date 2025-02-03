package com.unimib.ignitionfinance.domain.validation

import android.os.Build
import androidx.annotation.RequiresApi
import com.unimib.ignitionfinance.domain.validation.utils.ValidationErrors
import com.unimib.ignitionfinance.domain.validation.utils.ValidationRules
import com.unimib.ignitionfinance.data.model.StockData

object DatasetValidator {

    @RequiresApi(Build.VERSION_CODES.O)
    fun validate(stockDataList: List<Pair<String, Map<String, StockData>>>): DatasetValidationResult {
        // Itera su ciascuna coppia (currency, dati storici) della lista
        for ((_, productData) in stockDataList) {
            // Controlla se per questo prodotto ci sono dati
            if (productData.isNotEmpty()) {
                var hasValidDate = false

                // Itera su ciascuna chiave della mappa, dove la chiave è la data
                for (dateStr in productData.keys) {
                    // Valida la stringa della data (es. se non è vuota e se rispetta il limite di anni)
                    if (dateStr.isNotBlank() && ValidationRules.isDateOlderThan(dateStr, 10)) {
                        hasValidDate = true
                        break
                    }
                }

                // Se per almeno un prodotto abbiamo trovato una data valida, la validazione ha successo
                if (hasValidDate) {
                    return DatasetValidationResult.Success
                }
            }
        }

        // Se nessun prodotto ha una data valida, ritorna il fallimento con il messaggio d'errore
        return DatasetValidationResult.Failure(ValidationErrors.Input.YEARS_LIMIT)
    }
}

sealed class DatasetValidationResult {
    object Success : DatasetValidationResult()
    data class Failure(val message: String) : DatasetValidationResult()
}
